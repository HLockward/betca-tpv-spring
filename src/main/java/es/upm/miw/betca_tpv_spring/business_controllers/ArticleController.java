package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.Barcode;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Provider;
import es.upm.miw.betca_tpv_spring.dtos.ArticleAdvancedSearchDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleDto;
import es.upm.miw.betca_tpv_spring.dtos.ArticleSearchDto;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.exceptions.ConflictException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.ArticleReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class ArticleController {

    private static final Long FIRST_CODE_ARTICLE = 840000000000L; //until 840000099999L

    private ArticleReactRepository articleReactRepository;
    private ProviderReactRepository providerReactRepository;
    private ProviderRepository providerRepository;
    private ArticleRepository articleRepository;
    private long eanCode;

    @Autowired
    public ArticleController(ArticleReactRepository articleReactRepository,
                             ProviderReactRepository providerReactRepository, ProviderRepository providerRepository, ArticleRepository articleRepository) {
        this.articleReactRepository = articleReactRepository;
        this.providerReactRepository = providerReactRepository;
        this.providerRepository = providerRepository;
        this.articleRepository = articleRepository;
        this.eanCode = FIRST_CODE_ARTICLE;
    }

    public Mono<ArticleDto> readArticle(String code) {
        return this.articleReactRepository.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException("Article code (" + code + ")")))
                .map(ArticleDto::new);
    }

    public Flux<ArticleDto> readAll() {
        return this.articleReactRepository.findAll()
                .switchIfEmpty(Flux.error(new BadRequestException("Bad Request")))
                .map(ArticleDto::new);
    }

    private Mono<Void> noExistsByIdAssured(String id) {
        return this.articleReactRepository.existsById(id)
                .handle((result, sink) -> {
                    if (Boolean.TRUE.equals(result)) {
                        sink.error(new ConflictException("Article code (" + id + ")"));
                    } else {
                        sink.complete();
                    }
                });
    }

    public Mono<ArticleDto> createArticle(ArticleDto articleDto) {
        String code = articleDto.getCode();
        if (code == null) {
            System.out.println(code.substring(0,6).equals("840000"));
            code = new Barcode().generateEan13code(Long.parseLong(this.articleRepository.findFirstByOrderByCodeDesc().getCode().substring(0, 12)) + 1);
        }
        if (code.length() > 13 || Long.parseLong(code.substring(6, 12)) > 99999L) {
            return Mono.error(new BadRequestException("Index out of range"));
        }
        Mono<Void> noExistsByIdAssured = this.noExistsByIdAssured(code);
        int stock = (articleDto.getStock() == null) ? 10 : articleDto.getStock();
        Article article = Article.builder(code).description(articleDto.getDescription())
                .retailPrice(articleDto.getRetailPrice()).reference(articleDto.getReference()).stock(stock).build();
        if (articleDto.getTax() != null)
            article.setTax(articleDto.getTax());
        Mono<Void> provider;
        if (articleDto.getProvider() == null) {
            provider = Mono.empty();
        } else {
            provider = this.providerReactRepository.findById(articleDto.getProvider())
                    .switchIfEmpty(Mono.error(new NotFoundException("Provider (" + articleDto.getProvider() + ")")))
                    .doOnNext(article::setProvider).then();
        }
        return Mono
                .when(noExistsByIdAssured, provider)
                .then(this.articleReactRepository.save(article))
                .map(ArticleDto::new);
    }

    public Mono<ArticleDto> updateArticle(String code, ArticleDto articleDto) {
        Provider provider = this.providerRepository.findById(articleDto.getProvider()).get();
        Mono<Article> article = this.articleReactRepository.findById(code).
                switchIfEmpty(Mono.error(new NotFoundException("Article id " + articleDto.getCode())))
                .map(article1 -> {
                    article1.setProvider(provider);
                    article1.setDescription(articleDto.getDescription());
                    article1.setStock(articleDto.getStock());
                    article1.setDiscontinued(articleDto.getDiscontinued());
                    article1.setReference(articleDto.getReference());
                    article1.setRetailPrice(articleDto.getRetailPrice());
                    if (articleDto.getTax() != null)
                        article1.setTax(articleDto.getTax());
                    return article1;
                });

        return Mono.
                when(article).
                then(this.articleReactRepository.saveAll(article).next().map(ArticleDto::new));


    }

    public Flux<ArticleDto> searchArticleByDescriptionOrProvider(ArticleSearchDto articleSearchDto) {
        return this.articleReactRepository.findByDescriptionLikeOrProvider(articleSearchDto.getDescription(), articleSearchDto.getProvider())
                .switchIfEmpty(Flux.error(new BadRequestException("Params not found")))
                .map(ArticleDto::new);
    }

    public Flux<ArticleDto> searchArticleByDescriptionOrReferenceOrStockOrProviderOrRetailPriceOrDiscontinued(ArticleAdvancedSearchDto articleAdvancedSearchDto) {
        if (articleAdvancedSearchDto.getDescription().equals("null") && articleAdvancedSearchDto.getProvider().equals("null") && articleAdvancedSearchDto.getReference().equals("null") && articleAdvancedSearchDto.getStock() == null && articleAdvancedSearchDto.getRetailPrice() == null && !articleAdvancedSearchDto.getDiscontinued()) {
            return this.articleReactRepository.findAll().map(ArticleDto::new).filter(articleDto -> articleAdvancedSearchDto.getDiscontinued().equals(articleDto.getDiscontinued()));

        }
        if (articleAdvancedSearchDto.getDescription().equals("null") && articleAdvancedSearchDto.getProvider().equals("null") && articleAdvancedSearchDto.getReference().equals("null") && articleAdvancedSearchDto.getStock() == null && articleAdvancedSearchDto.getRetailPrice() == null && articleAdvancedSearchDto.getDiscontinued()) {
            return this.articleReactRepository.findAll().map(ArticleDto::new).filter(articleDto -> articleAdvancedSearchDto.getDiscontinued().equals(articleDto.getDiscontinued()));
        } else {
            return this.articleReactRepository.findByDescriptionLikeOrReferenceLikeOrStockOrProviderOrRetailPrice(articleAdvancedSearchDto.getDescription(),
                    articleAdvancedSearchDto.getReference(), articleAdvancedSearchDto.getStock(),
                    articleAdvancedSearchDto.getProvider(), articleAdvancedSearchDto.getRetailPrice())
                    .switchIfEmpty(Flux.error(new BadRequestException("Params not found")))
                    .map(ArticleDto::new).filter(articleDto -> articleDto.getDiscontinued().equals(articleAdvancedSearchDto.getDiscontinued()));
        }
    }
}
