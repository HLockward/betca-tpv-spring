package es.upm.miw.betca_tpv_spring.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_spring.documents.Budget;
import es.upm.miw.betca_tpv_spring.dtos.validations.ListNotEmpty;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BudgetDto {
    @ListNotEmpty
    private List<ShoppingDto> shoppingCart;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime creationDate;
    public BudgetDto() {
        // Empty for framework
    }
    public BudgetDto(List<ShoppingDto> shoppingCart, LocalDateTime creationDate){
        this.shoppingCart =shoppingCart;
        this.creationDate = creationDate;
    }

    public BudgetDto(Budget budget) {
        this.shoppingCart = Arrays.asList(budget.getShoppingList()).stream().map(ShoppingDto::new).collect(Collectors.toList());
        this.creationDate = budget.getCreationDate();
    }

    public List<ShoppingDto> getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(List<ShoppingDto> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}
