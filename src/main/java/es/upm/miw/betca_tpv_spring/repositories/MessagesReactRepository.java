package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Messages;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface MessagesReactRepository extends ReactiveSortingRepository<Messages, String> {

    Mono<Messages> findFirstByOrderBySentDateDescIdDesc();
}
