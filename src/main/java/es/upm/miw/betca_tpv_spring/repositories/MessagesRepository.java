package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.documents.Messages;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessagesRepository extends MongoRepository<Messages, String> {
}
