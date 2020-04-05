package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Offer;
import es.upm.miw.betca_tpv_spring.documents.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    private List<Article> articles;

    public TagDto() {
        // Empty for framework
    }

    public TagDto(String description, List<Article> articles) {
        this.description = description;
        this.articles = articles;
    }

    public TagDto(Tag tag){
        this.description=tag.getDescription();
        this.articles=tag.getArticleList();
        //this.articles= Arrays.asList(tag.getArticleList()).stream().map(ArticleDto::new).collect(Collectors.toList());
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "TagDto{" +
                "description='" + description + '\'' +
                ", articles=" + articles +
                '}';
    }
}
