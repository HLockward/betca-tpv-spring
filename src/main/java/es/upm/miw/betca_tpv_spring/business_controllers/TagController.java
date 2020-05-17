package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Tag;
import es.upm.miw.betca_tpv_spring.dtos.TagCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.TagDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.TagReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class TagController {

    private TagReactRepository tagReactRepository;
    @Autowired
    public TagController(TagReactRepository tagReactRepository) {
        this.tagReactRepository = tagReactRepository;
    }

    public Mono<TagDto> readOne(String description) {
        return this.tagReactRepository.findByDescription(description)
                .switchIfEmpty(Mono.error(new NotFoundException("Tag description (" + description + ")")))
                .map(TagDto::new);
    }

    public Flux<TagDto> readAll() {
        return this.tagReactRepository.findAll()
                .switchIfEmpty(Flux.error(new BadRequestException("Bad Request")))
                .map(TagDto::new);
    }

    public Mono<Tag> createTag(TagCreationDto tagCreationDto) {
        Article[] articles;
        articles = tagCreationDto.getArticleList().stream().map(articleDto -> Article.builder(articleDto.getCode())
        .description(articleDto.getDescription())
        .build()).toArray(Article[]::new);
    Tag tag = new Tag();
    tag.setDescription(tagCreationDto.getDescription());
    tag.setArticleList(articles);

    return tagReactRepository.save(tag);

    }
}