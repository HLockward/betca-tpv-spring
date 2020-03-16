package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrderSearchDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provider;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime closingDate;

    public OrderSearchDto(){
    }

    public OrderSearchDto(String description, String provider, LocalDateTime closingDate) {
        this.description = description;
        this.provider = provider;
        this.closingDate = closingDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDateTime getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDateTime closingDate) {
        this.closingDate = closingDate;
    }

    @Override
    public String toString() {
        return "OrderSearchDto{" +
                "description='" + description + '\'' +
                ", provider='" + provider + '\'' +
                ", closingDate=" + closingDate +
                '}';
    }
}
