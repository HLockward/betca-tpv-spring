package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.ArticlesFamilyController;
import es.upm.miw.betca_tpv_spring.dtos.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RequestMapping(ArticlesFamilyResource.ARTICLES_FAMILY)
@RestController
public class ArticlesFamilyResource {

    public static final String ARTICLES_FAMILY = "/articles-family";
    public static final String FAMILY_COMPOSITE = "/familydescription";
    public static final String SIZES = "/sizes";
    public static final String ARTICLES_FAMILY_ID = "/{id}";
    @Autowired
    private ArticlesFamilyController articlesFamilyController;

    @GetMapping(value = FAMILY_COMPOSITE)
    public List<ArticleFamilyCompleteDto> readInFamilyComposite(@Valid @RequestParam String description) {
        return articlesFamilyController.readFamilyCompositeArticlesList(description);
    }

    @GetMapping(value = SIZES)
    public List<String> readSizes() throws IOException {
        return articlesFamilyController.readSizes();
    }

    @PostMapping
    public Mono<ArticlesFamilyDto> createArticleFamily(@Valid @RequestBody FamilyCompleteDto articleFamilyDto) throws IOException {
        return articlesFamilyController.createArticleFamily(articleFamilyDto);
    }

    @GetMapping(value = ARTICLES_FAMILY_ID)
    public Mono<ArticlesFamilyCrudDto> searchArticlesFamilyById(@PathVariable String id){
        return this.articlesFamilyController.searchArticlesFamilyById(id)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping
    public Flux<ArticlesFamilyCrudDto> searchArticlesFamily(@RequestParam(required = false) String reference,
                                                        @RequestParam(required = false) String familyType){
        ArticlesFamilySearchDto articlesFamilySearchDto = new ArticlesFamilySearchDto(reference,familyType);
        if(reference == null && familyType == null){
            return this.articlesFamilyController.readAllArticlesFamily()
                    .doOnEach(log -> LogManager.getLogger(this.getClass()).debug(log));
        }else{
            return this.articlesFamilyController.searchArticlesFamilyByReferenceOrFamilyType(articlesFamilySearchDto)
                    .doOnEach(log -> LogManager.getLogger(this.getClass()).debug(log));
        }

    }


}
