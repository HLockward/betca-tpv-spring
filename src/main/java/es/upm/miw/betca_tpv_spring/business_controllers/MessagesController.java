package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.Messages;
import es.upm.miw.betca_tpv_spring.documents.User;
import es.upm.miw.betca_tpv_spring.dtos.MessagesCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.MessagesDto;
import es.upm.miw.betca_tpv_spring.repositories.MessagesReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.UserReactRepository;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Controller
public class MessagesController {

    private MessagesReactRepository messagesReactRepository;
    private UserReactRepository userReactRepository;

    @Autowired
    public MessagesController(MessagesReactRepository messagesReactRepository,
                              UserReactRepository userReactRepository) {
        this.messagesReactRepository = messagesReactRepository;
        this.userReactRepository = userReactRepository;

    }

    public Mono<MessagesDto> createMessage(MessagesCreationDto messagesCreationDto) {
        Messages messages = new Messages(
                null,
                null,
                null,
                messagesCreationDto.getMessageContent(),
                messagesCreationDto.getSentDate(),
                null);
        Mono<Integer> nextId = this.nextIdMessages()
                .doOnNext(messages::setIdFromInt)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
        Mono<User> userFrom = this.userReactRepository.findByMobile(messagesCreationDto.getFromUserMobile())
                .doOnNext(messages::setFromUser)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
        Mono<User> userTo = this.userReactRepository.findByMobile(messagesCreationDto.getToUserMobile())
                .doOnNext(messages::setToUser)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
        return Mono.when(userFrom, userTo, nextId)
                .then(this.messagesReactRepository.save(messages).map(MessagesDto::new))
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    private Mono<Integer> nextIdMessages() {
        return messagesReactRepository.findFirstByOrderBySentDateDescIdDesc()
                .map(messages -> messages.getIdParsedToInteger() + 1)
                .switchIfEmpty(Mono.just(1));
    }

    public Flux<MessagesDto> readAll() {
        return this.messagesReactRepository.findAllMessages();
    }

    public Mono<MessagesDto> readById(String messagesId) {
        return this.messagesReactRepository.findById(messagesId).map(MessagesDto::new);
    }

    public Mono<MessagesDto> markMessageAsRead(String id, LocalDateTime ldtReadDate) {
        Mono<Messages> messagesMono = this.messagesReactRepository.findById(id).map(messages1 -> {
            messages1.setReadDate(ldtReadDate);
            return messages1;
        });
        return Mono.when(messagesMono).then(this.messagesReactRepository.saveAll(messagesMono).next()).map(MessagesDto::new);
    }
}
