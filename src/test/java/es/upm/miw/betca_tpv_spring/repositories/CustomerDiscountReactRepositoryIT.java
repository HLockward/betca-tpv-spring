package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@TestConfig
public class CustomerDiscountReactRepositoryIT {

    @Autowired
    private CustomerDiscountReactRepository customerDiscountReactRepository;

    @Autowired
    private UserReactRepository userReactRepository;

    @Test
    void testFindAllAndDatabaseSeeder() {
        StepVerifier
                .create(this.customerDiscountReactRepository.findAll())
                .expectNextMatches(customerDiscount -> {
                    assertNotNull(customerDiscount.getId());
                    assertEquals("discount0", customerDiscount.getDescription());
                    assertNotNull(customerDiscount.getRegistrationDate());
                    assertEquals(BigDecimal.TEN, customerDiscount.getDiscount());
                    assertEquals(new BigDecimal(5), customerDiscount.getMinimumPurchase());
                    assertNotNull(customerDiscount.getUser());
                    assertFalse(customerDiscount.toString().matches("@"));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindDiscountByUserMobileAndDatabaseSeeder() {

        StepVerifier
                .create(this.customerDiscountReactRepository.findByUser(this.userReactRepository.findByMobile("666666004")))
                .expectNextMatches(customerDiscount -> {
                    assertNotNull(customerDiscount.getId());
                    assertEquals("discount4", customerDiscount.getDescription());
                    assertNotNull(customerDiscount.getRegistrationDate());
                    assertEquals(new BigDecimal(50), customerDiscount.getDiscount());
                    assertEquals(new BigDecimal(25), customerDiscount.getMinimumPurchase());
                    assertNotNull(customerDiscount.getUser());
                    assertFalse(customerDiscount.toString().matches("@"));
                    return true;
                })
                .thenCancel()
                .verify();
    }

}