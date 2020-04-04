package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.documents.Quarter;
import es.upm.miw.betca_tpv_spring.dtos.QuarterVATDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@ApiTestConfig
public class VatResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testReadVatFromQuarter() {
        QuarterVATDto quarterVATDto = this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                        .path(contextPath + VatResource.VAT + "/" + Quarter.Q2)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(QuarterVATDto.class)
                .returnResult().getResponseBody();
        assertNotNull(quarterVATDto);
        assertNotEquals(quarterVATDto.getTaxes().size(), 0);
    }

    @Test
    void testReadVatFromQuarterBadQuarterPath() {
        Exception exception = this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                        .path(contextPath + VatResource.VAT + "/Q5")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(Exception.class)
                .returnResult().getResponseBody();
        assertNotNull(exception);
    }
}
