package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_services.Barcode;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.ArticleResource.*;

@ApiTestConfig
class ArticleResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testReadArticleOne() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + ARTICLES + CODE_ID, "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticleDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testReadArticleNonExist() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + ARTICLES + CODE_ID, "kk")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateArticleRepeated() {
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + ARTICLES)
                .body(BodyInserters.fromObject(
                        new ArticleDto("8400000000017", "repeated", "", BigDecimal.TEN, 10)))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }


    @Test
    void testCreateArticleNegativePrice() {
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + ARTICLES)
                .body(BodyInserters.fromObject(
                        new ArticleDto("4800000000011", "new", "", new BigDecimal("-1"), 10)))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testReadAllArticles() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + ARTICLES)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSearchArticle() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ARTICLES + SEARCH)
                .queryParam("description", "null")
                .queryParam("provider", this.providerRepository.findAll().get(1).getId()).build()
        ).exchange().expectStatus().isOk();
    }

    @Test
    void testCreateArticle() {
        String code = new Barcode().generateEan13code(Long.parseLong(this.articleRepository.findFirstByOrderByCodeDesc().getCode().substring(0, 12)) + 1);
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + ARTICLES)
                .body(
                        BodyInserters.fromObject(
                                new ArticleDto(code, "nueva", "nuevo", new BigDecimal("20"), 10)
                        )
                ).exchange().expectStatus().isOk().expectBody(ArticleDto.class)
                .value(Assertions::assertNotNull);
        this.articleRepository.deleteById("8400000000093");
    }

    @Test
    void testSearchArticleByReference() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ARTICLES + ADVANCEDSEARCH)
                .queryParam("reference", "Zz").build()
        ).exchange().expectStatus().isOk();
    }

}
