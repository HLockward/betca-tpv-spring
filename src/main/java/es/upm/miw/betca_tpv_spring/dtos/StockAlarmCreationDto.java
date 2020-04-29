package es.upm.miw.betca_tpv_spring.dtos;

import java.util.Arrays;

public class StockAlarmCreationDto {

    private String description;

    private String provider;

    private Integer warning;

    private Integer critical;

    private StockAlarmArticleDto[] stockAlarmArticle;

    public StockAlarmCreationDto() {
    }

    public StockAlarmCreationDto(String description, String provider, Integer warning, Integer critical, StockAlarmArticleDto[] stockAlarmArticle) {
        this.description = description;
        this.provider = provider;
        this.warning = warning;
        this.critical = critical;
        this.stockAlarmArticle = stockAlarmArticle;
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

    public StockAlarmArticleDto[] getStockAlarmArticle() {
        return stockAlarmArticle;
    }

    public void setStockAlarmArticle(StockAlarmArticleDto[] stockAlarmArticle) {
        this.stockAlarmArticle = stockAlarmArticle;
    }

    @Override
    public String toString() {
        return "StockAlarmCreationDto{" +
                "description='" + description + '\'' +
                ", provider='" + provider + '\'' +
                ", warning=" + warning +
                ", critical=" + critical +
                ", stockAlarmArticle=" + Arrays.toString(stockAlarmArticle) +
                '}';
    }
}
