package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class InvoiceReactRepositoryIT {

    @Autowired
    private InvoiceReactRepository invoiceReactRepository;

    @Autowired
    private TicketReactRepository ticketReactRepository;

    @Test
    void testFindAllAndDatabaseSeeder() {
        StepVerifier
                .create(this.invoiceReactRepository.findAll())
                .expectNextMatches(invoice -> {
                    assertEquals(1, invoice.simpleId());
                    assertEquals("20201", invoice.getId());
                    assertNotNull(invoice.getCreationDate());
                    assertNotNull(invoice.getUser());
                    assertNotNull(invoice.getTicket());
                    assertFalse(invoice.toString().matches("@"));
                    return true;
                })
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    @Test
    void testfindFirstByOrderByCreationDateDescIdDesc() {
        StepVerifier
                .create(this.invoiceReactRepository.findFirstByOrderByCreationDateDescIdDesc())
                .expectNextMatches(invoice -> {
                    assertEquals(3, invoice.simpleId());
                    assertEquals("20203", invoice.getId());
                    assertNotNull(invoice.getCreationDate());
                    assertNotNull(invoice.getUser());
                    assertNotNull(invoice.getTicket());
                    assertFalse(invoice.toString().matches("@"));
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindFirstByTicketAndTaxGreaterThanEqual() {
        Mono<Ticket> ticketMono = ticketReactRepository.findById("201901125");
        StepVerifier
                .create(this.invoiceReactRepository.findFirstByTicketAndTaxGreaterThanEqual(ticketMono, BigDecimal.ZERO))
                .expectNextMatches(invoice -> {
                    assertEquals(3, invoice.simpleId());
                    assertEquals("20203", invoice.getId());
                    assertNotNull(invoice.getCreationDate());
                    assertNotNull(invoice.getUser());
                    assertNotNull(invoice.getTicket());
                    assertTrue(invoice.getTax().compareTo(BigDecimal.ZERO) >= 0);
                    assertFalse(invoice.toString().matches("@"));
                    return true;
                })
                .expectComplete()
                .verify();
    }
}
