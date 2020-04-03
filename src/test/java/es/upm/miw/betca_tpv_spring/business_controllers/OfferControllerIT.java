package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Offer;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferSearchDto;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.OfferRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class OfferControllerIT {

    @Autowired
    private OfferController offerController;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProviderRepository providerRepository;

    private OfferDto offerDto;

    @BeforeEach
    void seed() {
        Article[] articleList = {
                Article.builder("0000001").description("Potato").retailPrice(new BigDecimal(3)).build(),
                Article.builder("000000A").description("Strawberry").retailPrice(new BigDecimal(1)).build()
        };
        Offer offer = new Offer();
        offer.setDiscount(new BigDecimal(50));
        offer.setDescription("Summer Offer");
        offer.setExpirationDate(LocalDateTime.now());
        offer.setArticleList(articleList);

        this.offerRepository.save(offer);
    }

    @Test
    void testSearchOfferByExpirationDate() {
        OfferSearchDto offerSearchDto = new OfferSearchDto(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        StepVerifier.create(this.offerController.searchOffer(offerSearchDto))
                .expectNextCount(1)
                .thenCancel()
                .verify();
    }

    @Test
    void testGetOffer() {
        String id = this.offerRepository.findAll().get(0).getId();
        StepVerifier
                .create(this.offerController.getOffer(id))
                .expectNextMatches(offer -> {
                    assertEquals(id, offer.getId());
                    assertEquals(2, offer.getArticles().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testDeleteOffer() {
        assertEquals(1, this.offerRepository.count());
        String id = this.offerRepository.findAll().get(0).getId();
        StepVerifier
                .create(this.offerController.deleteOffer(id))
                .expectComplete()
                .verify();
        assertEquals(0, this.offerRepository.count());
    }

    @Test
    void testCreateOffer() {
        ArticleDto[] articles = {new ArticleDto(Article.builder("0000001").description("Potato").retailPrice(new BigDecimal(3)).build())};
        OfferCreationDto offerCreationDto = new OfferCreationDto(LocalDateTime.now(), new BigDecimal(5),"Summer Offer", Arrays.asList(articles));

        StepVerifier
                .create(this.offerController.createOffer(offerCreationDto))
                .expectNextMatches(offer -> {
                    assertEquals(1, offer.getArticleList().length);
                    assertEquals(new BigDecimal(5), offer.getDiscount());
                    assertEquals("Summer Offer", offer.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
