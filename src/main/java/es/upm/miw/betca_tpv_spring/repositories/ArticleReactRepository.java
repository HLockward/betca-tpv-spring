package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Article;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ArticleReactRepository extends ReactiveSortingRepository<Article, String> {
    Flux<Article> findByDescriptionLikeOrProvider(String description, String provider);

    Flux<Article> findByStockLessThanEqual(Integer stock);

    Flux<Article> findByDescriptionLikeOrReferenceLikeOrStockOrProviderOrRetailPrice(String description, String reference, Integer stock,
                                                                                     String provider, BigDecimal retailPrice);
    Mono<Article> findFirstByOrderByCodeDesc();
}
