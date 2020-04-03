package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.Tax;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static es.upm.miw.betca_tpv_spring.data_services.DatabaseSeederService.CUSTOMER_POINTS_NAME;
import static es.upm.miw.betca_tpv_spring.data_services.DatabaseSeederService.VARIOUS_NAME;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ArticleReactRepositoryIT {

    @Autowired
    private ArticleReactRepository articleReactRepository;

    @Test
    void testFindAllAndDatabaseSeeder() {
        StepVerifier
                .create(this.articleReactRepository.findAll())
                .expectNextMatches(article -> {
                    assertEquals("1", article.getCode());
                    assertEquals(VARIOUS_NAME, article.getReference());
                    assertEquals(VARIOUS_NAME, article.getDescription());
                    assertEquals(0, new BigDecimal("100").compareTo(article.getRetailPrice()));
                    assertNotNull(article.getRegistrationDate());
                    assertEquals(Tax.GENERAL, article.getTax());
                    assertFalse(article.isDiscontinued());
                    assertEquals(new Integer(1000), article.getStock());
                    return true;
                })
                .expectNextMatches(article -> {
                    assertEquals("0", article.getCode());
                    assertEquals(CUSTOMER_POINTS_NAME, article.getReference());
                    assertEquals(CUSTOMER_POINTS_NAME, article.getDescription());
                    assertEquals(0, new BigDecimal("0").compareTo(article.getRetailPrice()));
                    assertNotNull(article.getRegistrationDate());
                    assertEquals(Tax.GENERAL, article.getTax());
                    assertFalse(article.isDiscontinued());
                    assertEquals(new Integer(1), article.getStock());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByStockLessThanEquals() {
        StepVerifier
                .create(this.articleReactRepository.findByStockLessThanEqual(10))
                .expectNextMatches(article -> article.getStock() <= 10)
                .expectNextCount(8)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByDescriptionLikeOrReferenceLikeOrStockOrProviderOrRetailPriceOrDiscontinued() {
        StepVerifier
                .create(this.articleReactRepository.findByDescriptionLikeOrReferenceLikeOrStockOrProviderOrRetailPrice(
                        "null", "null", 0, "null", null))
                .expectNextCount(2)
                // .expectNextMatches(article -> article.getDescription().equals("descrip-a6"))
                .expectComplete()
                .verify();
    }

}
