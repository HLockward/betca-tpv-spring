package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Tag;
import es.upm.miw.betca_tpv_spring.dtos.TagCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.TagDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.TagReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Controller
public class TagController {

    private TagReactRepository tagReactRepository;
    private PdfService pdfService;
    private ArticleReactRepository articleReactRepository;
    private ArticleRepository articleRepository;

    @Autowired
    public TagController(TagReactRepository tagReactRepository, PdfService pdfService, ArticleReactRepository articleReactRepository, ArticleRepository articleRepository) {
        this.tagReactRepository = tagReactRepository;
        this.pdfService = pdfService;
        this.articleReactRepository = articleReactRepository;
        this.articleRepository = articleRepository;
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

    private Mono<Article> noExistsByIdAssured(String id) {
        return this.articleReactRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Article with Id:" + id)));
    }

    public Mono<Tag> createTag(TagCreationDto tagCreationDto) {
        Article[] articles;
        articles = tagCreationDto.getArticleList().stream().map(articleDto -> Article.builder(articleDto.getCode())
                .description(articleDto.getDescription())
                .build()).toArray(Article[]::new);

        Arrays.stream(articles).forEach(article -> {
            Mono<Article> notExist = this.noExistsByIdAssured(article.getCode());
        });

        Tag tag = new Tag();
        tag.setDescription(tagCreationDto.getDescription());
        tag.setArticleList(articles);
        return this.tagReactRepository.save(tag);
    }

    public Mono<TagDto> updateTag(String description, TagCreationDto tagCreationDto) {
        Article[] articles;
        articles = tagCreationDto.getArticleList().stream().map(articleDto -> Article.builder(articleDto.getCode())
                .description(articleDto.getDescription())
                .build()).toArray(Article[]::new);
        Mono<Tag> tag = tagReactRepository.findByDescription(description).
                switchIfEmpty(Mono.error(new NotFoundException("Tag description (" + description + ")")))
                .map(tag1 -> {
                    tag1.setId(tagCreationDto.getId());
                    tag1.setDescription(description);
                    tag1.setArticleList(articles);
                    return tag1;
                });

        return Mono.when(tag).then(this.tagReactRepository.saveAll(tag).next().map(TagDto::new));

    }

    @Transactional
    public Mono<byte[]> printTag(String id) {
        Mono<Tag> tagReact = tagReactRepository.findById(id).switchIfEmpty(Mono.error(new NotFoundException("Tag code (" + id + ")")));
        return pdfService.generateTag(tagReact);
    }

    public Mono<Void> deleteTag(String id) {
        Mono<Tag> tag = this.tagReactRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Tag id:" + id)));
        return Mono
                .when(tag)
                .then(this.tagReactRepository.deleteById(id));
    }
}