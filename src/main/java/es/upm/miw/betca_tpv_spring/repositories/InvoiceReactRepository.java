package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Invoice;
import es.upm.miw.betca_tpv_spring.documents.Ticket;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.Date;

public interface InvoiceReactRepository extends ReactiveSortingRepository<Invoice, String> {
    Mono<Invoice> findFirstByOrderByCreationDateDescIdDesc();
    Mono<Invoice> findFirstByTicketAndTaxGreaterThanEqual(Mono<Ticket> ticket, BigDecimal tax);
}
