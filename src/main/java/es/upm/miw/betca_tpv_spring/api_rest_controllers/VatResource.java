package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.InvoiceController;
import es.upm.miw.betca_tpv_spring.documents.Quarter;
import es.upm.miw.betca_tpv_spring.dtos.QuarterVATDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(VatResource.VAT)
public class VatResource {

    public static final String VAT = "/vat-management";
    public static final String QUARTER = "/{quarter}";

    private InvoiceController invoiceController;

    @Autowired
    public VatResource(InvoiceController invoiceController) {
        this.invoiceController = invoiceController;
    }

    @GetMapping(value = QUARTER)
    public Mono<QuarterVATDto> readVatFromQuarter(@PathVariable Quarter quarter) {
        return this.invoiceController.readQuarterlyVat(quarter);
    }
}
