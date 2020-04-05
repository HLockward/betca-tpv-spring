package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.repositories.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
class TagControllerIT {
    @Autowired
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
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}
