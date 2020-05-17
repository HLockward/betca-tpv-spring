package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.TagCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.TagDto;
import es.upm.miw.betca_tpv_spring.repositories.TagRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.math.BigDecimal;
import java.util.Arrays;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.TagResource.TAGS;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.TagResource.TAG_DESCRIPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ApiTestConfig
class TagResourceIT {
    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TagRepository tagRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testReadOne() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TAGS + TAG_DESCRIPTION, "tag1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TagDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testReadAll() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + TAGS)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreate(){
        TagDto tagDto = createDto();

        Assert.assertNotNull(tagDto);
        assertEquals(new String("tag4"), tagDto.getDescription());
    }


    private TagDto createDto(){
        ArticleDto[] articles = {new ArticleDto(Article.builder("0000009").description("Pomelo").retailPrice(new BigDecimal(3)).build())};
        TagCreationDto tagCreationDto = new TagCreationDto("tag4", Arrays.asList(articles));

        return this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + TAGS)
                .body(BodyInserters.fromObject(tagCreationDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TagDto.class)
                .returnResult().getResponseBody();
    }
}
