package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.BudgetController;
import es.upm.miw.betca_tpv_spring.dtos.BudgetCreationInputDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(BudgetResource.BUDGETS)
public class BudgetResource {

    public static final String BUDGETS = "/budgets";
    private BudgetController budgetController;

    @Autowired
    public BudgetResource(BudgetController budgetController) {
        this.budgetController = budgetController;
    }

    @PostMapping(produces = {"application/pdf", "application/json"})
    public Mono<byte[]> createBudget(@Valid @RequestBody BudgetCreationInputDto budgetCreationInputDto) {
        return this.budgetController.createBudgetAndPdf(budgetCreationInputDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }
}
