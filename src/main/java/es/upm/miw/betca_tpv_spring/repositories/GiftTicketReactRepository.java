package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.GiftTicket;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface GiftTicketReactRepository extends ReactiveSortingRepository<GiftTicket, String> {

}