package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.MESSAGES;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.MESSAGES_ID;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.TO_USER;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.MOBILE;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.UNREAD;

import java.time.LocalDateTime;
import java.util.List;

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
        List<MessagesOutputDto> messagesOutputDtoList = this.restService.loginAdmin(this.webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                        .path(contextPath + MESSAGES)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesOutputDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        int ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck = 3;
        for (int i = 0; i < ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck; i++) {
            String indexValue = String.valueOf(i + 1);
            assertEquals(indexValue, messagesOutputDtoList.get(i).getId());
            assertEquals("Msg from " + indexValue + " to 7", messagesOutputDtoList.get(i).getMessageContent());
            assertNotNull(messagesOutputDtoList.get(i).getSentDate());
        }
    }

    @Test
    void testReadById() {
        String idToLookFor = "1";
        MessagesOutputDto messagesOutputDto = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + MESSAGES + MESSAGES_ID, idToLookFor)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessagesOutputDto.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
        assertEquals(idToLookFor, messagesOutputDto.getId());
        assertEquals("manager", messagesOutputDto.getFromUsername());
        assertEquals("u007", messagesOutputDto.getToUsername());
        assertEquals("Msg from 1 to 7", messagesOutputDto.getMessageContent());
        assertEquals(fixedLdt, messagesOutputDto.getSentDate());
        assertEquals(fixedLdt.plusDays(1), messagesOutputDto.getReadDate());
    }

    @Test
    void testReadAllMessagesByToUser() {
        List<MessagesOutputDto> messagesOutputDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE, 666666007)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesOutputDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        int ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck = 3;
        for (int i = 0; i < ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck; i++) {
            String indexValue = String.valueOf(i + 1);
            assertEquals(indexValue, messagesOutputDtoList.get(i).getId());
            assertEquals("Msg from " + indexValue + " to 7", messagesOutputDtoList.get(i).getMessageContent());
            assertNotNull(messagesOutputDtoList.get(i).getSentDate());
        }
    }

    @Test
    void testReadAllMessagesByToUserAnotherUser() {
        List<MessagesOutputDto> messagesOutputDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE, 666666001)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesOutputDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        assertEquals("4", messagesOutputDtoList.get(0).getId());
        assertEquals("u007", messagesOutputDtoList.get(0).getFromUsername());
        assertEquals("manager", messagesOutputDtoList.get(0).getToUsername());
        assertEquals("Msg from 7 to 1", messagesOutputDtoList.get(0).getMessageContent());
        assertEquals(fixedLdt.plusDays(6), messagesOutputDtoList.get(0).getSentDate());
        assertNull(messagesOutputDtoList.get(0).getReadDate());
    }

    @Test
    void testReadAllUnReadMessagesByToUser() {
        List<MessagesOutputDto> messagesOutputDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE + UNREAD, 666666002)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesOutputDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        assertEquals("5", messagesOutputDtoList.get(0).getId());
        assertEquals("u007", messagesOutputDtoList.get(0).getFromUsername());
        assertEquals("u002", messagesOutputDtoList.get(0).getToUsername());
        assertEquals("Msg from 7 to 2", messagesOutputDtoList.get(0).getMessageContent());
        assertEquals(fixedLdt.plusDays(8), messagesOutputDtoList.get(0).getSentDate());
        assertNull(messagesOutputDtoList.get(0).getReadDate());
    }

    @Test
    void testReadAllUnReadMessagesByToUserCheckNoReads() {
        List<MessagesOutputDto> messagesDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE + UNREAD, 666666007)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesOutputDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        for (MessagesOutputDto messagesOutputDto : messagesDtoList) {
            assertNotNull(messagesOutputDto.getSentDate());
            assertNull(messagesOutputDto.getReadDate());
        }
    }

    @Test
    void testCreateMessage() {
        MessagesCreationDto messagesCreationDto = new MessagesCreationDto(
                "666666000",
                "666666007",
                "Msg",
                fixedLdt.plusDays(6)
        );
        MessagesDto messagesDtoResponse = this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + MESSAGES)
                .body(BodyInserters.fromObject(messagesCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
        assertEquals("666666000", messagesDtoResponse.getFromUser().getMobile());
        assertEquals("666666007", messagesDtoResponse.getToUser().getMobile());
        assertEquals("Msg", messagesDtoResponse.getMessageContent());
        assertEquals(fixedLdt.plusDays(6), messagesDtoResponse.getSentDate());
        assertNull(messagesDtoResponse.getReadDate());
    }

    @Test
    void testMarkMessageAsRead() {
        String idToLookFor = "3";
        MessagesDto messagesDtoResponse = this.restService.loginAdmin(webTestClient)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + MESSAGES + MESSAGES_ID)
                        .queryParam("readDate", fixedLdt.plusDays(6).toString())
                        .build(idToLookFor))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
        assertEquals(idToLookFor, messagesDtoResponse.getId());
        assertNotNull(messagesDtoResponse.getFromUser());
        assertEquals("666666003", messagesDtoResponse.getFromUser().getMobile());
        assertNotNull(messagesDtoResponse.getToUser());
        assertEquals("666666007", messagesDtoResponse.getToUser().getMobile());
        assertEquals("Msg from 3 to 7", messagesDtoResponse.getMessageContent());
        assertEquals(fixedLdt.plusDays(4), messagesDtoResponse.getSentDate());
        assertEquals(fixedLdt.plusDays(6), messagesDtoResponse.getReadDate());
    }
}
