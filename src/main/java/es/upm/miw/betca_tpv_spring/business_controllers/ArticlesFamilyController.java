package es.upm.miw.betca_tpv_spring.business_controllers;
import es.upm.miw.betca_tpv_spring.business_services.Barcode;
import es.upm.miw.betca_tpv_spring.documents.*;
import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.exceptions.BadRequestException;
import es.upm.miw.betca_tpv_spring.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Controller
public class ArticlesFamilyController {

    @Autowired
    private FamilyCompositeRepository familyCompositeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticlesFamilyReactRepository articlesFamilyReactRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ArticlesFamilyRepository articlesFamilyRepository;

    private List<String> getSizes() throws IOException {
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        Properties prop = new Properties();
        prop.load(inputStream);
        return Arrays.asList(prop.getProperty("sizes").split(","));
    }

    public List<ArticleFamilyCompleteDto> readFamilyCompositeArticlesList(String description) {
        FamilyComposite familyComplete = familyCompositeRepository.findFirstByDescription(description);
        List<ArticleFamilyCompleteDto> dtos = new ArrayList<>();

        if (familyComplete.getFamilyType() == FamilyType.ARTICLES) {
            for (ArticlesFamily articlesFamily : familyComplete.getArticlesFamilyList()) {
                if (articlesFamily.getFamilyType() == FamilyType.ARTICLES) {
                    dtos.add(new ArticleFamilyCompleteDto(articlesFamily.getFamilyType(), articlesFamily.getDescription(), articlesFamily.getArticlesFamilyList()));
                }
                if (articlesFamily.getFamilyType() == FamilyType.ARTICLE) {
                    Article article = articleRepository.findByCode(articlesFamily.getArticleIdList().get(0));
                    dtos.add(new ArticleFamilyCompleteDto(articlesFamily.getFamilyType(), article.getCode(), article.getDescription(), article.getRetailPrice()));
                }
                if (articlesFamily.getFamilyType() == FamilyType.SIZES) {
                    dtos.add(new ArticleFamilyCompleteDto(articlesFamily.getFamilyType(), articlesFamily.getReference(), articlesFamily.getDescription()));
                }
            }
        } else if (familyComplete.getFamilyType() == FamilyType.SIZES) {
            for (ArticlesFamily articlesFamily : familyComplete.getArticlesFamilyList()) {
                Article article = articleRepository.findByCode(articlesFamily.getArticleIdList().get(0));
                dtos.add(new ArticleFamilyCompleteDto(article.getReference().split("T")[1], article.getStock(), article.getRetailPrice(), article.getCode()));
            }
        }
        return dtos;

    }

    public List<String>  readSizes() throws IOException {
        return getSizes();
    }


    public Mono<ArticlesFamilyDto> createArticleFamily(FamilyCompleteDto articlesFamilyDto) throws IOException {

        List<String> sizes = getSizes();
        int lowerLimit = Integer.parseInt(articlesFamilyDto.getFromSize());
        int upperLimit = Integer.parseInt(articlesFamilyDto.getToSize());
        List<ArticlesFamily> familyArticleList = new ArrayList<>();
        Optional<Provider> provider = this.providerRepository.findById(articlesFamilyDto.getProvider());

        int increment = 1;
        if(articlesFamilyDto.getIncrement()>0 && !articlesFamilyDto.getSizeType())
            increment = articlesFamilyDto.getIncrement();


        for (int index = lowerLimit; index <= upperLimit;index += increment) {
            String code = new Barcode().generateEan13code(Long.parseLong(this.articleRepository.findFirstByOrderByCodeDesc().getCode().substring(0, 12)) + 1);
            if (code.length() == 13 && Long.parseLong(code.substring(7, 12)) > 99999L) {
                return Mono.error(new BadRequestException("Index out of range"));
            }

            String description;
            if(articlesFamilyDto.getSizeType())
                description = sizes.get(index);
            else
                description = String.valueOf(index);

            Article article = Article.builder(code).description(articlesFamilyDto.getReference()+ " - " + articlesFamilyDto.getDescription() + " T" + description)
            .reference(articlesFamilyDto.getReference()+ " T" + description).provider(provider.get()).build();
            this.articleRepository.save(article);
            familyArticleList.add(new FamilyArticle(article));
        }
        this.articlesFamilyRepository.saveAll(familyArticleList);
        ArticlesFamily familyCompositeSizesList = new FamilyComposite(FamilyType.SIZES, articlesFamilyDto.getReference(), articlesFamilyDto.getDescription());

       for (ArticlesFamily articlesFamily : familyArticleList) {
            familyCompositeSizesList.add(articlesFamily);
        }

        return this.articlesFamilyReactRepository.save(familyCompositeSizesList).map(ArticlesFamilyDto::new);
    }

    public Flux<ArticlesFamilyCrudDto> readAllArticlesFamily(){
        return this.articlesFamilyReactRepository.findAll()
                .map(ArticlesFamilyCrudDto::new);
    }

    public Mono<ArticlesFamilyCrudDto> searchArticlesFamilyById(String id){
        return this.articlesFamilyReactRepository.findById(id)
                .map(ArticlesFamilyCrudDto::new);
    }

    public Flux<ArticlesFamilyCrudDto> searchArticlesFamilyByReferenceOrFamilyType(ArticlesFamilySearchDto articlesFamilySearchDto){
        return this.articlesFamilyReactRepository
                .findByReferenceLikeOrFamilyType(
                        articlesFamilySearchDto.getReference(),
                        articlesFamilySearchDto.getArticleFamily())
                .map(ArticlesFamilyCrudDto::new);
    }



}
