package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.data_services.DatabaseSeederService;
import es.upm.miw.betca_tpv_spring.documents.ShoppingState;
import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.OrderRepository;
import es.upm.miw.betca_tpv_spring.repositories.TicketRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class TicketControllerIT {

    @Autowired
    private TicketController ticketController;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CashierClosureController cashierClosureController;

    @Autowired
    private DatabaseSeederService databaseSeederService;

    @AfterEach
    void initialize() {
        databaseSeederService.deleteAllAndInitializeAndSeedDataBase();
    }

    @Test
    void testUpdateStockWhenCreateTicket() {
        int stock = this.articleRepository.findById("1").get().getStock();
        StepVerifier
                .create(this.cashierClosureController.createCashierClosureOpened()).expectComplete().verify();
        ShoppingDto shoppingDto =
                new ShoppingDto("1", "prueba", BigDecimal.TEN, 1, BigDecimal.ZERO,
                        BigDecimal.TEN, true);
        TicketCreationInputDto ticketCreationInputDto = new TicketCreationInputDto(null, BigDecimal.TEN
                , BigDecimal.ZERO, BigDecimal.ZERO, Collections.singletonList(shoppingDto), "Nota del ticket...");
        StepVerifier
                .create(this.ticketController.createTicketAndPdf(ticketCreationInputDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
        assertEquals(new Integer(stock - 1), this.articleRepository.findById("1").get().getStock());
        shoppingDto.setAmount(-1);
        StepVerifier
                .create(this.ticketController.createTicketAndPdf(ticketCreationInputDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
        assertEquals(new Integer(stock), this.articleRepository.findById("1").get().getStock());
        StepVerifier
                .create(this.cashierClosureController.close(new CashierClosureInputDto())).expectComplete().verify();
    }

    @Test
    void testSearchOnlyByMobile() {
        String mobile = "666666004";
        TicketSearchDto ticketSearchDto = new TicketSearchDto(mobile, null, null);
        StepVerifier
                .create(this.ticketController.searchByMobileDateOrAmount(ticketSearchDto))
                .expectNextCount(3)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchOnlyByDate() {
        LocalDateTime date = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        TicketSearchDto ticketSearchDto = new TicketSearchDto(null, date, null);

        int expected = this.ticketRepository.findByCreationDateBetween(date, date.plusDays(1)).size();

        StepVerifier
                .create(this.ticketController.searchByMobileDateOrAmount(ticketSearchDto))
                .expectNextCount(expected)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchOnlyByAmount() {
        Integer amount = 6;
        TicketSearchDto ticketSearchDto = new TicketSearchDto(null, null, amount);
        StepVerifier
                .create(this.ticketController.searchByMobileDateOrAmount(ticketSearchDto))
                .expectNextCount(4)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchByMobileDateAndAmount() {
        String mobile = "666666004";
        LocalDateTime date = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        Integer amount = 6;
        TicketSearchDto ticketSearchDto = new TicketSearchDto(mobile, date, amount);
        StepVerifier
                .create(this.ticketController.searchByMobileDateOrAmount(ticketSearchDto))
                .expectNextCount(1)
                .thenCancel()
                .verify();
    }

    @Test
    void testGetNotCommittedByArticle() {
        String articleId = "8400000000024";
        StepVerifier
                .create(this.ticketController.searchNotCommittedByArticle(articleId))
                .expectNextCount(1)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchNotCommittedByOrder() {
        String id = this.orderRepository.findAll().get(0).getId();
        StepVerifier
                .create(this.ticketController.searchNotCommittedByOrder(id))
                .expectNextCount(1)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchNotCommittedByOrderNotFound() {
        String id = "555";
        StepVerifier
                .create(this.ticketController.searchNotCommittedByOrder(id))
                .expectNextCount(0)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchNotCommittedByTag() {
        String tag = "tag1";
        StepVerifier
                .create(this.ticketController.searchNotCommittedByTag(tag))
                .expectNextCount(1)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchNotCommittedByTagNotFound() {
        String tag = "777";
        StepVerifier
                .create(this.ticketController.searchNotCommittedByTag(tag))
                .expectNextCount(0)
                .thenCancel()
                .verify();
    }

    @Test
    void testUpdateShoppingTicket() {
        ShoppingPatchDto shoppingPatchDto1 = new ShoppingPatchDto("8400000000017", 10, ShoppingState.IN_STOCK);
        ShoppingPatchDto shoppingPatchDto2 = new ShoppingPatchDto("8400000000024", 15, ShoppingState.REQUIRE_PROVIDER);
        TicketPatchDto ticketPatchDto = new TicketPatchDto();
        ticketPatchDto.getShoppingPatchDtoList().add(shoppingPatchDto1);
        ticketPatchDto.getShoppingPatchDtoList().add(shoppingPatchDto2);
        StepVerifier
                .create(this.ticketController.updateShoppingTicket("201901121", ticketPatchDto))
                .expectNextMatches(ticketOutputDto -> {
                    assertEquals(ticketOutputDto.getId(), "201901121");
                    assertEquals(ticketOutputDto.getShoppingList()[0].getAmount(), 10);
                    assertEquals(ticketOutputDto.getShoppingList()[0].getShoppingState(), ShoppingState.IN_STOCK);
                    assertEquals(ticketOutputDto.getShoppingList()[1].getAmount(), 15);
                    assertEquals(ticketOutputDto.getShoppingList()[1].getShoppingState(),  ShoppingState.REQUIRE_PROVIDER);
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
