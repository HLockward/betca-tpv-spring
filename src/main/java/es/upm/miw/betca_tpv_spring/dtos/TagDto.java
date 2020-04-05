package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Tag;

import java.util.List;

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

    public TagDto(Tag tag) {
        this.description = tag.getDescription();
        this.articles = tag.getArticleList();
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
