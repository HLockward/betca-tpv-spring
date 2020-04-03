package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.math.BigDecimal;

public class ArticleAdvancedSearchDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reference;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer stock;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provider;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal retailPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean discontinued;

    public ArticleAdvancedSearchDto() {
        // Empty for framework
    }

    public ArticleAdvancedSearchDto(String description, String reference, Integer stock, String provider, BigDecimal retailPrice, boolean discontinued) {
        if (description == null)
            this.description = "null";
        else
            this.description = description;
        if (reference == null)
            this.reference = "null";
        else
            this.reference = reference;
        this.stock = stock;
        if (provider == null)
            this.provider = "null";
        else
            this.provider = provider;
        this.retailPrice = retailPrice;
        this.discontinued = discontinued;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Boolean getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(Boolean discontinued) {
        this.discontinued = discontinued;
    }

    @Override
    public String toString() {
        return "ArticleAdvancedSearchDto{" +
                "description='" + description + '\'' +
                ", reference='" + reference + '\'' +
                ", stock=" + stock +
                ", provider='" + provider + '\'' +
                ", retailPrice=" + retailPrice +
                ", discontinued=" + discontinued +
                '}';
    }
}
