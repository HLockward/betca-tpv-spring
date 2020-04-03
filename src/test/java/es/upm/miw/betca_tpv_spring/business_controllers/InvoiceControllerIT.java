package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.data_services.DatabaseSeederService;
import es.upm.miw.betca_tpv_spring.documents.Quarter;
import es.upm.miw.betca_tpv_spring.documents.Shopping;
import es.upm.miw.betca_tpv_spring.documents.ShoppingState;
import es.upm.miw.betca_tpv_spring.documents.Ticket;
import es.upm.miw.betca_tpv_spring.dtos.InvoiceFilterDto;
import es.upm.miw.betca_tpv_spring.dtos.InvoiceNegativeCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.ShoppingDto;
import es.upm.miw.betca_tpv_spring.repositories.InvoiceReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.TicketRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class InvoiceControllerIT {

    @Autowired
    private InvoiceController invoiceController;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @Autowired
    private InvoiceReactRepository invoiceReactRepository;

    @AfterEach
    void initialize() {
        databaseSeederService.deleteAllAndInitializeAndSeedDataBase();
    }

    @Test
    void testCreateInvoice() {
        StepVerifier
                .create(this.invoiceController.createAndPdf())
                .expectNextMatches(invoice -> {
                    assertNotNull(invoice);
                    assertTrue(invoice.length > 0);
                    return true;
                })
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.invoiceReactRepository.findById("20204"))
                .expectNextMatches(invoice -> {
                    assertNotNull(invoice.getCreationDate());
                    assertNotNull(invoice.getTicket());
                    assertNotNull(invoice.getUser());
                    assertEquals(new BigDecimal("0.662400"), invoice.getBaseTax());
                    assertEquals(new BigDecimal("0.027600"), invoice.getTax());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateInvoiceErrorUserNotCompleted() {
        Optional<Ticket> ticketOptional1 = ticketRepository.findById("201901125");
        ticketOptional1.ifPresent(ticket -> ticketRepository.delete(ticket));
        Optional<Ticket> ticketOptional2 = ticketRepository.findById("201901126");
        ticketOptional2.ifPresent(ticket -> ticketRepository.delete(ticket));
        StepVerifier
                .create(this.invoiceController.createAndPdf())
                .expectErrorMatches(error -> {
                    assertEquals("Bad Request Exception (400). User not completed", error.getMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void testCreateInvoiceErrorNotTicket() {
        ticketRepository.deleteAll();
        StepVerifier
                .create(this.invoiceController.createAndPdf())
                .expectErrorMatches(error -> {
                    assertEquals("Not Found Exception (404). Last Ticket not found", error.getMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void testCreateInvoiceErrorTicketIsDebt() {
        ticketRepository.deleteById("201901122");
        ticketRepository.deleteById("201901123");
        ticketRepository.deleteById("201901124");
        ticketRepository.deleteById("201901125");
        ticketRepository.deleteById("201901126");
        StepVerifier
                .create(this.invoiceController.createAndPdf())
                .expectErrorMatches(error -> {
                    assertEquals("Bad Request Exception (400). Ticket is debt", error.getMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void testCreateInvoiceErrorArticleNotFound() {
        Optional<Ticket> ticketOptional = ticketRepository.findById("201901126");
        ticketOptional.ifPresent(ticket -> {
            Shopping[] shoppingList = {new Shopping(2, BigDecimal.ZERO, ShoppingState.COMMITTED, "875675567",
                    "aaa", BigDecimal.TEN)};
            ticket.setShoppingList(shoppingList);
            ticketRepository.save(ticket);
        });
        StepVerifier
                .create(this.invoiceController.createAndPdf())
                .expectErrorMatches(error -> {
                    assertEquals("Not Found Exception (404). Article(875675567)", error.getMessage());
                    return true;
                })
                .verify();
    }


    @Test
    void testUpdateInvoice() {
        Optional<Ticket> ticketOptional = ticketRepository.findById("201901121");
        ticketOptional.ifPresent(ticket -> {
            Shopping[] shoppingList = ticket.getShoppingList();
            shoppingList[0].setAmount(1);
            ticket.setShoppingList(shoppingList);
            ticketRepository.save(ticket);
        });

        StepVerifier
                .create(this.invoiceController.updateAndPdf("20201"))
                .expectNextMatches(invoice -> {
                    assertNotNull(invoice);
                    assertTrue(invoice.length > 0);
                    return true;
                })
                .expectComplete()
                .verify();

        StepVerifier
                .create(invoiceReactRepository.findById("20201"))
                .expectNextMatches(invoice -> {
                    assertNotNull(invoice.getCreationDate());
                    assertNotNull(invoice.getTicket());
                    assertNotNull(invoice.getUser());
                    assertEquals(new BigDecimal("14.2200"), invoice.getBaseTax());
                    assertEquals(new BigDecimal("3.7800"), invoice.getTax());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateInvoiceNotFound() {
        StepVerifier
                .create(this.invoiceController.updateAndPdf("12234"))
                .expectErrorMatches(throwable -> {
                    assertEquals("Not Found Exception (404). Invoice(12234)", throwable.getMessage());
                    return true;
                })
                .verify();
    }


    @Test
    void testCreateNegativeInvoice() {
        List<ShoppingDto> shoppings = new ArrayList<>();
        shoppings.add(new ShoppingDto("8400000000055", "descrip-a5", new BigDecimal("0.23"),
                -2, new BigDecimal("50"), new BigDecimal("0.23"), true));
        InvoiceNegativeCreationInputDto invoiceNegativeCreationInputDto = new InvoiceNegativeCreationInputDto("201901125", shoppings);
        StepVerifier
                .create(this.invoiceController.createNegativeAndPdf(invoiceNegativeCreationInputDto))
                .expectNextMatches(invoice -> {
                    assertNotNull(invoice);
                    assertTrue(invoice.length > 0);
                    return true;
                })
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.invoiceReactRepository.findById("20204"))
                .expectNextMatches(invoice -> {
                    assertNotNull(invoice.getCreationDate());
                    assertNotNull(invoice.getTicket());
                    assertNotNull(invoice.getUser());
                    assertEquals(new BigDecimal("-0.220800"), invoice.getBaseTax());
                    assertEquals(new BigDecimal("-0.009200"), invoice.getTax());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateNegativeInvoiceErrorAmountPositive() {
        List<ShoppingDto> shoppings = new ArrayList<>();
        shoppings.add(new ShoppingDto("8400000000055", "descrip-a5", new BigDecimal("0.23"),
                2, new BigDecimal("50"), new BigDecimal("0.23"), true));
        InvoiceNegativeCreationInputDto invoiceNegativeCreationInputDto = new InvoiceNegativeCreationInputDto("201901125", shoppings);
        StepVerifier
                .create(this.invoiceController.createNegativeAndPdf(invoiceNegativeCreationInputDto))
                .expectErrorMatches(throwable -> {
                    assertEquals("Bad Request Exception (400). Shopping Amount not allowed (2)", throwable.getMessage());
                    return true;
                })
                .verify();
    }


    @Test
    void testCreateNegativeInvoiceErrorPositiveInvoiceNotFound() {
        List<ShoppingDto> shoppings = new ArrayList<>();
        InvoiceNegativeCreationInputDto invoiceNegativeCreationInputDto = new InvoiceNegativeCreationInputDto("201901123", shoppings);
        StepVerifier
                .create(this.invoiceController.createNegativeAndPdf(invoiceNegativeCreationInputDto))
                .expectErrorMatches(throwable -> {
                    assertEquals("Not Found Exception (404). Positive Invoice not found", throwable.getMessage());
                    return true;
                })
                .verify();
    }


    @Test
    void testCreateNegativeInvoiceErrorTicketNotFound() {
        List<ShoppingDto> shoppings = new ArrayList<>();
        InvoiceNegativeCreationInputDto invoiceNegativeCreationInputDto = new InvoiceNegativeCreationInputDto("999", shoppings);
        StepVerifier
                .create(this.invoiceController.createNegativeAndPdf(invoiceNegativeCreationInputDto))
                .expectErrorMatches(throwable -> {
                    assertEquals("Not Found Exception (404). Ticket(999)", throwable.getMessage());
                    return true;
                })
                .verify();
    }

    @Test
    void testReadAll() {
        StepVerifier
                .create(this.invoiceController.readAll())
                .expectNextMatches(invoice -> {
                    assertEquals("20201", invoice.getInvoice());
                    assertEquals("201901122", invoice.getTicket());
                    assertEquals("666666004", invoice.getMobile());
                    return true;
                })
                .expectNextCount(1)
                .thenCancel()
                .verify();
    }

    @Test
    void readAllByFilters() {
        InvoiceFilterDto invoiceFilterDto = new InvoiceFilterDto("666666005",
                LocalDateTime.now().minusDays(1).toLocalDate().format(DateTimeFormatter.ISO_DATE),
                LocalDateTime.now().plusDays(1).toLocalDate().format(DateTimeFormatter.ISO_DATE));
        StepVerifier
                .create(this.invoiceController.readAllByFilters(invoiceFilterDto))
                .expectNextMatches(invoice -> {
                    assertEquals("20202", invoice.getInvoice());
                    assertEquals("201901126", invoice.getTicket());
                    assertEquals("666666005", invoice.getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void readAllByFiltersNullMobile() {
        InvoiceFilterDto invoiceFilterDto = new InvoiceFilterDto(null,
                LocalDateTime.now().minusDays(1).toLocalDate().format(DateTimeFormatter.ISO_DATE)
                , LocalDateTime.now().plusDays(1).toLocalDate().format(DateTimeFormatter.ISO_DATE));
        StepVerifier
                .create(this.invoiceController.readAllByFilters(invoiceFilterDto))
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadQuarterlyVatFoundInvoices() {
        StepVerifier
                .create(this.invoiceController.readQuarterlyVat(Quarter.Q2))
                .expectNextMatches(quarterVATDto -> {
                    assertNotEquals(quarterVATDto.getTaxes().size(), BigDecimal.ZERO);
                    assertNotEquals(quarterVATDto.getTaxes().get(0).getVat(), BigDecimal.ZERO);
                    assertNotEquals(quarterVATDto.getTaxes().get(0).getTaxableAmount(), BigDecimal.ZERO);
                    assertNotEquals(quarterVATDto.getTaxes().get(1).getVat(), BigDecimal.ZERO);
                    assertNotEquals(quarterVATDto.getTaxes().get(1).getTaxableAmount(), BigDecimal.ZERO);
                    assertNotEquals(quarterVATDto.getTaxes().get(2).getVat(), BigDecimal.ZERO);
                    assertNotEquals(quarterVATDto.getTaxes().get(2).getTaxableAmount(), BigDecimal.ZERO);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadQuarterlyVatNoInvoices() {
        StepVerifier
                .create(this.invoiceController.readQuarterlyVat(Quarter.Q4))
                .expectNextMatches(quarterVATDto -> {
                    assertNotEquals(quarterVATDto.getTaxes().size(), 0);
                    assertEquals(quarterVATDto.getTaxes().get(0).getVat(), BigDecimal.ZERO);
                    assertEquals(quarterVATDto.getTaxes().get(0).getTaxableAmount(), BigDecimal.ZERO);
                    assertEquals(quarterVATDto.getTaxes().get(1).getVat(), BigDecimal.ZERO);
                    assertEquals(quarterVATDto.getTaxes().get(1).getTaxableAmount(), BigDecimal.ZERO);
                    assertEquals(quarterVATDto.getTaxes().get(2).getVat(), BigDecimal.ZERO);
                    assertEquals(quarterVATDto.getTaxes().get(2).getTaxableAmount(), BigDecimal.ZERO);
                    return true;
                })
                .expectComplete()
                .verify();
    }

}
