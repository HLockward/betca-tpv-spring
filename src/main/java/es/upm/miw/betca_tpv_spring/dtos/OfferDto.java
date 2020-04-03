package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.Offer;
import es.upm.miw.betca_tpv_spring.dtos.validations.BigDecimalPositive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class OfferDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime expirationDate;

    private LocalDateTime registrationDate;

    @BigDecimalPositive
    private BigDecimal discount;

    private String description;

    private List<ArticleDto> articles;

    public OfferDto() {
        // empty for framework
    }

    public OfferDto(String id, LocalDateTime expirationDate, LocalDateTime registrationDate,
                    BigDecimal discount, String description, List<ArticleDto> articles) {
        this.id = id;
        this.expirationDate = expirationDate;
        this.registrationDate = registrationDate;
        this.discount = discount;
        this.description = description;
        this.articles = articles;
    }

    public OfferDto(Offer offer) {
        this(offer.getId(), offer.getExpirationDate(), offer.getRegistrationDate(), offer.getDiscount(), offer.getDescription(), mapArticlesToDto(offer));
    }

    private static List<ArticleDto> mapArticlesToDto(Offer offer) {
        List<Article> articlesList = new ArrayList<>();
        Collections.addAll(articlesList, offer.getArticleList());

        Stream<Object> stream = articlesList.stream().map(articleDto -> {
            return new ArticleDto(articleDto.getCode(), articleDto.getDescription(), articleDto.getReference(), articleDto.getRetailPrice(), articleDto.getStock());
        });

        return Arrays.asList(stream.toArray(ArticleDto[]::new));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ArticleDto> getArticles() {
        return articles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferDto that = (OfferDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(expirationDate, that.expirationDate) &&
                Objects.equals(registrationDate, that.registrationDate) &&
                Objects.equals(discount, that.discount) &&
                Objects.equals(description, that.description) &&
                Objects.equals(articles, that.articles);
    }

    @Override
    public String toString() {
        return "OfferDto{" +
                "id='" + id + '\'' +
                ", expirationDate=" + expirationDate +
                ", registrationDate=" + registrationDate +
                ", discount=" + discount +
                ", description='" + description + '\'' +
                ", articles=" + articles +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expirationDate, registrationDate, discount, description, articles);
    }
}
