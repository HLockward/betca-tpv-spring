package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import es.upm.miw.betca_tpv_spring.repositories.StockAlarmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.StockAlarmResource.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

@ApiTestConfig
class StockAlarmResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private StockAlarmRepository stockAlarmRepository;

    private StockAlarmDto stockAlarmDto;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testStockAlarmReadAll() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + STOCK_ALARMS)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testStockAlarmCreate() {
        StockAlarmArticleDto[] stockAlarmArticleDto = {
                new StockAlarmArticleDto("1", 500, 1500),
                new StockAlarmArticleDto("8400000000017", 15, 20),
        };
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + STOCK_ALARMS)
                .body(BodyInserters.fromObject(
                        new StockAlarmCreationDto(
                                "333", this.providerRepository.findAll().get(1).getId(), 1, 1, stockAlarmArticleDto
                        )))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarmDto.class).
                value(stockAlarmOutputDto -> assertNotNull(stockAlarmOutputDto.getId()));

    }

    @Test
    void testUpdateStockAlarm() {
        String id = this.stockAlarmRepository.findAll().get(0).getId();
        StockAlarmArticleDto[] stockAlarmArticleDto = {
                new StockAlarmArticleDto(this.articleRepository.findAll().get(1).getCode(), 2,2),
                new StockAlarmArticleDto(this.articleRepository.findAll().get(2).getCode(), 3,3),
        };
        StockAlarmDto stockAlarmDto =this.restService.loginAdmin(webTestClient)
                .put().uri(contextPath + STOCK_ALARMS + STOCK_ALARMS_ID, id)
                .body(BodyInserters.fromObject(
                        new StockAlarmCreationDto("stock2", this.providerRepository.findAll().get(1).getId(),2,2, stockAlarmArticleDto)
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockAlarmDto.class)
                .returnResult().getResponseBody();
        assertEquals(id, stockAlarmDto.getId());
    }
}
