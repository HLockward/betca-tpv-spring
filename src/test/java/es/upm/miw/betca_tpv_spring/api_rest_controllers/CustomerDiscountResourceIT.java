package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.dtos.CustomerDiscountDto;
import es.upm.miw.betca_tpv_spring.repositories.CustomerDiscountReactRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.CustomerDiscountResource.*;

@ApiTestConfig
class CustomerDiscountResourceIT {

    private List<CustomerDiscountDto> customerDiscounts;

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    private CustomerDiscountReactRepository customerDiscountReactRepository;

    @BeforeEach
    void beforeTest() {
        customerDiscounts = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + CUSTOMER_DISCOUNTS)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDiscountDto.class)
                .returnResult().getResponseBody();
    }

    @Test
    void testReadAllCustomerDiscounts() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + CUSTOMER_DISCOUNTS)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateCustomerDiscount() {
        CustomerDiscountDto customerDiscountDto = new CustomerDiscountDto();

        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + CUSTOMER_DISCOUNTS)
                .body(BodyInserters.fromObject(customerDiscountDto))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testFindCustomerDiscountByUserMobile() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + CUSTOMER_DISCOUNTS + MOBILE_ID, "666666004")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDiscountDto.class)
                .value(Assertions::assertNotNull);
    }

    @Test
    void testDeleteCustomerDiscount() {
        this.restService.loginAdmin(webTestClient)
                .delete().uri(contextPath + CUSTOMER_DISCOUNTS + ID, 555)
                .exchange()
                .expectStatus().isOk();
    }

}