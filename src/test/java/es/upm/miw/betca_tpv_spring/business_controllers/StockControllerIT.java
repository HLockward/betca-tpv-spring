package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


@TestConfig
class StockControllerIT {
    @Autowired
    private StockController stockController;

    @Test
    void testReadAll() {
        StepVerifier
                .create(this.stockController.readAll(null, null, null))
                .expectNextCount(10)
                .thenCancel()
                .verify();
    }

    @Test
    void testReadAllFilterDateAndStock() {
        StepVerifier
                .create(this.stockController.readAll(5, LocalDateTime.now().minusMonths(1), LocalDateTime.now()))
                .expectNextCount(2)
                .thenCancel()
                .verify();
    }

    @Test
    void testArticleInfo() {
        StepVerifier
                .create(this.stockController.getArticleInfo(null))
                .expectNextCount(10)
                .thenCancel()
                .verify();
    }

    @Test
    void testArticleInfoWithMinimumStock() {
        StepVerifier
                .create(this.stockController.getArticleInfo(10))
                .expectNextCount(9)
                .thenCancel()
                .verify();
    }

    @Test
    void testArticleInfoWithMinimumStockNoResult() {
        StepVerifier
                .create(this.stockController.getArticleInfo(-100000))
                .expectComplete()
                .verify();
    }

    @Test
    void testGetSoldUnits() {
        HashMap<String, Integer> unitsMap = new HashMap<>();
        unitsMap.put("8400000000017", 2);
        unitsMap.put("8400000000031", 12);
        unitsMap.put("8400000000055", 12);
        unitsMap.put("8400000000024", 3);
        StepVerifier
                .create(this.stockController.getSoldUnits(null, null))
                .expectNextMatches(articleStockDto -> articleStockDto.getSoldUnits().equals(unitsMap.get(articleStockDto.getCode())))
                .expectNextMatches(articleStockDto -> articleStockDto.getSoldUnits().equals(unitsMap.get(articleStockDto.getCode())))
                .expectNextMatches(articleStockDto -> articleStockDto.getSoldUnits().equals(unitsMap.get(articleStockDto.getCode())))
                .expectNextMatches(articleStockDto -> articleStockDto.getSoldUnits().equals(unitsMap.get(articleStockDto.getCode())))
                .thenCancel()
                .verify();
    }

    @Test
    void testGetShopping() {
        StepVerifier
                .create(this.stockController.getShopping(null, null))
                .expectNextCount(11)
                .thenCancel()
                .verify();
    }

    @Test
    void testGetShoppingInitDate() {
        StepVerifier
                .create(this.stockController.getShopping(LocalDateTime.now().with(LocalTime.of(0, 0)), null))
                .expectNextCount(11)
                .thenCancel()
                .verify();
    }

    @Test
    void testGetShoppingEndDate() {
        StepVerifier
                .create(this.stockController.getShopping(null, LocalDateTime.now().minusMonths(1)))
                .expectComplete()
                .verify();
    }

    @Test
    void testGetShoppingArticlePerYearEmpty() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime endDate = LocalDateTime.parse("2010-12-31 00:00", formatter);
        LocalDateTime initDate = LocalDateTime.parse("2010-01-01 00:00", formatter);

        StepVerifier
                .create(this.stockController.getShoppingArticlePerYear(initDate, endDate, "8400000000017"))
                .expectComplete()
                .verify();
    }

    @Test
    void testGetShoppingArticlePerYear() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime initDate = LocalDateTime.parse("2020-01-01 00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse("2020-12-31 00:00", formatter);

        StepVerifier
                .create(this.stockController.getShoppingArticlePerYear(initDate, endDate, "8400000000017"))
                .expectNextMatches(articleStockDto -> articleStockDto.getYear().equals(initDate.getYear()))
                .thenCancel()
                .verify();
    }

    @Test
    void readArticleSalesInfo() {
        StepVerifier
                .create(this.stockController.readArticleSalesInfo("8400000000017"))
                .expectNextMatches(articleStockDto -> articleStockDto.getYear().equals(LocalDateTime.now().getYear()))
                .thenCancel()
                .verify();
    }
}
