package es.upm.miw.betca_tpv_spring.dtos;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class InvoiceFilterDto {

    private String mobile;
    private String fromDate;
    private String toDate;

    public InvoiceFilterDto() {
    }

    public InvoiceFilterDto(String mobile, String fromDate, String toDate) {
        this.mobile = mobile;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getMobile() {
        return mobile;
    }

    public String getFromDate() {
        return fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    @Override
    public String toString() {
        return "InvoiceFilterDto{" +
                "mobile='" + mobile + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
