package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.MessagesController;
import es.upm.miw.betca_tpv_spring.dtos.MessagesCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.MessagesDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(MessagesResource.MESSAGES)
public class MessagesResource {

    public static final String MESSAGES = "/messages";

    private MessagesController messagesController;

    @Autowired
    public MessagesResource(MessagesController messagesController) {
        this.messagesController = messagesController;
    }

    @PostMapping
    public Mono<MessagesDto> createMessage(@Valid @RequestBody MessagesCreationDto messagesCreationDto) {
        return this.messagesController.createMessage(messagesCreationDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping
    public Flux<MessagesDto> readAll() {
        return this.messagesController.readAll()
                .doOnEach(log -> LogManager.getLogger(this.getClass()).debug(log));
    }
}
