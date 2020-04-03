package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.*;
import es.upm.miw.betca_tpv_spring.dtos.TicketCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketOutputDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketSearchDto;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.exceptions.PdfException;
import es.upm.miw.betca_tpv_spring.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private PdfService pdfService;
    private CustomerPointsReactRepository customerPointsReactRepository;
    private static final Integer EACH_TWO_UNIT_ONE_POINT = 2;
    private OrderRepository orderRepository;

    @Autowired
    public TicketController(TicketReactRepository ticketReactRepository, UserReactRepository userReactRepository,
                            ArticleReactRepository articleReactRepository, CashierClosureReactRepository cashierClosureReactRepository,
                            PdfService pdfService, CustomerPointsReactRepository customerPointsReactRepository,
                            OrderRepository orderRepository) {
        this.ticketReactRepository = ticketReactRepository;
        this.userReactRepository = userReactRepository;
        this.articleReactRepository = articleReactRepository;
        this.cashierClosureReactRepository = cashierClosureReactRepository;
        this.pdfService = pdfService;
        this.customerPointsReactRepository = customerPointsReactRepository;
        this.orderRepository = orderRepository;
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


    public byte[] getPdf(String id) {
        try {
            String path = "/tpv-pdfs/tickets/ticket-" + id;
            String USER_HOME = "user.home";
            String PDF_FILE_EXT = ".pdf";
            return Files.readAllBytes(new File(System.getProperty(USER_HOME) + path + PDF_FILE_EXT).toPath());
        } catch (IOException ioe) {
            throw new PdfException("Can’t read PDF");
        }
    }

    public Mono<Ticket> getTicket(String id) {
        return this.ticketReactRepository.findById(id);
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
        return ticketFlux.map(ticket -> new TicketOutputDto(ticket.getId(), ticket.getReference()));
    }

    public Flux<TicketOutputDto> searchNotCommittedByArticle(String articleId) {
        return this.ticketReactRepository.findNotCommittedByArticleId(articleId)
                .map(ticket -> new TicketOutputDto(ticket.getId(), ticket.getReference()));
    }

   public Flux<TicketOutputDto> searchNotCommittedByOrder(String orderId) {
       List<Flux<Ticket>> fluxes = new ArrayList<>();
       this.orderRepository.findById(orderId).map(order -> Arrays.asList(order.getOrderLines())).ifPresent(orderLines -> {
           orderLines.forEach(orderLine ->  {
               String articleId = orderLine.getArticle().getCode();
               Flux<Ticket> tickets = this.ticketReactRepository.findNotCommittedByArticleId(articleId);
               fluxes.add(tickets);
           });
       });
       return Flux.merge(fluxes).map(ticket -> new TicketOutputDto(ticket.getId(), ticket.getReference()));
   }
}
