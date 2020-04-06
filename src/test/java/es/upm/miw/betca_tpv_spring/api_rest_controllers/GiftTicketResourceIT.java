package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.data_services.DatabaseSeederService;
import es.upm.miw.betca_tpv_spring.dtos.GiftTicketCreationDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;


@ApiTestConfig
class GiftTicketResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @AfterEach
    void initialize() {
        databaseSeederService.deleteAllAndInitializeAndSeedDataBase();
    }

    @Test
    void testCreateGiftTicket() {
        GiftTicketCreationDto giftTicketCreationDto = new GiftTicketCreationDto("Este regalo es para ti");
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + GiftTicketResource.GIFT_TICKETS)
                .body(BodyInserters.fromObject(giftTicketCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateGiftTicketWithoutMessage() {
        GiftTicketCreationDto giftTicketCreationDto = new GiftTicketCreationDto(null);
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + GiftTicketResource.GIFT_TICKETS)
                .body(BodyInserters.fromObject(giftTicketCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }
}
