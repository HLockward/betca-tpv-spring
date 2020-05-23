package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.TagController;
import es.upm.miw.betca_tpv_spring.documents.Tag;
import es.upm.miw.betca_tpv_spring.dtos.TagCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.TagDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(TagResource.TAGS)
public class TagResource {
    public static final String TAGS = "/tags";
    public static final String TAG_ID = "/{id}";
    private static final String PRINT = "/print";
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

    @PostMapping(produces = {"application/json"})
    public  Mono<Tag> create(@Valid @RequestBody TagCreationDto tagCreationDto) {
        return this.tagController.createTag(tagCreationDto).doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PutMapping(value = TAG_DESCRIPTION, produces = {"application/json"})
    public Mono<Tag> update(@PathVariable String description, @Valid @RequestBody TagCreationDto tagCreationDto){
        return this.tagController.updateTag(description, tagCreationDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }


    @GetMapping(value = TAG_ID + PRINT, produces = {"application/pdf"})
    public Mono<byte[]> print(@PathVariable String id){
        return this.tagController.printTag(id).doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

}