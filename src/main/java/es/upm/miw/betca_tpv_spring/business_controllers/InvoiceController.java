package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.*;
import es.upm.miw.betca_tpv_spring.dtos.InvoiceNegativeCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.InvoiceOutputDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.InvoiceReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.TicketReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class InvoiceController {

    private PdfService pdfService;
    private InvoiceReactRepository invoiceReactRepository;
    private TicketReactRepository ticketReactRepository;
    private ArticleReactRepository articleReactRepository;

    @Autowired
    public InvoiceController(PdfService pdfService,
                             InvoiceReactRepository invoiceReactRepository,
                             TicketReactRepository ticketReactRepository,
                             ArticleReactRepository articleReactRepository) {
        this.pdfService = pdfService;
        this.invoiceReactRepository = invoiceReactRepository;
        this.ticketReactRepository = ticketReactRepository;
        this.articleReactRepository = articleReactRepository;
    }

    private Mono<Invoice> createInvoice() {
        Invoice invoice = new Invoice(0, null, null);
        Mono<Ticket> ticketPublisher = ticketReactRepository.findFirstByOrderByCreationDateDescIdDesc()
                .switchIfEmpty(Mono.error(new NotFoundException("Last Ticket not found")))
                .doOnNext(ticket -> {
                    invoice.setTicket(ticket);
                    invoice.setUser(ticket.getUser());
                })
                .handle((ticket, synchronousSink) -> {
                    User user = ticket.getUser();
                    if (ticket.getUser() == null || isNotUserCompletedForInvoice(user))
                        synchronousSink.error(new BadRequestException("User not completed"));
                    else if (ticket.isDebt())
                        synchronousSink.error(new BadRequestException("Ticket is debt"));
                    else
                        synchronousSink.next(ticket);
                });
        Mono<Integer> nextId = this.nextIdStartingYearly()
                .map(id -> {
                    invoice.setId(id);
                    return id;
                });
        Mono<Invoice> calculateBaseAndTaxPublisher = this.calculateBaseAndTax(invoice, ticketPublisher);
        return Mono.when(calculateBaseAndTaxPublisher, nextId)
                .then(invoiceReactRepository.save(invoice));
    }


    private boolean isNotUserCompletedForInvoice(User user) {
        return (user.getUsername() == null || user.getUsername().trim().equals(""))
                || (user.getAddress() == null || user.getAddress().trim().equals(""))
                || (user.getDni() == null || user.getDni().trim().equals(""));
    }

    private Mono<Invoice> calculateBaseAndTax(Invoice invoice, Mono<Ticket> ticketPublisher) {
        return ticketPublisher.flatMap(ticket -> calculateBaseAndTax(invoice, ticket.getShoppingList()));
    }


    private Mono<Integer> nextIdStartingYearly() {
        return invoiceReactRepository.findFirstByOrderByCreationDateDescIdDesc()
                .map(invoice -> {
                    if (invoice.getCreationDate().isAfter(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))) {
                        return invoice.simpleId() + 1;
                    } else {
                        return 1;
                    }
                })
                .switchIfEmpty(Mono.just(1));
    }

    @Transactional
    public Mono<byte[]> createAndPdf() {
        return pdfService.generateInvoice(this.createInvoice());
    }

    @Transactional
    public Mono<byte[]> updateAndPdf(String id) {
        return pdfService.generateInvoice(this.updateInvoice(id));
    }

    private Mono<Invoice> updateInvoice(String id) {
        return invoiceReactRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Invoice(" + id + ")")))
                .flatMap(invoice -> this.calculateBaseAndTax(invoice, invoice.getTicket().getShoppingList()))
                .flatMap(invoice -> invoiceReactRepository.save(invoice));
    }

    private Mono<Invoice> calculateBaseAndTax(Invoice invoice, Shopping[] shoppingList) {
        Stream<Shopping> ticketShoppingList = Arrays.stream(shoppingList);
        List<Mono<Article>> articlePublishers = ticketShoppingList
                .map(shopping -> this.articleReactRepository.findById(shopping.getArticleId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Article(" + shopping.getArticleId() + ")")))
                        .filter(article -> article.getTax() != Tax.FREE)
                        .doOnNext(article -> {
                            BigDecimal articleTaxRate = article.getTax().getRate().divide(new BigDecimal("100"));
                            BigDecimal articleTax = shopping.getShoppingTotal().multiply(articleTaxRate);
                            invoice.setTax(invoice.getTax().add(articleTax));
                            BigDecimal articleBaseTax = shopping.getShoppingTotal().subtract(articleTax);
                            invoice.setBaseTax(invoice.getBaseTax().add(articleBaseTax));
                        })).collect(Collectors.toList());
        return Mono.when(articlePublishers).then(Mono.just(invoice));
    }

    @Transactional
    public Mono<byte[]> createNegativeAndPdf(InvoiceNegativeCreationInputDto invoiceNegativeCreationInputDto) {
        Shopping[] returnedShoppings = invoiceNegativeCreationInputDto.getReturnedShoppingList().stream().map(shoppingDto ->
                new Shopping(shoppingDto.getAmount(), shoppingDto.getDiscount(), ShoppingState.RETURNED,
                        shoppingDto.getCode(), shoppingDto.getDescription(), shoppingDto.getRetailPrice()))

                .toArray(Shopping[]::new);
        return pdfService.generateNegativeInvoice(
                this.createNegativeInvoice(invoiceNegativeCreationInputDto.getReturnedTicketId(), returnedShoppings),
                returnedShoppings);
    }

    private Mono<Invoice> createNegativeInvoice(String ticketId, Shopping[] returnedShoppings) {
        Invoice invoice = new Invoice(0, null, null);
        Mono<Ticket> oldTicketPublisher = ticketReactRepository.findById(ticketId)
                .switchIfEmpty(Mono.error(new NotFoundException("Ticket(" + ticketId + ")")))
                .doOnNext(ticket -> {
                    invoice.setUser(ticket.getUser());
                    invoice.setTicket(ticket);
                });
        Mono<Invoice> invoiceMono = invoiceReactRepository.findFirstByTicketAndTaxGreaterThanEqual(oldTicketPublisher, BigDecimal.ZERO)
                .switchIfEmpty(Mono.error(new NotFoundException("Positive Invoice not found")));

        Mono<Integer> nextId = this.nextIdStartingYearly()
                .map(id -> {
                    invoice.setId(id);
                    return id;
                });
        Flux<Shopping> shoppingFlux = Flux.fromArray(returnedShoppings)
                .handle((shopping, shoppingSynchronousSink) -> {
                    if (shopping.getAmount() >= 0)
                        shoppingSynchronousSink.error(new BadRequestException("Shopping Amount not allowed (" + shopping.getAmount() + ")"));
                });
        Mono<Invoice> calculateBaseAndTaxPublisher = this.calculateBaseAndTax(invoice, returnedShoppings);
        return Mono.when(calculateBaseAndTaxPublisher, nextId, invoiceMono, shoppingFlux)
                .then(invoiceReactRepository.save(invoice));

    }

    public Flux<InvoiceOutputDto> readAll() {
        return invoiceReactRepository.findAll()
                .map(InvoiceOutputDto::new);
    }

    public Flux<InvoiceOutputDto> readAllByFilters(String mobile, LocalDate fromDate, LocalDate toDate) {
       return invoiceReactRepository.findAll()
                .filter(invoice -> ((mobile == null || mobile.equals("")) || invoice.getUser().getMobile().equals(mobile))
                        && (fromDate == null || invoice.getCreationDate().toLocalDate().compareTo(fromDate) >= 0)
                        && (toDate == null || invoice.getCreationDate().toLocalDate().compareTo(toDate) < 0))
                .map(InvoiceOutputDto::new);
    }
}
