package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.TagCreationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class TagControllerIT {
    @Autowired
    private
    TagController tagController;

    @Test
    void testReadOne() {
        StepVerifier
                .create(this.tagController.readOne("tag2"))
                .expectNextMatches(tag ->
                        tag.getDescription().equals("tag2"))
                .expectComplete()
                .verify();
    }

    @Test
    void testReadAll() {
        StepVerifier
                .create(this.tagController.readAll())
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateTag() {
        ArticleDto[] articles = {new ArticleDto(Article.builder("0000008").description("Apple").retailPrice(new BigDecimal(8)).build()), new ArticleDto(Article.builder("00000082").description("Apple2").retailPrice(new BigDecimal(8)).build())};
        TagCreationDto tagCreationDto = new TagCreationDto("tagC", Arrays.asList(articles));

        StepVerifier
                .create(this.tagController.createTag(tagCreationDto))
                .expectNextMatches(tag -> {
                    assertEquals(2, tag.getArticleList().length);
                    assertEquals("tagC", tag.getDescription());
                    return true;
                })
                .expectComplete()
                .verify();
    }
    @Test
    void testCreateTagWithEmptyArticles() {
        ArticleDto[] articles = {new ArticleDto(Article.builder("asldhajsd").description("Apple").retailPrice(new BigDecimal(8)).build())};
        TagCreationDto tagCreationDto = new TagCreationDto("tagC", Arrays.asList(articles));

        StepVerifier
                .create(this.tagController.createTag(tagCreationDto))
                .expectError()
                .verify();

    }
}
