package es.upm.miw.betca_tpv_spring.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;

@Document
public class StockAlarm {

    @Id
    private String id;
    private String description;
    @DBRef
    private Provider provider;
    private Integer warning;
    private Integer critical;
    private StockAlarmArticle[] stockAlarmArticle;

    public StockAlarm() {
    }

    public StockAlarm(String description, Provider provider, Integer warning, Integer critical, StockAlarmArticle[] stockAlarmArticle) {
        this.description = description;
        this.provider = provider;
        this.warning = warning;
        this.critical = critical;
        this.stockAlarmArticle = stockAlarmArticle;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
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
        return "StockAlarm{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", provider='" + provider + '\'' +
                ", warning=" + warning +
                ", critical=" + critical +
                ", alarmArticle=" + Arrays.toString(stockAlarmArticle) +
                '}';
    }
}
