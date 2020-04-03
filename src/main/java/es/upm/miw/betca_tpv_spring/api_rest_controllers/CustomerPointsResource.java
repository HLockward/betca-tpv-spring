package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.CustomerPointsController;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(CustomerPointsResource.CUSTOMER_POINTS)
public class CustomerPointsResource {

    public static final String CUSTOMER_POINTS = "/customer-points";
    public static final String MOBILE_ID = "/{mobile}";

    private CustomerPointsController customerPointsController;

    @Autowired
    public CustomerPointsResource(CustomerPointsController customerPointsController) {
        this.customerPointsController = customerPointsController;
    }

    @GetMapping(value = MOBILE_ID)
    public Mono<Integer> readCustomerPointsByUserMobile(@PathVariable String mobile) {
        return this.customerPointsController.getCustomerPointsByUserMobile(mobile)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PutMapping(value = MOBILE_ID)
    public Mono<Void> updateCustomerPointsByUserMobile(@PathVariable String mobile, @Valid @RequestBody Integer points) {
        return this.customerPointsController.setCustomerPointsByUserMobile(mobile, points)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PostMapping
    public Mono<String> createCustomerPointsByExistingUserMobile(@Valid @RequestBody String mobile) {
        return this.customerPointsController.createCustomerPointsByExistingUserMobile(mobile)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }
}
