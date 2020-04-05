package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.MESSAGES;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.MESSAGES_ID;
//import static es.upm.miw.betca_tpv_spring.api_rest_controllers.MessagesResource.MY_MESSAGES;
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
        List<MessagesDto> messagesDtoList = this.restService.loginAdmin(this.webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                        .path(contextPath + MESSAGES)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        int ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck = 3;
        for (int i = 0; i < ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck; i++) {
            String indexValue = String.valueOf(i + 1);
            assertEquals(indexValue, messagesDtoList.get(i).getId());
            assertEquals(String.valueOf(i + 666666001), messagesDtoList.get(i).getFromUser().getMobile());
            assertEquals(String.valueOf(666666007), messagesDtoList.get(i).getToUser().getMobile());
            assertEquals("Msg from " + indexValue + " to 7", messagesDtoList.get(i).getMessageContent());
            assertNotNull(messagesDtoList.get(i).getSentDate());
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
    void testReadMessage() {
        String idToLookFor = "1";
        MessagesDto messagesDtoResponse = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + MESSAGES + MESSAGES_ID, idToLookFor)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
        assertEquals(idToLookFor, messagesDtoResponse.getId());
        assertNotNull(messagesDtoResponse.getFromUser());
        assertEquals("666666001", messagesDtoResponse.getFromUser().getMobile());
        assertNotNull(messagesDtoResponse.getToUser());
        assertEquals("666666007", messagesDtoResponse.getToUser().getMobile());
        assertEquals("Msg from 1 to 7", messagesDtoResponse.getMessageContent());
        assertEquals(fixedLdt, messagesDtoResponse.getSentDate());
        assertEquals(fixedLdt.plusDays(1), messagesDtoResponse.getReadDate());
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

    @Test
    void testReadAllMessagesToUser() {
        List<MessagesDto> messagesDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE, 666666007)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        int ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck = 3;
        for (int i = 0; i < ItemsOfTheCollectionMessagesInTheSeederWeWantToCheck; i++) {
            String indexValue = String.valueOf(i + 1);
            assertEquals(indexValue, messagesDtoList.get(i).getId());
            assertEquals(String.valueOf(i + 666666001), messagesDtoList.get(i).getFromUser().getMobile());
            assertEquals(String.valueOf(666666007), messagesDtoList.get(i).getToUser().getMobile());
            assertEquals("Msg from " + indexValue + " to 7", messagesDtoList.get(i).getMessageContent());
            assertNotNull(messagesDtoList.get(i).getSentDate());
        }
    }

    @Test
    void testReadAllMessagesToUserAnotherUser() {
        List<MessagesDto> messagesDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE, 666666001)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        assertEquals("4", messagesDtoList.get(0).getId());
        assertEquals(String.valueOf(666666007), messagesDtoList.get(0).getFromUser().getMobile());
        assertEquals(String.valueOf(666666001), messagesDtoList.get(0).getToUser().getMobile());
        assertEquals("Msg from 7 to 1", messagesDtoList.get(0).getMessageContent());
        assertEquals(fixedLdt.plusDays(6), messagesDtoList.get(0).getSentDate());
        assertNull(messagesDtoList.get(0).getReadDate());
    }

    @Test
    void testReadAllUnReadMessagesByToUser() {
        List<MessagesDto> messagesDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE + UNREAD, 666666002)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        assertEquals("5", messagesDtoList.get(0).getId());
        assertEquals(String.valueOf(666666007), messagesDtoList.get(0).getFromUser().getMobile());
        assertEquals(String.valueOf(666666002), messagesDtoList.get(0).getToUser().getMobile());
        assertEquals("Msg from 7 to 2", messagesDtoList.get(0).getMessageContent());
        assertEquals(fixedLdt.plusDays(8), messagesDtoList.get(0).getSentDate());
        assertNull(messagesDtoList.get(0).getReadDate());
    }

    @Test
    void testReadAllUnReadMessagesByToUserCheckNoReads() {
        List<MessagesDto> messagesDtoList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + MESSAGES + TO_USER + MOBILE + UNREAD, 666666007)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessagesDto.class)
                .value(Assertions::assertNotNull)
                .value(list -> assertTrue(list.size() >= 1))
                .returnResult().getResponseBody();
        for (MessagesDto messagesDto : messagesDtoList) {
            assertNotNull(messagesDto.getSentDate());
            assertNull(messagesDto.getReadDate());
        }
    }
}
