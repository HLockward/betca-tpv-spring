package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.ArticlesFamily;
import es.upm.miw.betca_tpv_spring.documents.FamilyType;

import java.util.List;

public class ArticlesFamilyDto {

    private String id;

    private FamilyType familyType;

    private String reference;

    private String description;

    private List<ArticlesFamily> articlesFamilyList;

    private Article article;

    public ArticlesFamilyDto() {
    }

    public ArticlesFamilyDto(String id, FamilyType familyType, String reference) {
        this.id = id;
        this.familyType = familyType;
        this.reference = reference;
    }

    public ArticlesFamilyDto(ArticlesFamily articlesFamily) {
        this.id = articlesFamily.getId();
        this.familyType = articlesFamily.getFamilyType();
        this.reference = articlesFamily.getReference();
        this.description = articlesFamily.getDescription();
        this.articlesFamilyList = articlesFamily.getArticlesFamilyList();
        this.article = articlesFamily.getArticle();

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return id;
    }

    public void setCode(String code) {
        this.id = code;
    }

    public FamilyType getFamilyType() {
        return familyType;
    }

    public void setFamilyType(FamilyType familyType) {
        this.familyType = familyType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Article getArticle(){
        return article;
    }

    public void setArticle(Article article){
        this.article = article;
    }

    public List<ArticlesFamily> getArticlesFamilyList() {
        return articlesFamilyList;
    }

    public void setArticlesFamilyList(List<ArticlesFamily> articlesFamilyList) {
        this.articlesFamilyList = articlesFamilyList;
    }

    @Override
    public String toString() {
        return "ArticlesFamilyDto{" +
                "id='" + id + '\'' +
                ", familyType=" + familyType +
                ", reference='" + reference + '\'' +
                '}';
    }
}
