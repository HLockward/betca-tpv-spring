package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.OfferController;
import es.upm.miw.betca_tpv_spring.documents.Offer;
import es.upm.miw.betca_tpv_spring.dtos.OfferCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferSearchDto;
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
@RequestMapping(OfferResource.OFFERS)
public class OfferResource {
    public static final String OFFERS = "/offers";

    public static final String OFFER_ID = "/{id}";

    private static final String PRINT = "/print";

    private OfferController offerController;

    @Autowired
    public OfferResource(OfferController offerController) {
        this.offerController = offerController;
    }

    @GetMapping
    public Flux<Offer> search(@RequestParam String registrationDate, @RequestParam String expirationDate) {
        OfferSearchDto offerSearchDto = new OfferSearchDto(LocalDateTime.parse(registrationDate, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse(expirationDate, DateTimeFormatter.ISO_DATE_TIME));
        return this.offerController.searchOffer(offerSearchDto).doOnEach(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Offer> createOffer(@Valid @RequestBody OfferCreationDto offerCreationDto) {
        return this.offerController.createOffer(offerCreationDto).doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @DeleteMapping(value = OFFER_ID)
    public Mono<Void> deleteOffer(@PathVariable String id) {
        return this.offerController.deleteOffer(id).doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = OFFER_ID)
    public Mono<OfferDto> getOrder(@PathVariable String id) {
        return this.offerController.getOffer(id).doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = OFFER_ID + PRINT, produces = {"application/pdf"})
    public Mono<byte[]> print(@PathVariable String id) {
        return this.offerController.printOffer(id).doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }
}
