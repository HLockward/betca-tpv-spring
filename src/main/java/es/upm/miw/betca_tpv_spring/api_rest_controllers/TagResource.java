package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.TagController;
import es.upm.miw.betca_tpv_spring.dtos.TagDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(TagResource.TAGS)
public class TagResource {
    public static final String TAGS = "/tags";
    public static final String TAG_DESCRIPTION = "/{description}";
    private TagController tagController;

    @Autowired
    public TagResource(TagController tagController) {
        this.tagController = tagController;
    }

    @GetMapping(value = TAG_DESCRIPTION)
    public Mono<TagDto> readOne(@PathVariable String description) {
        return this.tagController.readOne(description)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping
    public Flux<TagDto> readAll() {
        return this.tagController.readAll()
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }
}