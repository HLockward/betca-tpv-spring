package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_spring.documents.CustomerDiscount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerDiscountCreationDto {

    private String id;

    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime registrationDate;

    private BigDecimal discount;

    private BigDecimal minimumPurchase;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String mobile;

    public CustomerDiscountCreationDto() {
        // Empty for framework
    }

    public CustomerDiscountCreationDto(String id, String description, LocalDateTime registrationDate, BigDecimal discount,
                                       BigDecimal minimumPurchase) {
        this.id = id;
        this.description = description;
        this.registrationDate = LocalDateTime.now();
        this.discount = discount;
        this.minimumPurchase = minimumPurchase;

    }

    public CustomerDiscountCreationDto(CustomerDiscount customerDiscount) {
        this.id = customerDiscount.getId();
        this.description = customerDiscount.getDescription();
        this.registrationDate = customerDiscount.getRegistrationDate();
        this.discount = customerDiscount.getDiscount();
        this.minimumPurchase = customerDiscount.getMinimumPurchase();
        this.mobile = customerDiscount.getUser().getMobile();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRegistrationDate() {
        return this.registrationDate;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getMinimumPurchase() {
        return this.minimumPurchase;
    }

    public void setMinimumPurchase(BigDecimal minimumPurchase) {
        this.minimumPurchase = minimumPurchase;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    @Override
    public String toString() {
        return "CustomerDiscountCreationDto{" +
                "description='" + this.description + '\'' +
                ", registrationDate='" + this.registrationDate + '\'' +
                ", discount='" + this.discount + '\'' +
                ", minimumPurchase='" + this.minimumPurchase + '\'' +
                ", mobile=" + this.mobile +
                '}';
    }
}

