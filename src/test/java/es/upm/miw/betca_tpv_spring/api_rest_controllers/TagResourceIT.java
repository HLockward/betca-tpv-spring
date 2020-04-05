package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.TagDto;
import es.upm.miw.betca_tpv_spring.repositories.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.TagResource.TAGS;
import static es.upm.miw.betca_tpv_spring.api_rest_controllers.TagResource.TAG_DESCRIPTION;

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
}
