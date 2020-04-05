package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Budget;
import es.upm.miw.betca_tpv_spring.documents.Shopping;
import es.upm.miw.betca_tpv_spring.documents.ShoppingState;
import es.upm.miw.betca_tpv_spring.dtos.BudgetCreationInputDto;
import es.upm.miw.betca_tpv_spring.dtos.BudgetDto;
import es.upm.miw.betca_tpv_spring.dtos.ShoppingDto;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.BudgetReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BudgetController {

    private PdfService pdfService;
    private BudgetReactRepository budgetReactRepository;
    private ArticleReactRepository articleReactRepository;
    @Autowired
    public BudgetController(BudgetReactRepository budgetReactRepository, PdfService pdfService, ArticleReactRepository articleReactRepository) {
        this.budgetReactRepository = budgetReactRepository;
        this.pdfService = pdfService;
        this.articleReactRepository = articleReactRepository;
    }

    public Mono<BudgetDto> readBudget(String code) {
        List<Mono<ShoppingDto>> listShoppingDtoMono = new ArrayList<>();
        BudgetDto budgetDto = new BudgetDto();
        Mono<BudgetDto> budgetDtoMono = this.budgetReactRepository.findById(code).
                switchIfEmpty(Mono.error(new NotFoundException("Not found" + code))).
                map(BudgetDto::new).doOnNext(budget->{
            budgetDto.setShoppingCart(budget.getShoppingCart());
            budgetDto.setCreationDate(budget.getCreationDate());});

        Flux<ShoppingDto> shoppingDtoFlux = budgetDtoMono.
                map(budgetMap -> {
                    budgetMap.getShoppingCart().forEach(shDto ->
                            listShoppingDtoMono.add(getActualArticle(shDto))
                    );
                    return budgetMap;
                })
                .thenMany(Flux.merge(listShoppingDtoMono))
                .distinct()
                .map(shoppingDto -> new ShoppingDto(shoppingDto.getCode(), shoppingDto.getDescription(), shoppingDto.getRetailPrice(), shoppingDto.getAmount(), shoppingDto.getDiscount(), shoppingDto.getTotal(), shoppingDto.isCommitted()));

        return Mono.when(shoppingDtoFlux).then(
                Mono.just(budgetDto));
    }

    private Mono<ShoppingDto> getActualArticle(ShoppingDto shDto){
        ShoppingDto shoppingDtoReturn = new ShoppingDto();
        Mono<Article> shoppingDtoMono= this.articleReactRepository.findById(shDto.getCode()).doOnNext(articleFor -> {
            if (articleFor.getRetailPrice().compareTo(shDto.getRetailPrice()) < 0) {
                shDto.setRetailPrice(articleFor.getRetailPrice());
                shDto.setDiscount(BigDecimal.ZERO);
                shDto.setTotal(articleFor.getRetailPrice().multiply(new BigDecimal(shDto.getAmount())));
            } else if (articleFor.getRetailPrice().compareTo(shDto.getRetailPrice()) > 0) {
                BigDecimal percent = new BigDecimal("100").subtract((shDto.getTotal().divide((articleFor.getRetailPrice().multiply(new BigDecimal(shDto.getAmount()))), MathContext.DECIMAL128).multiply(new BigDecimal("100"))));
                BigDecimal newTotal = shDto.getTotal().subtract(shDto.getTotal().multiply(percent.divide(new BigDecimal("100"))));
                if (shDto.getTotal().compareTo(newTotal) > 0) {
                    shDto.setDiscount(percent);
                    shDto.setRetailPrice(articleFor.getRetailPrice());
                }
            }
            shoppingDtoReturn.setRetailPrice(shDto.getRetailPrice());
            shoppingDtoReturn.setTotal(shDto.getTotal());
            shoppingDtoReturn.setDiscount(shDto.getDiscount());
            shoppingDtoReturn.setAmount(shDto.getAmount());
            shoppingDtoReturn.setCode(shDto.getCode());
            shoppingDtoReturn.setCommitted(shDto.isCommitted());
            shoppingDtoReturn.setDescription(shDto.getDescription());
        });

        return Mono.when(shoppingDtoMono).then(Mono.just(shoppingDtoReturn));
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