package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.documents.ArticlesFamily;
import es.upm.miw.betca_tpv_spring.documents.FamilyComposite;
import es.upm.miw.betca_tpv_spring.documents.FamilyType;
import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.ArticlesFamilyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.Optional;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.ArticlesFamilyResource.*;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.ProviderResource.PROVIDERS;
import static org.junit.Assert.*;

@ApiTestConfig
public class ArticlesFamilyResourceIT {
    private FamilyType familyType;
    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ArticlesFamilyRepository articlesFamilyRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private ArticlesFamily familyComposite = new FamilyComposite(FamilyType.ARTICLES,
            "reference",
            "description");

    private String[] articlesFamilyIdList;

    @BeforeEach
    void fillArticlesFamilyList(){
        this.familyComposite.add(this.articlesFamilyRepository.findAll().get(0));
        this.familyComposite.add(this.articlesFamilyRepository.findAll().get(9));
        this.familyComposite.add(this.articlesFamilyRepository.findAll().get(12));
    }

    @Test
    void testReadInFamilyCompositeVarios() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ARTICLES_FAMILY + FAMILY_COMPOSITE)
                .queryParam("description", "root").build()
        ).exchange().expectStatus().isOk();
    }


    @Test
    void testReadInFamilyCompositeReturnsServerError() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ARTICLES_FAMILY + FAMILY_COMPOSITE)
                .queryParam("description", "kk").build()
        ).exchange().expectStatus().is5xxServerError();
    }

    @Test
    void testReadInFamilyCompositeRootReturnsOk() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ARTICLES_FAMILY + FAMILY_COMPOSITE)
                .queryParam("description", "root").build()
        ).exchange().expectStatus().isOk();
    }

    @Test
    void testCreateArticleFamilySizeInternational() {
        ProviderCreationDto providerCreationDto =
                new ProviderCreationDto("pro4", "12345678J", "C/TPV-pro, 4", "9166666603", "p4@gmail.com", "p4", true);
        ProviderDto provider = this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + PROVIDERS)
                .body(BodyInserters.fromObject(providerCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderDto.class)
                .returnResult().getResponseBody();
        assertNotNull(provider);
        String id = provider.getId();
        FamilyCompleteDto familyCompleteDto = FamilyCompleteDto.builder()
                                              .description("Camisetas").sizeType(true).reference("Springfield")
                                              .fromSize("0").toSize("5").provider(id).build();
        ArticlesFamilyDto articleFamily = this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + ARTICLES_FAMILY)
                .body(BodyInserters.fromObject(familyCompleteDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticlesFamilyDto.class)
                .returnResult().getResponseBody();
        assertNotNull(articleFamily);
        assertEquals("Springfield", articleFamily.getReference());
        assertEquals(null, articleFamily.getCode());
        assertEquals(FamilyType.values()[2], articleFamily.getFamilyType());
    }

    @Test
    void testCreateArticleFamilySizeNumber() {
        ProviderCreationDto providerCreationDto =
                new ProviderCreationDto("pro5", "52345678J", "C/TPV-pro, 5", "9166666603", "p5@gmail.com", "p5", true);
        ProviderDto provider = this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + PROVIDERS)
                .body(BodyInserters.fromObject(providerCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProviderDto.class)
                .returnResult().getResponseBody();
        assertNotNull(provider);
        String id = provider.getId();
        FamilyCompleteDto familyCompleteDto = FamilyCompleteDto.builder()
                                              .description("Jeans").sizeType(false).reference("Zaara")
                                              .fromSize("0").toSize("40").increment(2).provider(id).build();
        ArticlesFamilyDto articleFamily = this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + ARTICLES_FAMILY)
                .body(BodyInserters.fromObject(familyCompleteDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticlesFamilyDto.class)
                .returnResult().getResponseBody();
        assertNotNull(articleFamily);
        assertEquals("Zaara", articleFamily.getReference());
        assertEquals(null, articleFamily.getCode());
        assertEquals(FamilyType.values()[2], articleFamily.getFamilyType());
    }

    @Test
    void testCreateArticleFamilyServerError() {
        FamilyCompleteDto familyCompleteDto = FamilyCompleteDto.builder()
                .description("Jeans").sizeType(false).reference("Zaara")
                .fromSize("0").toSize("40").increment(2).build();
        ArticlesFamilyDto articleFamily = this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + ARTICLES_FAMILY)
                .body(BodyInserters.fromObject(familyCompleteDto))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ArticlesFamilyDto.class)
                .returnResult().getResponseBody();
        assertNull(articleFamily.getReference());
        assertNull(articleFamily.getFamilyType());
    }

    @Test
    void testCreateArticleFamilyBadRequest() {
        ArticlesFamilyDto articleFamily = this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + ARTICLES_FAMILY)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ArticlesFamilyDto.class)
                .returnResult().getResponseBody();
        assertNull(articleFamily.getReference());
        assertNull(articleFamily.getFamilyType());
    }

    @Test
    void testSize() {
        List<String> sizes = this.restService.loginAdmin(this.webTestClient)
                .get().uri(contextPath + ARTICLES_FAMILY +SIZES)
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .returnResult().getResponseBody();
        assertNotNull(sizes);
        assertTrue(sizes.size()>0);
    }

    @Test
    void testSearchArticlesFamilyById(){
        String id = this.familyComposite.getArticlesFamilyList().get(0).getId();
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + ARTICLES_FAMILY +"/"+ id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticlesFamilyCrudDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testReadAllArticlesFamily() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + ARTICLES_FAMILY)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateArticlesFamilyTypeArticle(){
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + ARTICLES_FAMILY + CREATE_ARTICLES_FAMILY)
                .body(
                        BodyInserters.fromObject(
                                new ArticlesFamilyCreationDto(FamilyType.ARTICLE, "ropa de hombre", "ropa para hombre", null,"8400000000024")
                        )
                ).exchange().expectStatus().isOk().expectBody(ArticlesFamilyDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testCreateArticlesFamilyTypeArticles(){

        String[] articlesFamilyListId = {
                this.familyComposite.getArticlesFamilyList().get(0).getId(),
                this.familyComposite.getArticlesFamilyList().get(1).getId(),
                this.familyComposite.getArticlesFamilyList().get(2).getId()
        };
        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + ARTICLES_FAMILY + CREATE_ARTICLES_FAMILY)
                .body(
                        BodyInserters.fromObject(
                                new ArticlesFamilyCreationDto(FamilyType.ARTICLES, "ropa de hombre", "ropa para hombre", articlesFamilyListId,null)
                        )
                ).exchange().expectStatus().isOk().expectBody(ArticlesFamilyCreationDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testUpdateArticlesFamilyTypeArticle(){
        String id = this.familyComposite.getArticlesFamilyList().get(0).getId();

        ArticlesFamilyCrudDto articlesFamilyCrudDto = this.restService.loginAdmin(webTestClient)
                .put().uri(contextPath + ARTICLES_FAMILY +"/"+ id)
                .body(BodyInserters.fromObject(
                        new ArticlesFamilyCreationDto(FamilyType.ARTICLE, "ropa vieja", null, null, "8400000000024")
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticlesFamilyCrudDto.class)
                .returnResult().getResponseBody();

        ArticlesFamily articlesFamily = this.articlesFamilyRepository.findById(id).get();
        assertEquals(articlesFamily.getId(), articlesFamilyCrudDto.getId());
        assertEquals(articlesFamily.getReference(), articlesFamilyCrudDto.getReference());
        assertEquals(articlesFamily.getArticle(), articlesFamilyCrudDto.getArticle());
    }

    @Test
    void testUpdateArticlesFamilyTypeArticles(){
        String id = this.familyComposite.getArticlesFamilyList().get(2).getId();
        String[] articlesFamilyListId = {
                this.familyComposite.getArticlesFamilyList().get(0).getId(),
                this.familyComposite.getArticlesFamilyList().get(1).getId()
        };
        ArticlesFamilyCreationDto articlesFamilyCreationDto = this.restService.loginAdmin(webTestClient)
                .put().uri(contextPath + ARTICLES_FAMILY +"/"+ id)
                .body(BodyInserters.fromObject(
                        new ArticlesFamilyCreationDto(FamilyType.ARTICLES, "ropa vieja", "description", articlesFamilyListId, null)
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ArticlesFamilyCreationDto.class)
                .returnResult().getResponseBody();

        ArticlesFamily articlesFamily = this.articlesFamilyRepository.findById(id).get();
        assertEquals(articlesFamily.getId(), articlesFamilyCreationDto.getId());
        assertEquals(articlesFamily.getReference(), articlesFamilyCreationDto.getReference());
        assertEquals(articlesFamily.getArticlesFamilyList().size(), articlesFamilyListId.length);
        assertEquals(articlesFamily.getArticlesFamilyList().get(0).getId(), articlesFamilyListId[0]);
        assertEquals(articlesFamily.getArticlesFamilyList().get(1).getId(), articlesFamilyListId[1]);
    }

    @Test
    void testDeleteArticleFamilyById(){
        String id = this.familyComposite.getArticlesFamilyList().get(2).getId();
        this.restService.loginAdmin(webTestClient)
                .delete().uri(contextPath + ARTICLES_FAMILY +"/"+ id)
                .exchange()
                .expectStatus().isOk();
        assertEquals(Optional.empty(), this.articlesFamilyRepository.findById(id));
    }


}
