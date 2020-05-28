package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ArticlesFamilySearchDto {

    @JsonInclude(Include.NON_NULL)
    private String reference;

    @JsonInclude(Include.NON_NULL)
    private String articleFamily;

    public ArticlesFamilySearchDto() {
        // Empty for framework
    }

    public ArticlesFamilySearchDto(String reference) {
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }


    @Override
    public String toString() {
        return "ArticlesFamilySearchDto{" +
                "reference=" + this.reference +
                '}';
    }
}
