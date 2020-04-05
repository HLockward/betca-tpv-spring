package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.Invoice;

public class InvoiceOutputDto {

    private String invoice;

    private String ticket;

    private String mobile;

    public InvoiceOutputDto() {
    }

    public InvoiceOutputDto(Invoice invoice) {
        this.invoice = invoice.getId();
        this.ticket = invoice.getTicket().getId();
        this.mobile = invoice.getUser().getMobile();
    }

    public String getInvoice() {
        return invoice;
    }

    public String getTicket() {
        return ticket;
    }

    public String getMobile() {
        return mobile;
    }

    @Override
    public String toString() {
        return "InvoiceOutputDto{" +
                "invoice='" + invoice + '\'' +
                ", ticket='" + ticket + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}