package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.ArticleController;
import es.upm.miw.betca_tpv_spring.dtos.ArticleAdvancedSearchDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleSearchDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.math.BigDecimal;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(ArticleResource.ARTICLES)
public class ArticleResource {

    public static final String ARTICLES = "/articles";
    public static final String CODE_ID = "/{code}";
    public static final String SEARCH = "/search";
    public static final String ADVANCEDSEARCH = "/advancedSearch";
    private ArticleController articleController;

    @Autowired
    public ArticleResource(ArticleController articleController) {
        this.articleController = articleController;
    }

    @GetMapping(value = CODE_ID)
    public Mono<ArticleDto> readArticle(@PathVariable String code) {
        return this.articleController.readArticle(code)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping
    public Flux<ArticleDto> readAll() {
        return this.articleController.readAll()
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PostMapping
    public Mono<ArticleDto> createArticle(@Valid @RequestBody ArticleDto articleDto) {
        return this.articleController.createArticle(articleDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PutMapping(value = CODE_ID)
    public Mono<ArticleDto> updateArticle(@PathVariable String code, @Valid @RequestBody ArticleDto articleDto) {
        return this.articleController.updateArticle(code, articleDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = SEARCH)
    public Flux<ArticleDto> searchArticleByDescriptionOrProvider(@RequestParam(required = false) String description,
                                                                 @RequestParam(required = false) String provider) {
        ArticleSearchDto articleSearchDto = new ArticleSearchDto(description, provider);
        return this.articleController.searchArticleByDescriptionOrProvider(articleSearchDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = ADVANCEDSEARCH)
    public Flux<ArticleDto> searchArticleByDescriptionOrReferenceOrStockOrProviderOrRetailPriceOrDiscontinued(
            @RequestParam(required = false) String description, @RequestParam(required = false) String reference,
            @RequestParam(required = false) Integer stock, @RequestParam(required = false) String provider,
            @RequestParam(required = false) String retailPrice, @RequestParam(required = false) String discontinued) {
        BigDecimal price;
        if (retailPrice == null)
            price = null;
        else
            price = new BigDecimal(retailPrice);
        ArticleAdvancedSearchDto articleAdvancedSearchDto = new ArticleAdvancedSearchDto(description, reference, stock,
                provider, price, Boolean.valueOf(discontinued));
        return this.articleController.searchArticleByDescriptionOrReferenceOrStockOrProviderOrRetailPriceOrDiscontinued(articleAdvancedSearchDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

}
