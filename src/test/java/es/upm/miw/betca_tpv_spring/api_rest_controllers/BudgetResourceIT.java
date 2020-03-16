package es.upm.miw.betca_tpv_spring.api_rest_controllers;


import es.upm.miw.betca_tpv_spring.dtos.BudgetCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.ShoppingDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.util.Collections;

@ApiTestConfig
class BudgetResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testCreateBudget() {
        ShoppingDto shoppingDto =
                new ShoppingDto("3", "test", new BigDecimal("20.00"), 5, BigDecimal.ZERO,
                        new BigDecimal("200.00"), true);
        BudgetCreationInputDto budgetCreationInputDto = new BudgetCreationInputDto(Collections.singletonList(shoppingDto));
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + BudgetResource.BUDGETS)
                .body(BodyInserters.fromObject(budgetCreationInputDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(byte[].class)
                .value(Assertions::assertNotNull);
    }
    @Test
    void TestCreateBudgetWithoutShoppingCart(){
        ShoppingDto shoppingDto = null;
        BudgetCreationInputDto budgetCreationInputDto = new BudgetCreationInputDto(Collections.singletonList(shoppingDto));

        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + BudgetResource.BUDGETS)
                .body(BodyInserters.fromObject(budgetCreationInputDto))
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
