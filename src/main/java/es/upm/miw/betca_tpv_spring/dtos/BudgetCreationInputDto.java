package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.dtos.validations.ListNotEmpty;

import java.util.List;

public class BudgetCreationInputDto {
    @ListNotEmpty
    private List<ShoppingDto> shoppingCart;

    public BudgetCreationInputDto() {
        // Empty for framework
    }

    public BudgetCreationInputDto(List<ShoppingDto> shoppingCart){
        this.shoppingCart =shoppingCart;
    }

    public List<ShoppingDto> getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(List<ShoppingDto> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

}
