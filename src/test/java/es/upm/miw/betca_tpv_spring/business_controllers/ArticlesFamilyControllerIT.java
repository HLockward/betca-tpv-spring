package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.ArticlesFamily;
import es.upm.miw.betca_tpv_spring.documents.FamilyComposite;
import es.upm.miw.betca_tpv_spring.documents.FamilyType;
import es.upm.miw.betca_tpv_spring.dtos.ArticlesFamilyCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticlesFamilySearchDto;
import es.upm.miw.betca_tpv_spring.dtos.FamilyCompleteDto;
import es.upm.miw.betca_tpv_spring.dtos.ProviderDto;
import es.upm.miw.betca_tpv_spring.repositories.ArticlesFamilyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ArticlesFamilyControllerIT {

    private FamilyType familyType;

    private ArticlesFamily familyComposite = new FamilyComposite(FamilyType.ARTICLES,
            "reference",
            "description");

    @Autowired
    private ArticlesFamilyController articlesFamilyController;

    @Autowired
    private ProviderController providerController;

    @Autowired
    private ArticlesFamilyRepository articlesFamilyRepository;

    @BeforeEach
    void fillArticlesFamilyList() {
        this.familyComposite.add(this.articlesFamilyRepository.findAll().get(0));
        this.familyComposite.add(this.articlesFamilyRepository.findAll().get(9));
        this.familyComposite.add(this.articlesFamilyRepository.findAll().get(12));
    }


    @Test
    void testReadFamilyCompositeArticlesList() {
        assertNotNull(articlesFamilyController.readFamilyCompositeArticlesList("root"));
        assertEquals("varios", articlesFamilyController.readFamilyCompositeArticlesList("root").get(1).getDescription());
        assertEquals(FamilyType.values()[1], articlesFamilyController.readFamilyCompositeArticlesList("root").get(0).getFamilyType());
        assertNotEquals("algo", articlesFamilyController.readFamilyCompositeArticlesList("varios").get(1).getDescription());
        assertEquals(FamilyType.values()[0], articlesFamilyController.readFamilyCompositeArticlesList("varios").get(0).getFamilyType());
    }

    @Test
    void TestCreateFamilyArticle() throws IOException {
        assertNotNull(articlesFamilyController.readSizes());
        Flux<ProviderDto> provider = providerController.readAll();
        FamilyCompleteDto familyCompleteDto = FamilyCompleteDto.builder()
                .description("Jeans").sizeType(false).reference("Zaara")
                .fromSize("0").toSize("40").increment(2).provider(provider.collectList().block().get(0).getId()).build();
        assertNotNull(articlesFamilyController.createArticleFamily(familyCompleteDto));

    }

    @Test
    void testSearchArticlesFamilyById() {
        String id = this.familyComposite.getArticlesFamilyList().get(0).getId();
        StepVerifier
                .create(this.articlesFamilyController.searchArticlesFamilyById(id))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void testReadAllArticlesFamily() {
        StepVerifier
                .create(this.articlesFamilyController.readAllArticlesFamily())
                .expectNextCount(13)
                .thenCancel()
                .verify();
    }

    @Test
    void testSearchArticlesFamilyByReferencesOrFamilyType() {
        ArticlesFamilySearchDto articlesFamilySearchDto =
                new ArticlesFamilySearchDto("", "ARTICLES");
        StepVerifier
                .create(this.articlesFamilyController.searchArticlesFamilyByReferenceOrFamilyType(articlesFamilySearchDto))
                .expectNextCount(3)
                .thenCancel()
                .verify();
    }

    @Test
    void testCreateArticlesFamilyTypeArticle() {
        ArticlesFamilyCreationDto articlesFamilyCreationDto =
                new ArticlesFamilyCreationDto(FamilyType.ARTICLE, "ropa vieja", null, null, "8400000000024");
        StepVerifier
                .create(this.articlesFamilyController.createArticlesFamily(articlesFamilyCreationDto))
                .expectNextMatches(articlesFamilyCrudDto -> {
                    assertEquals(articlesFamilyCrudDto.getArticle().getCode(), articlesFamilyCreationDto.getArticle());
                    assertEquals(articlesFamilyCrudDto.getFamilyType(), articlesFamilyCreationDto.getFamilyType());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateArticlesFamilyTypeArticles() {
        String[] articlesFamilyListId = {
                this.familyComposite.getArticlesFamilyList().get(0).getId(),
                this.familyComposite.getArticlesFamilyList().get(1).getId(),
                this.familyComposite.getArticlesFamilyList().get(2).getId()
        };
        ArticlesFamilyCreationDto articlesFamilyCreationDto =
                new ArticlesFamilyCreationDto(FamilyType.ARTICLES, "ropa vieja", "description", articlesFamilyListId, null);

        StepVerifier
                .create(this.articlesFamilyController.createArticlesFamily(articlesFamilyCreationDto))
                .expectNextMatches(articlesFamilyCrudDto -> {
                    assertEquals(articlesFamilyCrudDto.getReference(), articlesFamilyCreationDto.getReference());
                    assertEquals(articlesFamilyCrudDto.getDescription(), articlesFamilyCreationDto.getDescription());
                    assertEquals(articlesFamilyCrudDto.getFamilyType(), articlesFamilyCreationDto.getFamilyType());
                    assertEquals(articlesFamilyCrudDto.getArticlesFamilyList().size(), articlesFamilyListId.length);
                    assertEquals(articlesFamilyCrudDto.getArticlesFamilyList().get(0).getId(), articlesFamilyListId[0]);
                    assertEquals(articlesFamilyCrudDto.getArticlesFamilyList().get(1).getId(), articlesFamilyListId[1]);
                    assertEquals(articlesFamilyCrudDto.getArticlesFamilyList().get(2).getId(), articlesFamilyListId[2]);
                    return true;
                })
                .expectComplete()
                .verify();

    }

    @Test
    void testUpdateArticlesFamilyTypeArticle() {
        String id = this.familyComposite.getArticlesFamilyList().get(0).getId();
        ArticlesFamilyCreationDto articlesFamilyCreationDto =
                new ArticlesFamilyCreationDto(FamilyType.ARTICLE, "ropa vieja", null, null, "8400000000048");
        StepVerifier
                .create(this.articlesFamilyController.updateArticlesFamily(id, articlesFamilyCreationDto))
                .expectNextMatches(articlesFamilyCrudDto -> {
                    assertEquals(articlesFamilyCrudDto.getId(), id);
                    assertEquals(articlesFamilyCrudDto.getArticle().getCode(), articlesFamilyCreationDto.getArticle());
                    assertEquals(articlesFamilyCrudDto.getFamilyType(), articlesFamilyCreationDto.getFamilyType());
                    return true;
                })
                .expectComplete()
                .verify();

    }

    @Test
    void testUpdateArticlesFamilyTypeArticles() {
        String id = this.familyComposite.getArticlesFamilyList().get(2).getId();

        String[] articlesFamilyListId = {
                this.familyComposite.getArticlesFamilyList().get(0).getId(),
                this.familyComposite.getArticlesFamilyList().get(1).getId()
        };
        ArticlesFamilyCreationDto articlesFamilyCreationDto =
                new ArticlesFamilyCreationDto(FamilyType.ARTICLES, "ropa vieja", null, articlesFamilyListId, null);
        StepVerifier
                .create(this.articlesFamilyController.updateArticlesFamily(id, articlesFamilyCreationDto))
                .expectNextMatches(articlesFamilyCrudDto -> {
                    assertEquals(articlesFamilyCrudDto.getId(), id);
                    assertEquals(articlesFamilyCrudDto.getReference(), articlesFamilyCreationDto.getReference());
                    assertNotEquals(articlesFamilyCrudDto.getDescription(), articlesFamilyCreationDto.getDescription());
                    assertEquals(articlesFamilyCrudDto.getFamilyType(), articlesFamilyCreationDto.getFamilyType());
                    assertEquals(articlesFamilyCrudDto.getArticlesFamilyList().size(), articlesFamilyListId.length);
                    assertEquals(articlesFamilyCrudDto.getArticlesFamilyList().get(0).getId(), articlesFamilyListId[0]);
                    assertEquals(articlesFamilyCrudDto.getArticlesFamilyList().get(1).getId(), articlesFamilyListId[1]);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testDeleteArticleFamilyById(){
        long totalArticlesFamily = this.articlesFamilyRepository.count();
        String id = this.familyComposite.getArticlesFamilyList().get(0).getId();
        StepVerifier
                .create(this.articlesFamilyController.deleteArticlesFamily(id))
                .expectComplete()
                .verify();
        assertEquals(totalArticlesFamily - 1, this.articlesFamilyRepository.count());
    }
}
