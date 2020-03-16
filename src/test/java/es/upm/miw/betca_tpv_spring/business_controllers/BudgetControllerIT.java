package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.dtos.BudgetCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.ShoppingDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;

@TestConfig
public class BudgetControllerIT {

    @Autowired
    private BudgetController budgetController;

    @Test
    void testCreateBudget() {
        ShoppingDto shoppingDto =
                new ShoppingDto("1", "prueba", BigDecimal.TEN, 1, BigDecimal.ZERO,
                        BigDecimal.TEN, true);
        BudgetCreationInputDto budgetCreationInputDto = new BudgetCreationInputDto(
                Collections.singletonList(shoppingDto));
        StepVerifier
                .create(this.budgetController.createBudgetAndPdf(budgetCreationInputDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }
}
