package es.upm.miw.betca_tpv_spring.dtos;

import java.util.List;

public class TagCreationDto {

    private String id;
    private String description;
    private List<ArticleDto> articleList;

    public TagCreationDto() {
        // empty for framework
    }

    public TagCreationDto(String id, String description, List<ArticleDto> articleList) {
        this.id = id;
        this.description = description;
        this.articleList = articleList;
    }

    public TagCreationDto(String description, List<ArticleDto> articleList) {
        this.description = description;
        this.articleList = articleList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    @Override
    public String toString() {
        return "TagCreationDto{" +
                "description='" + description + '\'' +
                '}';
    }
}
