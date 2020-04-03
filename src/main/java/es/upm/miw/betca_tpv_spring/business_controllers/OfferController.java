package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Offer;
import es.upm.miw.betca_tpv_spring.dtos.OfferCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferSearchDto;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.OfferReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class OfferController {
    private OfferReactRepository offerReactRepository;
    private PdfService pdfService;

    @Autowired
    public OfferController(OfferReactRepository offerReactRepository, PdfService pdfService) {
        this.offerReactRepository = offerReactRepository;
        this.pdfService = pdfService;
    }

    public Flux<Offer> searchOffer(OfferSearchDto offerSearchDto) {
        return this.offerReactRepository.findAllByRegistrationDateBetween(offerSearchDto.getRegistrationDate().minusDays(1), offerSearchDto.getExpirationDate().plusDays(1));
    }

    public Mono<OfferDto> getOffer(String offerId) {
        return this.offerReactRepository.findById(offerId)
                .switchIfEmpty(Mono.error(new NotFoundException("Order id: " + offerId))).map(OfferDto::new);
    }

    public Mono<Offer> createOffer(OfferCreationDto offerCreationDto) {
        Article[] articles;
        articles = offerCreationDto.getArticleList().stream().map(articleDto -> Article.builder(articleDto.getCode())
                .description(articleDto.getDescription())
                .retailPrice(articleDto.getRetailPrice())
                .build()).toArray(Article[]::new);

        Offer offer = new Offer();
        offer.setDescription(offerCreationDto.getDescription());
        offer.setDiscount(offerCreationDto.getDiscount());
        offer.setExpirationDate(offerCreationDto.getExpirationDate());
        offer.setArticleList(articles);

        return offerReactRepository.save(offer);
    }

    public Mono<Void> deleteOffer(String offerId) {
        Mono<Offer> offer = this.offerReactRepository.findById(offerId);
        return Mono.when(offer).then(this.offerReactRepository.deleteById(offerId));
    }

    @Transactional
    public Mono<byte[]> printOffer(String id) {
        Mono<Offer> offerReact = offerReactRepository.findById(id).switchIfEmpty(Mono.error(new NotFoundException("Offer code (" + id + ")")));

        return pdfService.generateOffer(offerReact);
    }
}
