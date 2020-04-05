package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.Messages;
import es.upm.miw.betca_tpv_spring.documents.User;
import es.upm.miw.betca_tpv_spring.dtos.MessagesCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.MessagesDto;
import es.upm.miw.betca_tpv_spring.dtos.MessagesOutputDto;
import es.upm.miw.betca_tpv_spring.dtos.UserMinimumDto;
import es.upm.miw.betca_tpv_spring.repositories.MessagesReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.UserReactRepository;
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

    public Flux<MessagesOutputDto> readAll() {
        return this.messagesReactRepository.findAll().map(MessagesOutputDto::new);
    }

    public Mono<MessagesOutputDto> readById(String messagesId) {
        return this.messagesReactRepository.findById(messagesId).map(MessagesOutputDto::new);
    }

    public Flux<MessagesOutputDto> readAllMessagesByToUser(String toUserMobile) {
        UserMinimumDto userMinimumDto = new UserMinimumDto(null, null, null);
        return this.userReactRepository.findByMobile(toUserMobile).map(user -> {
            userMinimumDto.setMobile(user.getMobile());
            return user;
        }).thenMany(this.messagesReactRepository.findAll()
                .filter(messages -> userMinimumDto.getMobile().equals(messages.getToUser().getMobile()))
                .map(MessagesOutputDto::new));
    }

    public Flux<MessagesOutputDto> readAllUnReadMessagesByToUser(String toUserMobile) {
        UserMinimumDto userMinimumDto = new UserMinimumDto(null, null, null);
        return this.userReactRepository.findByMobile(toUserMobile).map(user -> {
            userMinimumDto.setMobile(user.getMobile());
            return user;
        }).thenMany(this.messagesReactRepository.findAll()
                .filter(messages ->
                        userMinimumDto.getMobile().equals(messages.getToUser().getMobile())
                                && messages.getReadDate() == null)
                .map(MessagesOutputDto::new));
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
                .doOnNext(messages::setIdFromInt);
        Mono<User> userFrom = this.userReactRepository.findByMobile(messagesCreationDto.getFromUserMobile())
                .doOnNext(messages::setFromUser);
        Mono<User> userTo = this.userReactRepository.findByMobile(messagesCreationDto.getToUserMobile())
                .doOnNext(messages::setToUser);
        return Mono.when(userFrom, userTo, nextId)
                .then(this.messagesReactRepository.save(messages).map(MessagesDto::new));
    }

    private Mono<Integer> nextIdMessages() {
        return messagesReactRepository.findFirstByOrderBySentDateDescIdDesc()
                .map(messages -> messages.getIdParsedToInteger() + 1)
                .switchIfEmpty(Mono.just(1));
    }

    public Mono<MessagesDto> markMessageAsRead(String id, LocalDateTime ldtReadDate) {
        Mono<Messages> messagesMono = this.messagesReactRepository.findById(id).map(messages1 -> {
            messages1.setReadDate(ldtReadDate);
            return messages1;
        });
        return Mono.when(messagesMono).then(this.messagesReactRepository.saveAll(messagesMono).next()).map(MessagesDto::new);
    }
}
