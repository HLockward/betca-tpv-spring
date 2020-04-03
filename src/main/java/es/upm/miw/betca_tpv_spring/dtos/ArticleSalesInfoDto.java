package es.upm.miw.betca_tpv_spring.dtos;

public class ArticleSalesInfoDto {
    private String code;
    private Integer year;
    private Integer month;
    private Integer amount;

    public ArticleSalesInfoDto() {
    }

    public ArticleSalesInfoDto(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ArticleSalesInfoDto{" +
                "code='" + code + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", amount=" + amount +
                '}';
    }
}
