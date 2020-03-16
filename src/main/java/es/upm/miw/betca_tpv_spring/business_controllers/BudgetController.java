package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.Budget;
import es.upm.miw.betca_tpv_spring.documents.Shopping;
import es.upm.miw.betca_tpv_spring.documents.ShoppingState;
import es.upm.miw.betca_tpv_spring.dtos.BudgetCreationInputDto;
import es.upm.miw.betca_tpv_spring.repositories.BudgetReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Controller
public class BudgetController {

    private PdfService pdfService;
    private BudgetReactRepository budgetReactRepository;

    @Autowired
    public BudgetController(BudgetReactRepository budgetReactRepository,
                            PdfService pdfService) {
        this.budgetReactRepository = budgetReactRepository;
        this.pdfService = pdfService;
    }

    public Mono<Budget> createBudget(BudgetCreationInputDto budgetCreationInputDto) {
        Shopping[] shoppingArray = budgetCreationInputDto.getShoppingCart().stream().map(shoppingDto ->
                new Shopping(shoppingDto.getAmount(), shoppingDto.getDiscount(),
                        shoppingDto.isCommitted() ? ShoppingState.COMMITTED : ShoppingState.NOT_COMMITTED,
                        shoppingDto.getCode(), shoppingDto.getDescription(), shoppingDto.getRetailPrice()))
                .toArray(Shopping[]::new);
        Budget budget = new Budget(shoppingArray);
        return this.budgetReactRepository.save(budget);
    }

    @Transactional
    public Mono<byte[]> createBudgetAndPdf(BudgetCreationInputDto budgetCreationInputDto) {
        return pdfService.generateBudget(this.createBudget(budgetCreationInputDto));
    }
}
