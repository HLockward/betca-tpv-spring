package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.StockAlarmArticle;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.StockAlarm;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmDto;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmSearchDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.StockAlarmReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StockAlarmController {

    private ArticleReactRepository articleReactRepository;
    private ProviderReactRepository providerReactRepository;
    private StockAlarmReactRepository stockAlarmReactRepository;

    @Autowired
    public StockAlarmController(ArticleReactRepository articleReactRepository, StockAlarmReactRepository stockAlarmReactRepository,ProviderReactRepository providerReactRepository) {
        this.articleReactRepository = articleReactRepository;
        this.providerReactRepository = providerReactRepository;
        this.stockAlarmReactRepository = stockAlarmReactRepository;
    }

    public Flux<StockAlarmDto> readAll() {
        return this.stockAlarmReactRepository.findAll()
                .switchIfEmpty(Flux.error(new BadRequestException("Bad Request")))
                .map(StockAlarmDto::new);
    }

    public Mono<StockAlarmDto> createStockAlarm(StockAlarmCreationDto stockAlarmCreationDto) {
        StockAlarm stockAlarm = new StockAlarm(stockAlarmCreationDto.getDescription(), null,
                stockAlarmCreationDto.getWarning(), stockAlarmCreationDto.getCritical(), null
        );
        Mono<Void> provider;
        if (stockAlarmCreationDto.getProvider() == null) {
            provider = Mono.empty();
        } else {
            provider = this.providerReactRepository.findById(stockAlarmCreationDto.getProvider())
                    .switchIfEmpty(Mono.error(new NotFoundException("Provider (" + stockAlarmCreationDto.getProvider() + ")")))
                    .doOnNext(stockAlarm::setProvider).then();
        }
        List<StockAlarmArticle> stockAlarmArticles = new ArrayList<>();
        Flux<Article> articles = Flux.empty();
        for (StockAlarmArticleDto stockAlarmArticleDto : stockAlarmCreationDto.getStockAlarmArticle()) {
            Mono<Article> articleReact = this.articleReactRepository.findById(stockAlarmArticleDto.getArticleId())
                    .switchIfEmpty(Mono.error(new NotFoundException("Article (" + stockAlarmArticleDto.getArticleId() + ")")))
                    .map(article -> {
                        stockAlarmArticles.add(new StockAlarmArticle(article, stockAlarmArticleDto.getWarning(),stockAlarmArticleDto.getCritical()));
                        stockAlarm.setStockAlarmArticle(stockAlarmArticles.toArray(new StockAlarmArticle[stockAlarmArticles.size()]));
                        return article;
                    });
            articles = articles.mergeWith(articleReact);
        }
        return Mono.when(provider,articles)
                .then(this.stockAlarmReactRepository.save(stockAlarm))
                .map(StockAlarmDto::new);
    }

    public Mono<StockAlarmDto> updateStockAlarm(String id, StockAlarmCreationDto stockAlarmCreationDto) {
        List<StockAlarmArticle> stockAlarmArticles = new ArrayList<>();
        Flux<Article> articles = Flux.empty();
        for (StockAlarmArticleDto stockAlarmArticleDto : stockAlarmCreationDto.getStockAlarmArticle()) {
            Mono<Article> articleReact = this.articleReactRepository.findById(stockAlarmArticleDto.getArticleId())
                    .switchIfEmpty(Mono.error(new NotFoundException("Article (" + stockAlarmArticleDto.getArticleId() + ")")))
                    .map(article -> {
                        stockAlarmArticles.add(new StockAlarmArticle(article, stockAlarmArticleDto.getWarning(),stockAlarmArticleDto.getCritical()));
                        return article;
                    });
            articles = articles.mergeWith(articleReact);
        }
        Mono<StockAlarm> stockAlarm = this.stockAlarmReactRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("StockAlarm (" + id + ")")))
                .map(stockAlarmUpdate -> {
                    stockAlarmUpdate.setDescription(stockAlarmCreationDto.getDescription());
                    stockAlarmUpdate.setWarning(stockAlarmCreationDto.getWarning());
                    stockAlarmUpdate.setCritical(stockAlarmCreationDto.getCritical());
                    stockAlarmUpdate.setStockAlarmArticle(stockAlarmArticles.toArray(new StockAlarmArticle[stockAlarmArticles.size()]));
                    return stockAlarmUpdate;
                });
        return Mono.when(articles).then(stockAlarm).then(this.stockAlarmReactRepository.saveAll(stockAlarm).next()
                .map(StockAlarmDto::new));
    }

    public Mono<Void> deleteStockAlarm(String id) {
        Mono<StockAlarm> stockAlarmMono = this.stockAlarmReactRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("StockAlarm (" + id + ")")));
        return Mono
                .when(stockAlarmMono)
                .then(this.stockAlarmReactRepository.deleteById(id));
    }

    public Flux<StockAlarmSearchDto> getAllArticlesInStockAlarm(String searchArticleState) {
        return this.getAllStockAlarmArticle()
                .filter(stockAlarmArticle -> {
                    if (searchArticleState.equals("warning")) {
                         return stockAlarmArticle.getArticle().getStock() < stockAlarmArticle.getWarning();
                    }else {
                         return stockAlarmArticle.getArticle().getStock() < stockAlarmArticle.getCritical();
                         }
                })
                .map(stockAlarmArticle -> {
                    StockAlarmSearchDto stockAlarmSearchDto = new StockAlarmSearchDto();
                    stockAlarmSearchDto.setCode(stockAlarmArticle.getArticle().getCode());
                    stockAlarmSearchDto.setDescription(stockAlarmArticle.getArticle().getDescription());
                    stockAlarmSearchDto.setStock(stockAlarmArticle.getArticle().getStock());
                    stockAlarmSearchDto.setWarning(stockAlarmArticle.getWarning());
                    stockAlarmSearchDto.setCritical(stockAlarmArticle.getCritical());
                    return stockAlarmSearchDto;
                });
    }

    public Flux<StockAlarmArticle> getAllStockAlarmArticle() {
        Flux<StockAlarm> stockAlarmFlux = this.stockAlarmReactRepository.findAll();
        return stockAlarmFlux
                .flatMap(stockAlarm -> Flux.just(stockAlarm.getStockAlarmArticle()))
                .map(StockAlarmArticle::new);
    }


}
