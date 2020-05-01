package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.StockAlarmArticle;
import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import es.upm.miw.betca_tpv_spring.repositories.StockAlarmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class StockAlarmControllerIT {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private StockAlarmRepository stockAlarmRepository;

    @Autowired
    private StockAlarmController stockAlarmController;

    private StockAlarmDto stockAlarmDto;

    @BeforeEach
    void seed() {
        StockAlarmArticle[] stockAlarmArticle = {
                new StockAlarmArticle(this.articleRepository.findAll().get(0),1,1),
                new StockAlarmArticle(this.articleRepository.findAll().get(1),2,2),
        };
        this.stockAlarmDto = new StockAlarmDto("stockT", this.providerRepository.findAll().get(0).getId(),5,5,stockAlarmArticle);
    }

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

    @Test
    void testUpdateStockAlarm() {
        String id = this.stockAlarmRepository.findAll().get(0).getId();
        StockAlarmArticleDto[] stockAlarmArticleDtos = {
                new StockAlarmArticleDto(this.articleRepository.findAll().get(1).getCode(), 3,3),
                new StockAlarmArticleDto(this.articleRepository.findAll().get(2).getCode(), 4,4),
        };
        StepVerifier
                .create(this.stockAlarmController
                        .updateStockAlarm(id, new StockAlarmCreationDto("stock2", this.providerRepository.findAll().get(1).getId(), 2,2, stockAlarmArticleDtos)))
                .expectNextMatches(stockAlarmDto1 -> {
                    assertEquals(this.stockAlarmRepository.findById(id).get().getId(), stockAlarmDto1.getId());
                    assertEquals(this.stockAlarmRepository.findById(id).get().getDescription(), stockAlarmDto1.getDescription());
                    assertEquals(new Integer(2),stockAlarmDto1.getWarning());
                    assertEquals(new Integer(2), stockAlarmDto1.getCritical());
                    assertEquals(this.stockAlarmRepository.findById(id).get().getStockAlarmArticle().length, stockAlarmDto1.getStockAlarmArticle().length);
                    return true;
                })
                .expectComplete()
                .verify();
    }
}

