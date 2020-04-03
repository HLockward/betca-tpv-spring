package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.ArticleStockDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.StockResource.CODE_ID;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.StockResource.STOCK;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ApiTestConfig
class StockResourceIT {
    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testReadAllStock() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + STOCK)
                .build())
                .exchange().expectStatus().isOk();
    }

    @Test
    void testReadStockMinStock() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + STOCK)
                .queryParam("minimumStock", 0)
                .build())
                .exchange().expectBody(ArticleStockDto[].class)
                .value(articleStockDto -> assertTrue(articleStockDto.length <= 0 || articleStockDto[0].getStock() <= 0));
    }

    @Test
    void testReadStockMinStockAndCreationDate() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + STOCK)
                .queryParam("minimumStock", 1)
                .queryParam("endDate", LocalDateTime.now().minusMonths(2).toString())
                .build())
                .exchange().expectBody(ArticleStockDto[].class)
                .value(articleStockDto -> assertTrue(articleStockDto.length <= 0 || articleStockDto[0].getStock() <= 1));
    }

    @Test
    void testReadArticleSalesInfoNotFound() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + STOCK + CODE_ID, "notFoundException")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testReadArticleSalesInfo() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + STOCK + CODE_ID, "8400000000017")
                .exchange()
                .expectStatus().isOk();
    }
}
