package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Budget;
import es.upm.miw.betca_tpv_spring.documents.Shopping;
import es.upm.miw.betca_tpv_spring.documents.ShoppingState;
import es.upm.miw.betca_tpv_spring.dtos.BudgetCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.ShoppingDto;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class BudgetControllerIT {

    @Autowired
    private BudgetController budgetController;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private LinkedList<Budget> listBudget;

    @BeforeEach
    void seedDataBudget() {
        listBudget = new LinkedList<>();
        Stream.iterate(0, i -> i + 1).limit(10)
                .map(i -> new Budget(new Shopping[]{
                        new Shopping(1, new BigDecimal("0"), ShoppingState.COMMITTED,
                                "8400000000017", "Zarzuela - Falda T2",   new BigDecimal("20")),
                        new Shopping(3, new BigDecimal("50"), ShoppingState.NOT_COMMITTED,
                                "8400000000024", "Zarzuela - Falda T4",   new BigDecimal("27.8"))
                }))
                .forEach(listBudget::add);
        this.budgetRepository.saveAll(listBudget);
    }

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

    @Test
    void testRead() {
        StepVerifier
                .create(this.budgetController.readBudget( this.listBudget.get(1).getId()))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadWithChangesNewDiscount() {
        Article articleToUpdate = this.articleRepository.findById("8400000000024").get();
        articleToUpdate.setRetailPrice(new BigDecimal("250"));
        this.articleRepository.save(articleToUpdate);
        StepVerifier
                .create(this.budgetController.readBudget( this.listBudget.get(1).getId())).
                expectNextMatches(budget -> {
                    assertNotNull(budget.getCreationDate());
                    assertNotNull(budget.getShoppingCart());
                    assertEquals(new BigDecimal("250"), budget.getShoppingCart().get(1).getRetailPrice());
                    assertEquals(new BigDecimal("94.4400"), budget.getShoppingCart().get(1).getDiscount());
                    return true;
                }).expectComplete()
                .verify();
    }

    @Test
    void testReadWithChangesNewPrice() {
        Article articleToUpdate = this.articleRepository.findById("8400000000024").get();
        articleToUpdate.setRetailPrice(new BigDecimal("5"));
        this.articleRepository.save(articleToUpdate);
        StepVerifier
                .create(this.budgetController.readBudget( this.listBudget.get(1).getId())).
                expectNextMatches(budget -> {
                    assertNotNull(budget.getCreationDate());
                    assertNotNull(budget.getShoppingCart());
                    assertEquals(new BigDecimal("5"), budget.getShoppingCart().get(1).getRetailPrice());
                    assertEquals(new BigDecimal("0"), budget.getShoppingCart().get(1).getDiscount());
                    return true;
                }).expectComplete()
                .verify();
    }

    @Test
    void testReadBudgetNotFound() {
        StepVerifier
                .create(this.budgetController.readBudget( "notFound"))
                .expectError(NotFoundException.class)
                .verify();
    }

}
