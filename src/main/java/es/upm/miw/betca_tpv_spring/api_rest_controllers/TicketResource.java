package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.TicketController;
import es.upm.miw.betca_tpv_spring.documents.Ticket;
import es.upm.miw.betca_tpv_spring.dtos.TicketCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketOutputDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketPatchDto;
import es.upm.miw.betca_tpv_spring.dtos.TicketSearchDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(TicketResource.TICKETS)
public class TicketResource {

    public static final String TICKETS = "/tickets";
    public static final String TICKET_ID = "/{id}";
    public static final String SEARCH = "/search";
    public static final String SEARCH_BY_ARTICLE = SEARCH + "/article/{articleId}";
    public static final String SEARCH_BY_ORDER = SEARCH + "/order/{orderId}";
    public static final String SEARCH_BY_TAG = SEARCH + "/tag/{tag}";

    private TicketController ticketController;

    @Autowired
    public TicketResource(TicketController ticketController) {
        this.ticketController = ticketController;
    }

    @PostMapping(produces = {"application/pdf", "application/json"})
    public Mono<byte[]> createTicket(@Valid @RequestBody TicketCreationInputDto ticketCreationDto) {
        return this.ticketController.createTicketAndPdf(ticketCreationDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping
    public Flux<TicketOutputDto> readAll() {
        return this.ticketController.readAll()
                .doOnEach(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = TICKET_ID + "/pdf")
    public byte[] getPdf(@PathVariable String id) {
        return this.ticketController.getPdf(id);
    }

    @GetMapping(value = TICKET_ID)
    public Mono<Ticket> getTicket(@PathVariable String id) {
        return this.ticketController.getTicket(id);
    }

    @GetMapping(value = SEARCH)
    public Flux<TicketOutputDto> searchByMobileDateOrAmount(@RequestParam(required = false) String mobile,
                                                            @RequestParam(required = false) String date,
                                                            @RequestParam(required = false) Integer amount) {
        LocalDateTime day = date == null ? null : LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        TicketSearchDto ticketSearchDto = new TicketSearchDto(mobile, day, amount);
        return this.ticketController.searchByMobileDateOrAmount(ticketSearchDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = SEARCH_BY_ARTICLE)
    public Flux<TicketOutputDto> searchNotCommittedByArticle(@PathVariable String articleId) {
        return this.ticketController.searchNotCommittedByArticle(articleId)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = SEARCH_BY_ORDER)
    public Flux<TicketOutputDto> searchNotCommittedByOrder(@PathVariable String orderId) {
        return this.ticketController.searchNotCommittedByOrder(orderId)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = SEARCH_BY_TAG)
    public Flux<TicketOutputDto> searchNotCommittedByTag(@PathVariable String tag) {
        return this.ticketController.searchNotCommittedByTag(tag)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PatchMapping(value = TICKET_ID)
    public Mono<TicketOutputDto> updateAmountAndShoppingStateTicket(@PathVariable String id, @RequestBody TicketPatchDto ticketPatchDto){
        if (ticketPatchDto.getShoppingPatchDtoList().size() == 0) {
            return Mono.error(new BadRequestException("Empty ShoppingList"));
        }
        return this.ticketController.updateShoppingTicket(id, ticketPatchDto);
    }
}
