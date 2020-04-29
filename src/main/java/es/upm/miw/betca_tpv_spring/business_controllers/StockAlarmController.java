package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.StockAlarmArticle;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.StockAlarm;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.StockAlarmDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleReactRepository;
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
    private StockAlarmReactRepository stockAlarmReactRepository;

    @Autowired
    public StockAlarmController(ArticleReactRepository articleReactRepository, StockAlarmReactRepository stockAlarmReactRepository) {
        this.articleReactRepository = articleReactRepository;
        this.stockAlarmReactRepository = stockAlarmReactRepository;
    }

//    public Flux<StockAlarmDto> readAll() {
//        return this.stockAlarmReactRepository.findAll()
//                .switchIfEmpty(Flux.error(new BadRequestException("Bad Request")))
//                .map(StockAlarmDto::new);
//    }

//    public Mono<StockAlarmDto> createStockAlarm(StockAlarmCreationDto stockAlarmCreationDto) {
//        StockAlarm stockAlarm = new StockAlarm(
//                stockAlarmCreationDto.getDescription(),
//                stockAlarmCreationDto.getProvider(),
//                stockAlarmCreationDto.getWarning(),
//                stockAlarmCreationDto.getCritical(),
//                stockAlarmCreationDto.getStockAlarmArticle()
//        );
//        return this.stockAlarmReactRepository.save(stockAlarm)
//                .map(StockAlarmDto::new);
//    }

//    public Mono<StockAlarmDto> updateStockAlarm(String stockAlarmId, StockAlarmCreationDto stockAlarmCreationDto) {
//        Mono<StockAlarm> stockAlarm = this.stockAlarmReactRepository.findById(stockAlarmId)
//                .switchIfEmpty(Mono.error(new NotFoundException("StockAlarm Id" + stockAlarmId)))
//                .map(stockAlarm1 -> {
//                    stockAlarm1.setDescription(stockAlarmCreationDto.getDescription());
//                    stockAlarm1.setProvider(stockAlarmCreationDto.getProvider());
//                    stockAlarm1.setWarning(stockAlarmCreationDto.getWarning());
//                    stockAlarm1.setCritical(stockAlarmCreationDto.getCritical());
//                    stockAlarm1.setStockAlarmArticle(stockAlarmCreationDto.getStockAlarmArticle());
//                    return stockAlarm1;
//                });
//        return Mono
//                .when(stockAlarm)
//                .then(this.stockAlarmReactRepository.saveAll(stockAlarm).next().map(StockAlarmDto::new));
//    }

//    public Mono<Void> deleteStockAlarm(String stockAlarmId) {
//        Mono<StockAlarm> stockAlarm = this.stockAlarmReactRepository.findById(stockAlarmId);
//        return Mono
//                .when(stockAlarm)
//                .then(this.stockAlarmReactRepository.deleteById(stockAlarmId));
//    }

//    public Flux<StockAlarmDto> searchWarning(final String warningOrCritical) {
//        Flux<StockAlarmDto> stockAlarmFluxResults = this.stockAlarmReactRepository.findAll()
//                .map(StockAlarmDto::new);
//        List<StockAlarmDto> stockAlarmPoJoList = new ArrayList<>();
//        stockAlarmFluxResults.toStream().forEach(stockAlarm -> {
//            StockAlarmArticle[] stockAlarmArticles = stockAlarm.getStockAlarmArticle();
//            List<StockAlarmArticle> stockAlarmArticleList = new ArrayList<>();
//            StockAlarmDto stockAlarmPoJo = new StockAlarmDto();
//            for (StockAlarmArticle stockAlarmArticle : stockAlarmArticles) {
//                Mono<Article> article = this.articleReactRepository.findById(stockAlarmArticle.getArticleId());
//                Flux<Article> concat = Flux.concat(article);
//                concat.toStream().forEach(articlePojo -> {
//                    if (warningOrCritical.equals("warning")) {
//                        if (articlePojo.getStock() < stockAlarmArticle.getWarning()) {
//                            stockAlarmArticleList.add(stockAlarmArticle);
//                        }
//                    } else if (warningOrCritical.equals("critical")) {
//                        if (articlePojo.getStock() < stockAlarmArticle.getCritical()) {
//                            stockAlarmArticleList.add(stockAlarmArticle);
//                        }
//                    }
//                });
//            }
//            if (!stockAlarmArticleList.isEmpty()) {
//                stockAlarmPoJo.setStockAlarmArticle(stockAlarmArticleList.toArray(new StockAlarmArticle[stockAlarmArticleList.size()]));
//                stockAlarmPoJo.setId(stockAlarm.getId());
//                stockAlarmPoJo.setDescription(stockAlarm.getDescription());
//                stockAlarmPoJo.setProvider(stockAlarm.getProvider());
//                stockAlarmPoJo.setWarning(stockAlarm.getWarning());
//                stockAlarmPoJo.setCritical(stockAlarm.getCritical());
//                stockAlarmPoJoList.add(stockAlarmPoJo);
//            }
//        });
//        return Flux.fromIterable(stockAlarmPoJoList);
//    }
}
