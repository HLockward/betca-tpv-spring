package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.OrderController;
import es.upm.miw.betca_tpv_spring.documents.Order;
import es.upm.miw.betca_tpv_spring.dtos.OrderSearchDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(OrderResource.ORDERS)
public class OrderResource {

    public static final String ORDERS = "/orders";

    private OrderController orderController;

    @Autowired
    public OrderResource(OrderController orderController) {
        this.orderController = orderController;
    }

    @GetMapping
    public Flux<Order> search(@RequestParam String description, @RequestParam String provider, @RequestParam String closingDate){
        OrderSearchDto orderSearchDto;

        if(closingDate.equals("null")){
            orderSearchDto = new OrderSearchDto(description, provider, null);
        } else {
            orderSearchDto = new OrderSearchDto(description, provider, LocalDateTime.now());
        }

        return this.orderController.searchOrder(orderSearchDto)
                .doOnEach(log -> LogManager.getLogger(this.getClass()).debug(log));
    }
}
