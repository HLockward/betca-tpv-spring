package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.StockAlarm;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface StockAlarmReactRepository extends ReactiveSortingRepository<StockAlarm, String> {

    @Query(value = "{'stockAlarmArticle': {  $elemMatch: { 'article' : ?0 } }}")
    Flux<StockAlarm> findByArticle(Article article);
}
