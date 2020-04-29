package es.upm.miw.betca_tpv_spring.dtos;

public class StockAlarmArticleDto {

    private String articleId;

    private Integer warning;

    private Integer critical;

    public StockAlarmArticleDto(String articleId, Integer warning, Integer critical) {
        this.articleId = articleId;
        this.warning = warning;
        this.critical = critical;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
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
        return "StockAlarmArticleDto{" +
                "articleId='" + articleId + '\'' +
                ", warning=" + warning +
                ", critical=" + critical +
                '}';
    }
}
