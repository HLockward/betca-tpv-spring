package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class StockAlarmControllerIT {

    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private StockAlarmController stockAlarmController;

    @Test
    void testCreateStockAlarm() {
        StockAlarmArticleDto[] stockAlarmArticleDto = {
                new StockAlarmArticleDto("1", 500, 1500),
                new StockAlarmArticleDto("8400000000017", 15, 20),
        };
       StockAlarmCreationDto stockAlarmCreationDto = new StockAlarmCreationDto(
               "stockAlarm1",
               this.providerRepository.findAll().get(1).getId(), 500,1000,stockAlarmArticleDto);

        StepVerifier
                .create(this.stockAlarmController.createStockAlarm(stockAlarmCreationDto))
                .expectNextMatches(stockAlarm -> {
                    assertEquals("stockAlarm1", stockAlarm.getDescription());
                    assertEquals(this.providerRepository.findAll().get(1).getId(), stockAlarm.getProvider());
                    assertEquals(new Integer(500),stockAlarm.getWarning());
                    assertEquals(new Integer(1000), stockAlarm.getCritical());
                    assertEquals(2,stockAlarm.getStockAlarmArticle().length);
                    return true;
                })
                .expectComplete()
                .verify();
    }
}

