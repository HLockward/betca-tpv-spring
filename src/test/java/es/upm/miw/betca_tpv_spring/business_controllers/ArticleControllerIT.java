package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.Tax;
import es.upm.miw.betca_tpv_spring.dtos.ArticleAdvancedSearchDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleSearchDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.exceptions.ConflictException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
class ArticleControllerIT {

    @Autowired
    private ArticleController articleController;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProviderRepository providerRepository;

    private ArticleDto articleDto;

    @BeforeEach
    void seed() {
        this.articleDto = new ArticleDto("8400000002345", "descrip", "ref", BigDecimal.TEN, null);
    }

    @Test
    void testRead() {
        StepVerifier
                .create(this.articleController.readArticle("8400000000017"))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadNotFound() {
        StepVerifier
                .create(this.articleController.readArticle("no exist"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testConflictRequestException() {
        this.articleDto.setCode("8400000000017");
        StepVerifier
                .create(this.articleController.createArticle(this.articleDto))
                .expectError(ConflictException.class)
                .verify();
        this.articleDto.setCode("no exist");
    }

    @Test
    void testProviderNotFoundException() {
        this.articleDto.setProvider("no exist");
        StepVerifier
                .create(this.articleController.createArticle(this.articleDto))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testInitStock() {
        StepVerifier
                .create(this.articleController.createArticle(this.articleDto))
                .expectNextMatches(articleDtoCreated ->
                        articleDtoCreated.getStock() != null)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadAll() {
        StepVerifier
                .create(this.articleController.readAll())
                .expectNextCount(10)
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateArticle() {
        ArticleDto articleDto = new ArticleDto("8400000000017", "articulo editado", "referencia editada", BigDecimal.valueOf(34.5), 15);
        articleDto.setProvider(this.providerRepository.findAll().get(1));
        articleDto.setTax(Tax.FREE);
        articleDto.setDiscontinued(false);

        ArticleDto articleDto2 = new ArticleDto("8400000000017", "Zarzuela - Falda T2", "Zz Falda T2", BigDecimal.valueOf(20), 10);
        articleDto2.setProvider(this.providerRepository.findAll().get(0));
        articleDto2.setTax(Tax.GENERAL);
        articleDto2.setDiscontinued(false);
        StepVerifier
                .create(this.articleController.updateArticle("8400000000017", articleDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.articleController.readArticle("8400000000017"))
                .expectNextMatches(articleDto1 -> {
                            assertTrue("articulo editado".equals(articleDto1.getDescription()));
                            assertTrue(this.providerRepository.findAll().get(1).getId().equals(articleDto1.getProvider()));
                            return true;
                        }
                )
                .expectComplete()
                .verify();

        StepVerifier
                .create(this.articleController.updateArticle("8400000000017", articleDto2))
                .expectNextMatches(articleDto1 -> {
                            assertTrue("Zarzuela - Falda T2".equals(articleDto1.getDescription()));
                            assertTrue(this.providerRepository.findAll().get(0).getId().equals(articleDto1.getProvider()));
                            return true;
                        }
                )
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateArticleWithCodeOutOfRange() {
        StepVerifier
                .create(this.articleController.createArticle(new ArticleDto("8400001092832","desc","ref",BigDecimal.TEN,5)))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void testCreateArticleWithCodeRegenerated() {
        ArticleDto articleDto = new ArticleDto("8403001092832","desc","ref",BigDecimal.TEN,5);
        articleDto.setTax(Tax.SUPER_REDUCED);
        StepVerifier
                .create(this.articleController.createArticle(articleDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
        this.articleRepository.deleteById(this.articleRepository.findFirstByOrderByCodeDesc().getCode());

    }

    @Test
    void testSearchArticleByDescriptionOrProvider() {
        ArticleSearchDto articleSearchDto = new ArticleSearchDto("null", this.providerRepository.findAll().get(1).getId());
        StepVerifier
                .create(this.articleController.searchArticleByDescriptionOrProvider(articleSearchDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testSearchArticleByDescriptionOrReferenceOrStockOrProviderOrRetailPriceOrDiscontinued() {
        ArticleAdvancedSearchDto articleAdvancedSearchDto = new ArticleAdvancedSearchDto(null, null, null, null, null, true);
        StepVerifier
                .create(this.articleController.searchArticleByDescriptionOrReferenceOrStockOrProviderOrRetailPriceOrDiscontinued(articleAdvancedSearchDto))
                .expectNextCount(1)
                // .expectNextMatches(articleDto1 -> "descrip-a6".equals(articleDto1.getDescription()))
                .expectComplete()
                .verify();
    }

    @Test
    void testSearchArticleByRetailPrice() {
        ArticleAdvancedSearchDto articleAdvancedSearchDto = new ArticleAdvancedSearchDto("descrip-a6", null, null, null, null, true);
        StepVerifier
                .create(this.articleController.searchArticleByDescriptionOrReferenceOrStockOrProviderOrRetailPriceOrDiscontinued(articleAdvancedSearchDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testSearchArticleByDiscontinuedFalse() {
        ArticleAdvancedSearchDto articleAdvancedSearchDto = new ArticleAdvancedSearchDto(null, null, null, null, null, false);
        articleAdvancedSearchDto.setDescription("null");
        articleAdvancedSearchDto.setReference("null");
        articleAdvancedSearchDto.setStock(null);
        articleAdvancedSearchDto.setProvider("null");
        articleAdvancedSearchDto.setRetailPrice(null);
        articleAdvancedSearchDto.setDiscontinued(false);
        StepVerifier
                .create(this.articleController.searchArticleByDescriptionOrReferenceOrStockOrProviderOrRetailPriceOrDiscontinued(articleAdvancedSearchDto))
                .expectNextCount(9)
                .expectComplete()
                .verify();
    }

    @AfterEach
    void delete() {
        this.articleRepository.deleteById(this.articleDto.getCode());
    }
}
