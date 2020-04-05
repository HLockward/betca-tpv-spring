package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.ShoppingState;

public class ShoppingPatchDto {

    private String articleId;

    private Integer amount;

    private ShoppingState shoppingState;

    public ShoppingPatchDto() {
    }

    public ShoppingPatchDto(String articleId, Integer amount, ShoppingState shoppingState) {
        this.articleId = articleId;
        this.amount = amount;
        this.shoppingState = shoppingState;
    }

    public String getArticleId() {
        return articleId;
    }

    public Integer getAmount() {
        return amount;
    }

    public ShoppingState getShoppingState() {
        return shoppingState;
    }

    @Override
    public String toString() {
        return "ShoppingPatchDto{" +
                "amount=" + amount +
                ", shoppingState=" + shoppingState +
                '}';
    }
}
