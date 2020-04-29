package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.StockAlarmArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmDto;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.StockAlarmResource.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@ApiTestConfig
class StockAlarmResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProviderRepository providerRepository;

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
}
