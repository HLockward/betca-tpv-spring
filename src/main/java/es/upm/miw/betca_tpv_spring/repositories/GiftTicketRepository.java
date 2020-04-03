package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.GiftTicket;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface GiftTicketRepository extends MongoRepository<GiftTicket, String> {

}
