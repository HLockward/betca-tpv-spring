package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.TestConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
public class CustomerPointsControllerIT {

    @Autowired
    private CustomerPointsController customerPointsController;

    @Test
    void testUpdateCustomerPointsByUserMobile() {
        StepVerifier
                .create(this.customerPointsController.setCustomerPointsByUserMobile("666666002", 100))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdateCustomerPointsByUserMobileNotFound() {
        StepVerifier
                .create(this.customerPointsController.setCustomerPointsByUserMobile("666664002", 10))
                .expectNextCount(0)
                .expectError()
                .verify();
    }

    @Test
    void sendCustomerPointsByUserMobileTest() {
        StepVerifier
                .create(this.customerPointsController.getCustomerPointsByUserMobile("666666003"))
                .expectNextMatches(cp -> {
                    assertNotNull(cp);
                    assertEquals(40, cp.intValue());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void sendCustomerPointsByUserMobileNotFoundTest() {
        StepVerifier
                .create(this.customerPointsController.getCustomerPointsByUserMobile("666666e003"))
                .expectError()
                .verify();
    }

    @Test
    void createCustomerPointsByUserMobileTest() {
        StepVerifier
                .create(this.customerPointsController.createCustomerPointsByExistingUserMobile("666666006"))
                .expectNextMatches(cp -> {
                    assertNotNull(cp);
                    assertEquals("666666006", cp);
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void createCustomerPointsByUserMobileNotFoundTest() {
        StepVerifier
                .create(this.customerPointsController.createCustomerPointsByExistingUserMobile("6666667001"))
                .expectError()
                .verify();
    }
}
