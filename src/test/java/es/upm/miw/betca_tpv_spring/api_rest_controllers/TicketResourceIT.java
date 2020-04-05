package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.documents.ShoppingState;
import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.repositories.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.CashierClosureResource.CASHIER_CLOSURES;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.TicketResource.*;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ApiTestConfig
class TicketResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testCreateTicket() {
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + CASHIER_CLOSURES)
                .exchange()
                .expectStatus().isOk();
        ShoppingDto shoppingDto =
                new ShoppingDto("1", "prueba", new BigDecimal("100.00"), 1, BigDecimal.ZERO,
                        new BigDecimal("100.00"), true);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto(null, new BigDecimal("100.00")
                , BigDecimal.ZERO, BigDecimal.ZERO, Collections.singletonList(shoppingDto), "Nota del ticket...");
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + TicketResource.TICKETS)
                .body(BodyInserters.fromObject(ticketCreationInputDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
        this.restService.loginAdmin(webTestClient)
                .patch().uri(contextPath + CASHIER_CLOSURES + CashierClosureResource.LAST)
                .body(BodyInserters.fromObject(new CashierClosureInputDto(BigDecimal.ZERO, BigDecimal.ZERO, "")))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateReserve() {
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + CASHIER_CLOSURES)
                .exchange()
                .expectStatus().isOk();
        ShoppingDto shoppingDto =
                new ShoppingDto("1", "", new BigDecimal("100.00"), 1, BigDecimal.ZERO,
                        new BigDecimal("100.00"), false);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto("666666004",
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, Collections.singletonList(shoppingDto),
                "Nota del ticket...");
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + TicketResource.TICKETS)
                .body(BodyInserters.fromObject(ticketCreationInputDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
        this.restService.loginAdmin(webTestClient)
                .patch().uri(contextPath + CASHIER_CLOSURES + CashierClosureResource.LAST)
                .body(BodyInserters.fromObject(new CashierClosureInputDto(BigDecimal.ZERO, BigDecimal.ZERO, "")))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSearchByMobileDateOrAmount() {
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + TICKETS + SEARCH)
                .queryParam("mobile", "666666004")
                .queryParam("date", LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).toString())
                .queryParam("amount", 6)
                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        boolean expected = tickets.size() >= 1;
        assertTrue(expected);
    }

    @Test
    void testSearchMissParam() {
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                        .path(contextPath + TICKETS + SEARCH)
                        .queryParam("date", LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).toString())
                        .queryParam("amount", 6)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        boolean expected = tickets.size() >= 4;
        assertTrue(expected);
    }

    @Test
    void testSearchNotCommittedByArticle() {
        String articleId = "8400000000024";
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + SEARCH_BY_ARTICLE, articleId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        boolean expected = tickets.size() >= 1;
        assertTrue(expected);
    }

    @Test
    void testSearchNotCommittedByOrder() {
        String orderId = this.orderRepository.findAll().get(0).getId();
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + SEARCH_BY_ORDER, orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        boolean expected = tickets.size() >= 1;
        assertTrue(expected);
    }

    @Test
    void testSearchNotCommittedByOrderNotFound() {
        String orderId = "555";
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + SEARCH_BY_ORDER, orderId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        assertEquals(0, tickets.size());
    }

    @Test
    void testSearchNotCommittedByTag() {
        String tag = "tag1";
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + SEARCH_BY_TAG, tag)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        boolean expected = tickets.size() >= 1;
        assertTrue(expected);
    }

    @Test
    void testSearchNotCommittedByTagNotFound() {
        String tag = "777";
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + SEARCH_BY_TAG, tag)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        assertEquals(0, tickets.size());
    }

    @Test
    void testReadAll() {
        List<TicketOutputDto> tickets = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(tickets);
        assertNotEquals(tickets.size(),0);
    }

    @Test
    void testGetTicket() {
        TicketOutputDto ticket = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + "/201901121")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertNotNull(ticket);
        assertEquals(ticket.getId(), "201901121");
    }

    @Test
    void testGetTicketNotFound() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + "/9999999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetPdf() {
        byte[] file = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + "/201901121" + PDF)
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .returnResult().getResponseBody();
        assertNotNull(file);
    }

    @Test
    void testGetPdfNotFound() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TICKETS + "/99999999" + PDF)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateAmountAndShoppingStateTicket() {
        ShoppingPatchDto shoppingPatchDto1 = new ShoppingPatchDto("8400000000017", 10, ShoppingState.IN_STOCK);
        ShoppingPatchDto shoppingPatchDto2 = new ShoppingPatchDto("8400000000024", 15, ShoppingState.REQUIRE_PROVIDER);
        TicketPatchDto ticketPatchDto = new TicketPatchDto();
        ticketPatchDto.getShoppingPatchDtoList().add(shoppingPatchDto1);
        ticketPatchDto.getShoppingPatchDtoList().add(shoppingPatchDto2);
        TicketOutputDto ticketUpdated = this.restService.loginAdmin(webTestClient)
                .patch().uri(contextPath + TICKETS + "/201901121")
                .body(BodyInserters.fromObject(ticketPatchDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TicketOutputDto.class)
                .returnResult().getResponseBody();
        assertEquals(ticketUpdated.getId(), "201901121");
        assertEquals(ticketUpdated.getShoppingList()[0].getAmount(), 10);
        assertEquals(ticketUpdated.getShoppingList()[0].getShoppingState(), ShoppingState.IN_STOCK);
        assertEquals(ticketUpdated.getShoppingList()[1].getAmount(), 15);
        assertEquals(ticketUpdated.getShoppingList()[1].getShoppingState(),  ShoppingState.REQUIRE_PROVIDER);
    }

}
