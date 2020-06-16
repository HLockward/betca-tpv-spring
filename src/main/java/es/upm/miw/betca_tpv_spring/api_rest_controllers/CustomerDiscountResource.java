package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.CustomerDiscountController;
import es.upm.miw.betca_tpv_spring.dtos.CustomerDiscountCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.CustomerDiscountDto;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(CustomerDiscountResource.CUSTOMER_DISCOUNTS)
public class CustomerDiscountResource {

    public static final String CUSTOMER_DISCOUNTS = "/customer-discounts";
    public static final String MOBILE_ID = "/{mobile}";
    public static final String ID = "/{id}";

    private CustomerDiscountController customerDiscountController;

    @Autowired
    public CustomerDiscountResource(CustomerDiscountController customerDiscountController) {
        this.customerDiscountController = customerDiscountController;
    }

    @GetMapping
    public Flux<CustomerDiscountDto> readAll() {
        return this.customerDiscountController.readAll()
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PostMapping
    public Mono<CustomerDiscountDto> createCustomerDiscount(@Valid @RequestBody CustomerDiscountCreationDto customerDiscountCreationDto) {
        return this.customerDiscountController.createCustomerDiscount(customerDiscountCreationDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = MOBILE_ID)
    public Mono<CustomerDiscountDto> findByUserMobile(@PathVariable String mobile) {
        return this.customerDiscountController.findByUserMobile(mobile)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PutMapping(value = ID)
    public Mono<CustomerDiscountDto> updateCustomerDiscount(@PathVariable String id, @Valid @RequestBody CustomerDiscountCreationDto customerDiscountCreationDto) {
        return this.customerDiscountController.updateCustomerDiscount(id, customerDiscountCreationDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @DeleteMapping(value = ID)
    public Mono<Void> deleteCustomerDiscount(@PathVariable String id) {
        return this.customerDiscountController.deleteCustomerDiscount(id)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }


}
