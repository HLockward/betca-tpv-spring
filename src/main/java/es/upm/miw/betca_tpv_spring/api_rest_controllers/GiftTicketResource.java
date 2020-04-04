package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.GiftTicketController;
import es.upm.miw.betca_tpv_spring.dtos.GiftTicketCreationDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(GiftTicketResource.GIFT_TICKETS)
public class GiftTicketResource {

    public static final String GIFT_TICKETS = "/gift-tickets";

    private GiftTicketController giftTicketController;

    @Autowired
    public GiftTicketResource(GiftTicketController giftTicketController) {
        this.giftTicketController = giftTicketController;
    }

    @PostMapping(produces = {"application/pdf", "application/json"})
    public Mono<byte[]> createGiftTicket(@Valid @RequestBody GiftTicketCreationDto giftTicketCreationDto) {
        return this.giftTicketController.createGiftTicketAndPdf(giftTicketCreationDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

}
