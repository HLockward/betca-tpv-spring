package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.api_rest_controllers.ApiTestConfig;
import es.upm.miw.betca_tpv_spring.repositories.CustomerDiscountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ApiTestConfig
public class CustomerDiscountControllerIT {

    @Autowired
    CustomerDiscountController customerDiscountController;

    @Autowired
    private CustomerDiscountRepository customerDiscountRepository;

    @Test
    void testFindCustomerDiscountByUserMobile() {
        StepVerifier
                .create(this.customerDiscountController.findByUserMobile("666666004"))
                .expectNextMatches(cd -> {
                    assertNotNull(cd);
                    assertEquals(new BigDecimal(50), cd.getDiscount());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    /*@Test
    void testCreateCustomerDiscount() {
        CustomerDiscountDto customerDiscountDto = new CustomerDiscountDto("desc5", LocalDateTime.now(),
                new BigDecimal(15), new BigDecimal(5), User.builder().build());
        StepVerifier
                .create(this.customerDiscountController.createCustomerDiscount(customerDiscountDto))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }*/

}