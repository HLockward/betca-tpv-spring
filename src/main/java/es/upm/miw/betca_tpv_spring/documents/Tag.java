package es.upm.miw.betca_tpv_spring.documents;

        import org.springframework.data.annotation.Id;
        import org.springframework.data.mongodb.core.mapping.DBRef;
        import org.springframework.data.mongodb.core.mapping.Document;

        import java.util.Arrays;
        import java.util.List;
        import java.util.Objects;

@Document
public class Tag {

    @Id
    private String id;
    private String description;

    @DBRef
    private Article [] articleList;

    public Tag() {
        // empty to framework
    }

    public Tag(String description, Article[] articleList) {
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

    public Article[] getArticleList() {
        return articleList;
    }

    public void setArticleList(Article[] articleList) {
        this.articleList = articleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", articleList=" + Arrays.toString(articleList) +
                '}';
    }
}
