package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.*;
import es.upm.miw.betca_tpv_spring.dtos.TicketCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketOutputDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketPatchDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketSearchDto;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Controller
public class TicketController {

    private ArticleReactRepository articleReactRepository;
    private TicketReactRepository ticketReactRepository;
    private UserReactRepository userReactRepository;
    private CashierClosureReactRepository cashierClosureReactRepository;
    private TagReactRepository tagReactRepository;
    private PdfService pdfService;
    private CustomerPointsReactRepository customerPointsReactRepository;
    private static final Integer EACH_TWO_UNIT_ONE_POINT = 2;
    private OrderReactRepository orderReactRepository;
    private GiftTicketReactRepository giftTicketReactRepository;

    @Autowired
    public TicketController(TicketReactRepository ticketReactRepository, UserReactRepository userReactRepository,
                            ArticleReactRepository articleReactRepository, CashierClosureReactRepository cashierClosureReactRepository,
                            PdfService pdfService, CustomerPointsReactRepository customerPointsReactRepository,
                            OrderReactRepository orderReactRepository, TagReactRepository tagReactRepository,
                            GiftTicketReactRepository giftTicketReactRepository) {
        this.ticketReactRepository = ticketReactRepository;
        this.userReactRepository = userReactRepository;
        this.articleReactRepository = articleReactRepository;
        this.cashierClosureReactRepository = cashierClosureReactRepository;
        this.pdfService = pdfService;
        this.customerPointsReactRepository = customerPointsReactRepository;
        this.orderReactRepository = orderReactRepository;
        this.tagReactRepository = tagReactRepository;
        this.giftTicketReactRepository = giftTicketReactRepository;
    }

    private Mono<Integer> nextIdStartingDaily() {
        return ticketReactRepository.findFirstByOrderByCreationDateDescIdDesc()
                .map(ticket -> {
                    if (ticket.getCreationDate().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))) {
                        return ticket.simpleId() + 1;
                    } else {
                        return 1;
                    }
                })
                .switchIfEmpty(Mono.just(1));
    }

    private Mono<Void> updateArticlesStockAssured(TicketCreationInputDto ticketCreationDto) {
        List<Mono<Article>> articlePublishers = ticketCreationDto.getShoppingCart().stream()
                .map(shoppingDto -> this.articleReactRepository.findById(shoppingDto.getCode())
                        .switchIfEmpty(Mono.error(new NotFoundException("Article (" + shoppingDto.getCode() + ")")))
                        .map(article -> {
                            article.setStock(article.getStock() - shoppingDto.getAmount());
                            return article;
                        })
                        .flatMap(article -> articleReactRepository.save(article))
                ).collect(Collectors.toList());
        return Mono.when(articlePublishers);
    }

    public Mono<Ticket> createTicket(TicketCreationInputDto ticketCreationDto) {
        Shopping[] shoppingArray = ticketCreationDto.getShoppingCart().stream().map(shoppingDto ->
                new Shopping(shoppingDto.getAmount(), shoppingDto.getDiscount(),
                        shoppingDto.isCommitted() ? ShoppingState.COMMITTED : ShoppingState.NOT_COMMITTED,
                        shoppingDto.getCode(), shoppingDto.getDescription(), shoppingDto.getRetailPrice()))
                .toArray(Shopping[]::new);
        Ticket ticket = new Ticket(0, ticketCreationDto.getCard(), ticketCreationDto.getCash(),
                ticketCreationDto.getVoucher(), shoppingArray, null,
                ticketCreationDto.getNote(), null);
        Mono<User> user = this.userReactRepository.findByMobile(ticketCreationDto.getUserMobile())
                .doOnNext(ticket::setUser);
        Mono<Integer> nextId = this.nextIdStartingDaily()
                .doOnNext(ticket::setId);
        Mono<CashierClosure> cashierClosureReact = this.cashierClosureReactRepository.findFirstByOrderByOpeningDateDesc()
                .map(cashierClosure -> {
                    cashierClosure.voucher(ticketCreationDto.getVoucher());
                    cashierClosure.cash(ticketCreationDto.getCash());
                    cashierClosure.card(ticketCreationDto.getCard());
                    return cashierClosure;
                });
        Mono<Void> cashierClosureUpdate = this.cashierClosureReactRepository.saveAll(cashierClosureReact).then();

        Mono<CustomerPoints> customerPoints = this.customerPointsReactRepository.findByUser(user).map(customerPoints1 -> {
            int points = (customerPoints1.getPoints() + (ticket.getTotal().intValue() / EACH_TWO_UNIT_ONE_POINT));
            customerPoints1.setPoints(points < 0 ? 0 : points);
            return customerPoints1;
        }).doOnNext(cp -> ticket.setCustomerPoints(cp));

        Mono<Void> customerPointsUpdate = this.customerPointsReactRepository.saveAll(customerPoints).then();

        return Mono.when(user, nextId, updateArticlesStockAssured(ticketCreationDto), customerPointsUpdate, cashierClosureUpdate)
                .then(this.ticketReactRepository.save(ticket));
    }

    @Transactional
    public Mono<byte[]> createTicketAndPdf(TicketCreationInputDto ticketCreationDto) {
        return pdfService.generateTicket(this.createTicket(ticketCreationDto));
    }

    public Flux<TicketOutputDto> readAll() {
        return this.ticketReactRepository.findAllTickets();
    }

    public Mono<byte[]> getTicketPdf(String ticketId) throws IOException {
        Mono<Ticket> ticket = this.ticketReactRepository.findById(ticketId)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket " + ticketId + " not found")));
        return pdfService.generateTicket(ticket);
    }

    public Mono<TicketOutputDto> getTicket(String id) {
        return this.ticketReactRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket " + id + " not found")))
                .map(TicketOutputDto::new);
    }

    public Mono<TicketOutputDto> searchByGiftTicketReference(String giftTicketReference) {
        return this.giftTicketReactRepository.findById(giftTicketReference)
                .switchIfEmpty(Mono.error(new NotFoundException("GiftTicket " + giftTicketReference + " not found")))
                .map(giftTicket -> new TicketOutputDto(giftTicket.getTicket()));
    }

    public Flux<TicketOutputDto> searchByMobileDateOrAmount(TicketSearchDto ticketSearchDto) {
        Flux<Ticket> ticketFlux = ticketSearchDto.getDate() == null ?
                ticketReactRepository.findAll() :
                ticketReactRepository.findByCreationDateBetween(ticketSearchDto.getDate(), ticketSearchDto.getDate().plusDays(1));
        if (ticketSearchDto.getMobile() != null) {
            ticketFlux = ticketFlux.filter(ticket -> {
                User user = ticket.getUser();
                return user != null && user.getMobile().equals(ticketSearchDto.getMobile());
            });
        }
        if (ticketSearchDto.getAmount() != null) {
            ticketFlux = ticketFlux.filter(ticket -> {
                AtomicReference<Integer> numberOfItems = new AtomicReference<>(0);
                Arrays.stream(ticket.getShoppingList()).forEach(item -> {
                    numberOfItems.updateAndGet(v -> v + item.getAmount());
                });
                return numberOfItems.get().equals(ticketSearchDto.getAmount());
            });
        }
        return ticketFlux.distinct()
                .map(ticket -> new TicketOutputDto(ticket.getId(), ticket.getReference()));
    }

    public Flux<TicketOutputDto> searchNotCommittedByArticle(String articleId) {
        return this.ticketReactRepository.findNotCommittedByArticleId(articleId)
                .distinct()
                .map(ticket -> new TicketOutputDto(ticket.getId(), ticket.getReference()));
    }

   public Flux<TicketOutputDto> searchNotCommittedByOrder(String orderId) {
       List<Flux<Ticket>> tickets = new ArrayList<>();
       return this.orderReactRepository.findById(orderId)
               .map(order -> Arrays.stream(order.getOrderLines()).map(orderLine -> {
                   Article article = orderLine.getArticle();
                   tickets.add(this.ticketReactRepository.findNotCommittedByArticleId(article.getCode()));
                   return article;
               }).collect(Collectors.toList()))
               .thenMany(Flux.merge(tickets))
               .distinct()
               .map(ticket -> new TicketOutputDto(ticket.getId(), ticket.getReference()));
   }

   public Flux<TicketOutputDto> searchNotCommittedByTag(String tagDescription) {
        List<Flux<Ticket>> tickets = new ArrayList<>();
        return this.tagReactRepository.findByDescription(tagDescription)
                .map(tag -> Arrays.stream(tag.getArticleList()).map(article -> {
                    tickets.add(ticketReactRepository.findNotCommittedByArticleId(article.getCode()));
                    return tag;
                }).collect(Collectors.toList()))
                .thenMany(Flux.merge(tickets))
                .distinct()
                .map(ticket -> new TicketOutputDto(ticket.getId(), ticket.getReference()));
   }

    public Mono<TicketOutputDto> updateShoppingTicket(String id, TicketPatchDto shoppingPatchDto) {
        Mono<Ticket> ticket = this.ticketReactRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket " + id + " not found"))).map(ticket1 -> {
                    Arrays.stream(ticket1.getShoppingList()).forEach(shopping -> {
                        shoppingPatchDto.getShoppingPatchDtoList().stream().forEach(shoppingPatchDto1 -> {
                            if (shopping.getArticleId().equals(shoppingPatchDto1.getArticleId())) {
                                shopping.setAmount(shoppingPatchDto1.getAmount());
                                shopping.setShoppingState(shoppingPatchDto1.getShoppingState());
                            }
                        });
                    });
                    return ticket1;
                });
        return Mono.when(ticket).then(this.ticketReactRepository.saveAll(ticket).next().map(TicketOutputDto::new));
    }
}
