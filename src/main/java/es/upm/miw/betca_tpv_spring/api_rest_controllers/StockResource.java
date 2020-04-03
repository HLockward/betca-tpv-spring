package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.StockController;
import es.upm.miw.betca_tpv_spring.dtos.ArticleSalesInfoDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleStockDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(StockResource.STOCK)
public class StockResource {
    public static final String STOCK = "/stock";
    public static final String CODE_ID = "/{code}";

    private StockController stockController;

    @Autowired
    public StockResource(StockController stockController) {
        this.stockController = stockController;
    }

    @GetMapping
    public Flux<ArticleStockDto> readAll(@RequestParam(required = false) Integer minimumStock, @RequestParam(required = false) String initDate, @RequestParam(required = false) String endDate) {
        LocalDateTime initDateTime = initDate == null || initDate.isEmpty() ? null : LocalDateTime.parse(initDate, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endDateTime = endDate == null || endDate.isEmpty() ? null : LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME).plusDays(1);

        return this.stockController.readAll(minimumStock, initDateTime, endDateTime)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = CODE_ID)
    public Flux<ArticleSalesInfoDto> readArticleSalesInfo(@PathVariable String code) {
        return this.stockController.readArticleSalesInfo(code)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }
}
