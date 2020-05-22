package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Tag;

import java.util.*;
import java.util.stream.Stream;

public class TagDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ArticleDto> articlesList;

    public TagDto() {
        // Empty for framework
    }

    public TagDto(String id, String description, List<ArticleDto> articlesList) {
        this.id = id;
        this.description = description;
        this.articlesList = articlesList;
    }

    public TagDto(Tag tag) {
        this(tag.getId(), tag.getDescription(), mapArticlesToDto(tag));
    }

    private static List<ArticleDto> mapArticlesToDto(Tag tag) {
        List<Article> articlesList = new ArrayList<>();
        Collections.addAll(articlesList, tag.getArticleList());

        Stream<Object> stream = articlesList.stream().map(articleDto -> {
            return new ArticleDto(articleDto.getCode(), articleDto.getDescription(), articleDto.getReference(), articleDto.getRetailPrice(), articleDto.getStock());
        });

        return Arrays.asList(stream.toArray(ArticleDto[]::new));
    }


    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ArticleDto> getArticles() {
        return articlesList;
    }

    public void setArticles(List<ArticleDto> articles) {
        this.articlesList = articles;
    }

    @Override
    public String toString() {
        return "TagDto{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", articles=" + articlesList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDto tagDto = (TagDto) o;
        return Objects.equals(id, tagDto.id) &&
                Objects.equals(description, tagDto.description) &&
                Objects.equals(articlesList, tagDto.articlesList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, articlesList);
    }
}
