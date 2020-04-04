package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.MESSAGES;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

@ApiTestConfig
public class MessagesResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private LocalDateTime fixedLdt = LocalDateTime.of(2020, 4, 1, 1, 1, 1);

    @Test
    void testReadAll() {
        this.restService.loginAdmin(this.webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                    .path(contextPath + MESSAGES)
                    .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() > 1));
    }

    @Test
    void testCreateMessage() {
        MessagesCreationDto messagesCreationDto = new MessagesCreationDto(
                "666666000",
                "666666007",
                "Msg",
                fixedLdt.plusDays(6)
        );
        MessagesDto dto = this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + MESSAGES)
                .body(BodyInserters.fromObject(messagesCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
    }
}
