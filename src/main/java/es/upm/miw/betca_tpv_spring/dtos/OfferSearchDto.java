package es.upm.miw.betca_tpv_spring.dtos;

import java.time.LocalDateTime;
import java.util.Objects;

public class OfferSearchDto {
    private LocalDateTime registrationDate;
    private LocalDateTime expirationDate;

    public OfferSearchDto() {
        // empty
    }

    public OfferSearchDto(LocalDateTime registrationDate, LocalDateTime expirationDate) {
        this.registrationDate = registrationDate;
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return "OfferSearchDto{" +
                "initialDate=" + registrationDate +
                ", finalDate=" + expirationDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfferSearchDto that = (OfferSearchDto) o;
        return registrationDate.equals(that.registrationDate) &&
                expirationDate.equals(that.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationDate, expirationDate);
    }
}
