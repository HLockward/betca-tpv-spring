package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Tag;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface TagReactRepository extends ReactiveSortingRepository<Tag, String> {
    Mono<Tag> findByDescription(String tagDescription);
}
