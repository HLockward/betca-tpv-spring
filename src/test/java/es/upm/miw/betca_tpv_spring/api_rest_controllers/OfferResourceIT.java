package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Offer;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.OfferDto;
import es.upm.miw.betca_tpv_spring.dtos.OrderDto;
import es.upm.miw.betca_tpv_spring.repositories.OfferRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.OfferResource.OFFER_ID;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.OrderResource.ORDERS;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.OrderResource.ORDER_ID;
import static org.junit.Assert.assertEquals;

@ApiTestConfig
public class OfferResourceIT {

    public static final String OFFERS = "/offers";

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OfferRepository offerRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private LinkedList<Offer> offers;

    @BeforeEach
    void seedDatabase() {
        offers = new LinkedList<>();

        Offer offer = new Offer();
        offer.setDiscount(new BigDecimal(5));
        offer.setDescription("Summer Offer");
        offer.setExpirationDate(LocalDate.parse("2019-01-04").atStartOfDay());

        Article[] articleList = {
                Article.builder("0000001").description("Potato").retailPrice(new BigDecimal(3)).build(),
                Article.builder("000000A").description("Strawberry").retailPrice(new BigDecimal(1)).build()
        };

        offer.setArticleList(articleList);

        offers.add(offer);

        this.offerRepository.saveAll(offers);
    }

    @Test
    void testCreateOffer() {
        OfferDto offerDto = createOfferDto();

        Assert.assertNotNull(offerDto);
        assertEquals(new BigDecimal(5), offerDto.getDiscount());
    }

    @Test
    void testPrintOffer() {
        OfferDto offerDto = createOfferDto();
        this.restService.loginAdmin(this.webTestClient)
                .get().uri(contextPath + OFFERS + "/" + offerDto.getId() + "/print")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteOfferWithCorrectId() {
        OfferDto offerDto = createOfferDto();

        assert offerDto != null;
        this.offerRepository.deleteById(offerDto.getId());
        assertEquals(this.offerRepository.findById(offerDto.getId()), Optional.empty());
    }

    @Test
    void testSearchOfferByDates() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + OFFERS)
                .queryParam("registrationDate", "2020-03-31T22:00:00.000Z")
                .queryParam("expirationDate", "2020-04-29T22:00:00.000Z")
                .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetOffer() {
        OrderDto order = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + OFFERS + OFFER_ID, this.offers.get(0).getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .returnResult().getResponseBody();
        assertEquals(this.offerRepository.findById(this.offers.get(0).getId()).get().getId(), order.getId());
    }

    @Test
    void testDeleteOfferById() {
        this.restService.loginAdmin(webTestClient)
                .delete().uri(contextPath + OFFERS + OFFER_ID, this.offers.get(0).getId())
                .exchange()
                .expectStatus().isOk();
        assertEquals(Optional.empty(), this.offerRepository.findById(this.offers.get(0).getId()));
    }

    private OfferDto createOfferDto() {
        ArticleDto[] articles = {new ArticleDto(Article.builder("0000001").description("Potato").retailPrice(new BigDecimal(3)).build())};
        OfferCreationDto offerCreationDto = new OfferCreationDto(LocalDateTime.now(), new BigDecimal(5), "Winter Offer", Arrays.asList(articles));

        return this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + OFFERS)
                .body(BodyInserters.fromObject(offerCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(OfferDto.class)
                .returnResult().getResponseBody();
    }
}
