package es.upm.miw.betca_tpv_spring.dtos;

import java.util.List;

public class TagCreationDto {

    private String description;
    private List<ArticleDto> articleList;

    public TagCreationDto() {
    }

    public TagCreationDto(String description, List<ArticleDto> articleList) {
        this.description = description;
        this.articleList = articleList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ArticleDto> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<ArticleDto> articleList) {
        this.articleList = articleList;
    }

    @Override
    public String toString() {
        return "TagCreationDto{" +
                "description='" + description + '\'' +
                ", articleList=" + articleList +
                '}';
    }
}
