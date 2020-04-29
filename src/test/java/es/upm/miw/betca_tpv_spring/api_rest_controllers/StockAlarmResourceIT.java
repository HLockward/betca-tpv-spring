package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.StockAlarmResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ApiTestConfig
class StockAlarmResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testStockAlarmReadAll() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + STOCK_ALARMS)
                .exchange()
                .expectStatus().isOk();
    }

//    @Test
//    void testStockAlarmCreate() {
//        AlarmArticle alarmArticle2 = new AlarmArticle();
//        alarmArticle2.setWarning(3);
//        alarmArticle2.setCritical(3);
//        alarmArticle2.setArticleId("2");
//        AlarmArticle[] alarmArticles1 = {
//                new AlarmArticle("1", 1, 1),
//                alarmArticle2
//        };
//        this.restService.loginAdmin(webTestClient)
//                .post().uri(contextPath + STOCK_ALARMS)
//                .body(BodyInserters.fromObject(
//                        new StockAlarmInputDto(
//                                "333", "upm", 1, 1, alarmArticles1
//                        )))
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(StockAlarmOutputDto.class)
//                .value(stockAlarmOutputDto -> assertNotNull(stockAlarmOutputDto.getId())
//                );
//    }

//    @Test
//    void testStockAlarmUpdate() {
//        AlarmArticle[] alarmArticles1 = {
//                new AlarmArticle("1", 1, 1),
//                new AlarmArticle("2", 2, 2)
//        };
//        this.restService.loginAdmin(webTestClient)
//                .put().uri(contextPath + STOCK_ALARMS + STOCK_ALARMS_ID, 222)
//                .body(BodyInserters.fromObject(
//                        new StockAlarmInputDto(
//                                "123123", "upm", 99, 99, alarmArticles1
//                        )
//                ))
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(StockAlarmOutputDto.class)
//                .value(stockAlarmOutputDto ->
//                        assertEquals("123123", stockAlarmOutputDto.getDescription()));
//    }

//    @Test
//    void testStockAlarmDelete() {
//        this.restService.loginAdmin(webTestClient)
//                .delete().uri(contextPath + STOCK_ALARMS + STOCK_ALARMS_ID, 111)
//                .exchange()
//                .expectStatus().isOk();
//    }
//
//    @Test
//    void testStockAlarmSearchWarning() {
//        this.restService.loginAdmin(webTestClient)
//                .get().uri(contextPath + STOCK_ALARMS + STOCK_ALARMS_WARNING)
//                .exchange()
//                .expectStatus().isOk();
//    }
//
//    @Test
//    void testStockAlarmSearchCritical() {
//        this.restService.loginAdmin(webTestClient)
//                .get().uri(contextPath + STOCK_ALARMS + STOCK_ALARMS_CRITICAL)
//                .exchange()
//                .expectStatus().isOk();
//    }
}
