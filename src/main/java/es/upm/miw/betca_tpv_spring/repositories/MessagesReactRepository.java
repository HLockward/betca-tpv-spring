package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Messages;
import es.upm.miw.betca_tpv_spring.dtos.MessagesDto;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MessagesReactRepository extends ReactiveSortingRepository<Messages, String> {

    Mono<Messages> findById(String id);

    Mono<Messages> findFirstByOrderBySentDateDescIdDesc();

    @Query(value = "{}")
    Flux<MessagesDto> findAllMessages();
}
