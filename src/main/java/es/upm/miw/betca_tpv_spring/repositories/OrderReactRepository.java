package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Order;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface OrderReactRepository extends ReactiveSortingRepository<Order, String> {

    Flux<Order> findByDescriptionLikeOrProviderAndClosingDateIsNull(String description, String provider, LocalDateTime closingDate);

    Flux<Order> findByDescriptionLikeOrProvider(String description, String provider);
}
