package es.upm.miw.betca_tpv_spring.dtos;


import es.upm.miw.betca_tpv_spring.documents.Article;
import es.upm.miw.betca_tpv_spring.documents.StockAlarm;

public class StockAlarmSearchDto extends ArticleMinimumDto {

    private Integer stock;

    private Integer warning;

    private Integer critical;


    public StockAlarmSearchDto() {
    }

    public StockAlarmSearchDto(String code, String description) {
        super(code, description);
    }

    public StockAlarmSearchDto(Article article) {
        super(article);
    }

    public StockAlarmSearchDto(StockAlarm stockAlarm) {

    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getWarning() {
        return warning;
    }

    public void setWarning(Integer warning) {
        this.warning = warning;
    }

    public Integer getCritical() {
        return critical;
    }

    public void setCritical(Integer critical) {
        this.critical = critical;
    }

    @Override
    public String toString() {
        return "StockAlarmSearchDto{" +
                "code=" + getCode() +
                ",description=" + getDescription() +
                ", stock=" + stock +
                ", warning=" + warning +
                ", critical=" + critical +
                '}';
    }
}

