package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.Order;
import es.upm.miw.betca_tpv_spring.dtos.OrderSearchDto;
import es.upm.miw.betca_tpv_spring.repositories.OrderReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class OrderController {

    private OrderReactRepository orderReactRepository;

    @Autowired
    public OrderController(OrderReactRepository orderReactRepository) {
        this.orderReactRepository = orderReactRepository;
    }

    public Flux<Order> searchOrder(OrderSearchDto orderSearchDto) {
        if (orderSearchDto.getClosingDate() == null) {
            return this.orderReactRepository.findByDescriptionLikeOrProviderAndClosingDateIsNull(orderSearchDto.getDescription(), orderSearchDto.getProvider(), null);
        } else {
            return this.orderReactRepository.findByDescriptionLikeOrProvider(orderSearchDto.getDescription(), orderSearchDto.getProvider());
        }
    }
}
