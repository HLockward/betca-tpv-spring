package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Ticket;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleSalesInfoDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleStockDto;
import es.upm.miw.betca_tpv_spring.dtos.ShoppingDto;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.TicketReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class StockController {
    private ArticleReactRepository articleReactRepository;
    private TicketReactRepository ticketReactRepository;

    @Autowired
    public StockController(ArticleReactRepository articleReactRepository, TicketReactRepository ticketReactRepository) {
        this.articleReactRepository = articleReactRepository;
        this.ticketReactRepository = ticketReactRepository;
    }

    public Flux<ArticleStockDto> readAll(Integer minimumStock, LocalDateTime initDate, LocalDateTime endDate) {
        Flux<ArticleStockDto> articleSoldUnits = this.getSoldUnits(initDate, endDate);
        Flux<ArticleStockDto> articleInfo = this.getArticleInfo(minimumStock);
        return Flux.merge(articleSoldUnits, articleInfo).groupBy(ArticleStockDto::getCode)
                .flatMap(idFlux -> idFlux.reduce((article, article2) -> {
                            ArticleStockDto articleStockDto = article.getStock() != null ? article : article2;
                            articleStockDto.setSoldUnits(article.getSoldUnits() != null ? article.getSoldUnits() : article2.getSoldUnits());
                            return articleStockDto;
                        })
                ).map(article -> {
                    if (article.getSoldUnits() == null) {
                        article.setSoldUnits(0);
                    }
                    return article;
                }).filter(article -> {
                    boolean filterDate = initDate != null || endDate != null;
                    return article.getStock() != null && (!filterDate || article.getSoldUnits() > 0);
                });
    }

    public Flux<ArticleStockDto> getArticleInfo(Integer minimumStock) {
        Flux<Article> articlesFlux = minimumStock == null ? this.articleReactRepository.findAll()
                : this.articleReactRepository.findByStockLessThanEqual(minimumStock);
        return articlesFlux.map(article -> {
            ArticleStockDto articleStockDto = new ArticleStockDto();
            articleStockDto.setCode(article.getCode());
            articleStockDto.setStock(article.getStock());
            articleStockDto.setDescription(article.getDescription());
            return articleStockDto;
        });
    }

    public Flux<ArticleStockDto> getSoldUnits(LocalDateTime initDate, LocalDateTime endDate) {
        return this.getShopping(initDate, endDate).groupBy(ShoppingDto::getCode)
                .flatMap(idFlux -> idFlux.map(shopping -> {
                            ArticleStockDto article = new ArticleStockDto();
                            article.setCode(shopping.getCode());
                            article.setSoldUnits(shopping.getAmount());
                            return article;
                        }).reduce((article, article2) -> {
                            article.setSoldUnits(article.getSoldUnits() + article2.getSoldUnits());
                            return article;
                        })
                );
    }

    public Flux<ShoppingDto> getShopping(LocalDateTime initDate, LocalDateTime endDate) {
        Flux<Ticket> shoppingDtoFlux;
        if (initDate == null && endDate == null) {
            shoppingDtoFlux = this.ticketReactRepository.findAll();
        } else if (initDate == null) {
            shoppingDtoFlux = this.ticketReactRepository.findByCreationDateLessThanEqual(endDate);
        } else {
            shoppingDtoFlux = this.ticketReactRepository.findByCreationDateBetween(initDate, endDate != null ? endDate : LocalDateTime.now());
        }
        return shoppingDtoFlux.filter(ticket -> ticket.getShoppingList() != null)
                .flatMap(ticket -> Flux.just(ticket.getShoppingList()))
                .map(ShoppingDto::new);
    }

    public Flux<ArticleSalesInfoDto> readArticleSalesInfo(String code) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime initDate = LocalDateTime.parse(LocalDateTime.now().minusYears(1).getYear() + "-01-01 00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse(LocalDateTime.now().minusYears(1).getYear() + "-12-31 00:00", formatter);
        LocalDateTime firstDayYearDate = LocalDateTime.parse(LocalDateTime.now().getYear() + "-01-01 00:00", formatter);

        Flux<ArticleSalesInfoDto> shoppingLastYearDtoFlux = this.getShoppingArticlePerYear(initDate, endDate, code);
        Flux<ArticleSalesInfoDto> shoppingYearDtoFlux = this.getShoppingArticlePerYear(firstDayYearDate, null, code);
        Mono articleDtoFlux = this.articleReactRepository.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException("Article not found")))
                .map(ArticleDto::new).then();

        Flux<ArticleSalesInfoDto> articleSalesInfo = Flux.merge(shoppingLastYearDtoFlux, shoppingYearDtoFlux);

        return Flux.concat(articleDtoFlux, articleSalesInfo);


    }

    public Flux<ArticleSalesInfoDto> getShoppingArticlePerYear(LocalDateTime initDate, LocalDateTime endDate, String code) {

        return this.ticketReactRepository.findByCreationDateBetween(initDate, endDate != null ? endDate : LocalDateTime.now())
                .groupBy(ticket -> ticket.getCreationDate().getMonth())
                .flatMap(idFlux -> idFlux
                        .flatMap(ticket -> {
                            ArticleSalesInfoDto[] articleSalesInfoDto = new ArticleSalesInfoDto[ticket.getShoppingList().length];
                            for (int i = 0; i < ticket.getShoppingList().length; i++) {
                                ArticleSalesInfoDto article = new ArticleSalesInfoDto(ticket.getShoppingList()[i].getArticleId());
                                article.setMonth(ticket.getCreationDate().getMonth().getValue());
                                article.setYear(ticket.getCreationDate().getYear());
                                article.setAmount(ticket.getShoppingList()[i].getAmount());
                                articleSalesInfoDto[i] = article;
                            }
                            return Flux.just(articleSalesInfoDto);
                        })
                        .filter(articleSalesInfo -> articleSalesInfo.getCode().equals(code))
                        .reduce((articleSalesInfo, articleSalesInfo1) -> {
                            articleSalesInfo.setAmount(articleSalesInfo.getAmount() + articleSalesInfo1.getAmount());
                            return articleSalesInfo;
                        })
                );
    }

}
