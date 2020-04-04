package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.Messages;
import es.upm.miw.betca_tpv_spring.documents.User;
import es.upm.miw.betca_tpv_spring.dtos.MessagesCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.MessagesDto;
import es.upm.miw.betca_tpv_spring.repositories.MessagesReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.UserReactRepository;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class MessagesController {

    private MessagesReactRepository messagesReactRepository;
    private UserReactRepository userReactRepository;

    public Mono<MessagesDto> createMessage(MessagesCreationDto messagesCreationDto) {
        //Messages messages = new Messages(null,null,null,null,null,null);
        Messages messages = new Messages(
                null,
                null,
                null,
                messagesCreationDto.getMessageContent(),
                messagesCreationDto.getSentDate(),
                null);
        Mono<Integer> nextId = this.nextIdMessages()
                .doOnNext(messages::setIdFromInt);
        Mono<User> userFrom = this.userReactRepository.findByMobile(messagesCreationDto.getFromUser().getMobile())
                .doOnNext(messages::setFromUser);
        Mono<User> userTo = this.userReactRepository.findByMobile(messagesCreationDto.getToUser().getMobile())
                .doOnNext(messages::setToUser);
        return Mono.when(userFrom, userTo, nextId)
                .then(this.messagesReactRepository.save(messages).map(messages1 -> new MessagesDto(messages1)));
    }

    private Mono<Integer> nextIdMessages() {
        return messagesReactRepository.findFirstByOrderBySentDateDescIdDesc()
                .map(messages -> messages.getIdParsedToInteger() + 1)
                .switchIfEmpty(Mono.just(1));
    }
}
