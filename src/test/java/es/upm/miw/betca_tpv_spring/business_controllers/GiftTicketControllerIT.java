package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.data_services.DatabaseSeederService;
import es.upm.miw.betca_tpv_spring.dtos.GiftTicketCreationDto;
import es.upm.miw.betca_tpv_spring.repositories.GiftTicketRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
public class GiftTicketControllerIT {

    @Autowired
    private GiftTicketController giftTicketController;

    @Autowired
    private GiftTicketRepository giftTicketRepository;

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @AfterEach
    void initialize() {
        databaseSeederService.deleteAllAndInitializeAndSeedDataBase();
    }

    @Test
    void testCreateGiftTicket() {
        GiftTicketCreationDto giftTicketCreationDto = new GiftTicketCreationDto("Este es mi mensaje");
        StepVerifier
                .create(this.giftTicketController.createGiftTicketAndPdf(giftTicketCreationDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

}
