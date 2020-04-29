package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.StockAlarmArticle;

import java.util.Arrays;

public class StockAlarmDto {

    private String id;

    private String description;

    private String provider;

    private Integer warning;

    private Integer critical;

    private StockAlarmArticle[] stockAlarmArticle;

    public StockAlarmDto() {
    }

    public StockAlarmDto(String description, String provider, Integer warning, Integer critical, StockAlarmArticle[] stockAlarmArticle) {
        this.description = description;
        this.provider = provider;
        this.warning = warning;
        this.critical = critical;
        this.stockAlarmArticle = stockAlarmArticle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public StockAlarmArticle[] getStockAlarmArticle() {
        return stockAlarmArticle;
    }

    public void setStockAlarmArticle(StockAlarmArticle[] stockAlarmArticle) {
        this.stockAlarmArticle = stockAlarmArticle;
    }

    @Override
    public String toString() {
        return "StockAlarmDto{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", provider='" + provider + '\'' +
                ", warning=" + warning +
                ", critical=" + critical +
                ", stockAlarmArticle=" + Arrays.toString(stockAlarmArticle) +
                '}';
    }
}
