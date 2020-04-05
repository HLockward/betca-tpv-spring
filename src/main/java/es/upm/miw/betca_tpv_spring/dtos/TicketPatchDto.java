package es.upm.miw.betca_tpv_spring.dtos;

import java.util.ArrayList;
import java.util.List;

public class TicketPatchDto {

    private List<ShoppingPatchDto> shoppingPatchDtoList;

    public TicketPatchDto() {
        this.shoppingPatchDtoList = new ArrayList<>();
    }

    public List<ShoppingPatchDto> getShoppingPatchDtoList() {
        return shoppingPatchDtoList;
    }

    @Override
    public String toString() {
        return "TicketPatchDto{" +
                "shoppingPatchDtoList=" + shoppingPatchDtoList +
                '}';
    }
}
