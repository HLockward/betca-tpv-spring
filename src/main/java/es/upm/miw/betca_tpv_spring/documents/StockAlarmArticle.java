package es.upm.miw.betca_tpv_spring.documents;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class StockAlarmArticle {

    @DBRef
    private Article article;

    private Integer warning;

    private Integer critical;

    public StockAlarmArticle() {
    }

    public StockAlarmArticle(Article article, Integer warning, Integer critical) {
        this.article = article;
        this.warning = warning;
        this.critical = critical;
    }

    public StockAlarmArticle(StockAlarmArticle stockAlarmArticle) {
        this.article = stockAlarmArticle.getArticle();
        this.warning = stockAlarmArticle.getWarning();
        this.critical = stockAlarmArticle.getCritical();
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
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
        return "AlarmArticle{" +
                "articleId='" + article + '\'' +
                ", warning=" + warning +
                ", critical=" + critical +
                '}';
    }
}
