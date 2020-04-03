package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.dtos.validations.BigDecimalPositive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OfferCreationDto {
    private LocalDateTime expirationDate;

    @BigDecimalPositive
    private BigDecimal discount;

    private String description;

    private List<ArticleDto> articleList;

    public OfferCreationDto() {
        // empty for framework
    }

    public OfferCreationDto(LocalDateTime expirationDate, BigDecimal discount, String description, List<ArticleDto> articleList) {
        this.expirationDate = expirationDate;
        this.discount = discount;
        this.description = description;
        this.articleList = articleList;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
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

    public List<ArticleDto> getArticleList() {
        return articleList;
    }

    @Override
    public String toString() {
        return "OfferCreationDto{" +
                "expirationDate=" + expirationDate +
                ", discount=" + discount +
                ", description='" + description + '\'' +
                '}';
    }
}
