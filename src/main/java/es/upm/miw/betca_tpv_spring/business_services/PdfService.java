package es.upm.miw.betca_tpv_spring.business_services;

import es.upm.miw.betca_tpv_spring.documents.*;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class PdfService {

    private static final String[] TABLE_COLUMNS_HEADERS = {" ", "Desc.", "Ud.", "Dto.%", "€", "E."};
    private static final String[] TABLE_COLUMNS_HEADERS_INVOICES = {" ", "Desc.", "Price", "Ud.", "€"};

    private static final float[] TABLE_COLUMNS_SIZES_TICKETS = {15, 90, 15, 25, 35, 15};
    private static final float[] TABLE_COLUMNS_SIZES_BUDGETS = {15, 90, 15, 25, 35, 15};
    private static final float[] TABLE_COLUMNS_SIZES_INVOICES = {15, 90, 35, 15, 35};

    public static final String USER_HOME = "user.home";
    public static final String PDF_FILE_EXT = ".pdf";

    @Value("${miw.company.logo}")
    private String logo;

    @Value("${miw.company.name}")
    private String name;

    @Value("${miw.company.nif}")
    private String nif;

    @Value("${miw.company.phone}")
    private String phone;

    @Value("${miw.company.address}")
    private String address;

    @Value("${miw.company.email}")
    private String email;

    @Value("${miw.company.web}")
    private String web;

    private FileService fileService;

    public PdfService(FileService fileService) {
        this.fileService = fileService;
    }

    private void addHead(PdfBuilder pdf) {
        pdf.image(this.logo).paragraphEmphasized(this.name).paragraphEmphasized("Tfn: " + this.phone)
                .paragraph("NIF: " + this.nif + "   -   " + this.address)
                .paragraph("Email: " + this.email + "  -  " + "Web: " + this.web);
        pdf.line();
    }

    private void addCostumerHead(PdfBuilder pdf, User user) {
        pdf.paragraphEmphasized("COSTUMER");
        pdf.paragraphEmphasized(user.getUsername()).paragraphEmphasized("Tfn: " + user.getMobile())
                .paragraph("NIF: " + user.getDni() + "   -   " + user.getAddress())
                .paragraph("Email: " + user.getEmail());
        pdf.line();
    }

    private void addBookingDetails(PdfBuilder pdf, int notCommitted, Ticket ticket) {
        if (notCommitted > 0) {
            pdf.paragraphEmphasized("Items pending delivery: " + notCommitted);
            if (ticket.getUser() != null) {
                pdf.paragraph("Contact phone: " + ticket.getUser().getMobile() + " - " + ticket.getUser().getUsername().substring(0,
                        (ticket.getUser().getUsername().length() > 10) ? 10 : ticket.getUser().getUsername().length()));
            }
            pdf.qrCode(ticket.getReference());
        }
    }

    private void addVoucherValue(PdfBuilder pdf, Voucher voucher) {
        pdf.paragraphEmphasized(voucher.getValue() + " €")
                .paragraphEmphasized(" ").line();
    }

    private void addFoot(PdfBuilder pdf) {
        pdf.line().paragraph("Items can be returned within 15 days of shopping");
        pdf.paragraphEmphasized("Thanks for your visit and please send us your suggestions to help us improve this service")
                .paragraphEmphasized(" ").line();
    }

    private void addFootVoucher(PdfBuilder pdf) {
        pdf.line().paragraph("Voucher can be used within 30 days after its creation");
        pdf.paragraphEmphasized("Thanks for your visit and please send us your suggestions to help us improve this service")
                .paragraphEmphasized(" ").line();
    }

    public Mono<byte[]> generateTicket(Mono<Ticket> ticketReact) {
        return ticketReact.map(ticket -> {
            final String path = "/tpv-pdfs/tickets/ticket-" + ticket.getId();
            PdfBuilder pdf = new PdfBuilder(path);
            this.addHead(pdf);
            if (ticket.getCustomerPoints() != null)
                pdf.paragraphEmphasized("Acummulated points: " + ticket.getCustomerPoints().getPoints());

            if (ticket.isDebt()) {
                pdf.paragraphEmphasized("BOOKING");
                pdf.paragraphEmphasized("Paid: " + ticket.pay().setScale(2, RoundingMode.HALF_UP) + "€");
                pdf.paragraphEmphasized("Owed: " + ticket.debt().setScale(2, RoundingMode.HALF_UP) + "€");
            } else {
                pdf.paragraphEmphasized("TICKET");
            }
            pdf.barCode(ticket.getId()).line();
            pdf.paragraphEmphasized(ticket.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            int notCommitted = 0;
            PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_TICKETS).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
            for (int i = 0; i < ticket.getShoppingList().length; i++) {
                Shopping shopping = ticket.getShoppingList()[i];
                String state = "";
                if (shopping.getShoppingState() != ShoppingState.COMMITTED && shopping.getAmount() > 0) {
                    state = "N";
                    notCommitted++;
                }
                String discount = "";
                if ((shopping.getDiscount().doubleValue() > 0.009) && !shopping.getArticleId().equals("1")) {
                    discount = "" + shopping.getDiscount().setScale(2, RoundingMode.HALF_UP);
                }
                table.tableCell(String.valueOf(i + 1), shopping.getDescription(), "" + shopping.getAmount(), discount,
                        shopping.getShoppingTotal().setScale(2, RoundingMode.HALF_UP) + "€", state);
            }
            table.tableColspanRight(ticket.getTotal().setScale(2, RoundingMode.HALF_UP) + "€").build();

            pdf.paragraph(ticket.getNote());
            this.addBookingDetails(pdf, notCommitted, ticket);
            this.addFoot(pdf);
            return pdf.build();
        });
    }

    public Mono<byte[]> generateGiftTicket(Mono<GiftTicket> giftTicketReact) {
        return giftTicketReact.map(giftTicket -> {
            final String path = "/tpv-pdfs/tickets/gift-ticket-" + giftTicket.getId();
            PdfBuilder pdf = new PdfBuilder(path);
            Ticket myTicket = giftTicket.getTicket();
            this.addHead(pdf);
            pdf.paragraphEmphasized("GIFT TICKET");
            pdf.barCode(giftTicket.getId()).line();
            pdf.paragraphEmphasized(myTicket.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_TICKETS).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
            for (int i = 0; i < myTicket.getShoppingList().length; i++) {
                Shopping shopping = myTicket.getShoppingList()[i];
                table.tableCell(String.valueOf(i + 1), shopping.getDescription(), "" + shopping.getAmount(), " - ", " - ", " - ");
            }
            table.build();
            pdf.paragraph("Expiration date: " + giftTicket.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pdf.line().paragraphEmphasized("Message: " + giftTicket.getPersonalizedMessage())
                    .paragraphEmphasized(" ").line();
            return pdf.build();
        });
    }

    public Mono<byte[]> generateBudget(Mono<Budget> budgetReact) {
        return budgetReact.map(budget -> {
            final String path = "/tpv-pdfs/budgets/budget-" + budget.getId();
            PdfBuilder pdf = new PdfBuilder(path);
            this.addHead(pdf);
            pdf.barCode(budget.getId());
            pdf.paragraphEmphasized("BUDGET");
            pdf.paragraphEmphasized(budget.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            double total = 0;
            PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_BUDGETS).tableColumnsHeader(TABLE_COLUMNS_HEADERS);
            for (int i = 0; i < budget.getShoppingList().length; i++) {
                String state = "";
                Shopping shopping = budget.getShoppingList()[i];
                String discount = "" + shopping.getDiscount().setScale(2, RoundingMode.HALF_UP);
                if (shopping.getShoppingState() != ShoppingState.COMMITTED && shopping.getAmount() > 0) {
                    state = "N";
                }
                total = total + shopping.getShoppingTotal().doubleValue();
                table.tableCell(String.valueOf(i + 1), shopping.getDescription(), "" + shopping.getAmount(), discount,
                        shopping.getShoppingTotal().setScale(2, RoundingMode.HALF_UP) + "€",state);
            }
            table.tableColspanRight(total + "€").build();
            return pdf.build();
        });
    }

    public Mono<byte[]> generateVoucher(Mono<Voucher> voucherReact) {
        return voucherReact.map(voucher -> {
            final String path = "/tpv-pdfs/vouchers/voucher-" + voucher.getId();
            PdfBuilder pdf = new PdfBuilder(path);
            this.addHead(pdf);
            pdf.qrCode(voucher.getId());
            pdf.paragraphEmphasized(voucher.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            this.addVoucherValue(pdf, voucher);
            this.addFootVoucher(pdf);
            return pdf.build();
        });
    }

    public Mono<byte[]> generateInvoice(Mono<Invoice> invoiceReact) {
        return invoiceReact.map(invoice -> buildInvoicePdf(invoice, invoice.getTicket().getShoppingList()));
    }

    public Mono<byte[]> generateNegativeInvoice(Mono<Invoice> invoiceReact, Shopping[] returnedShoppings) {
        return invoiceReact.map(invoice -> buildInvoicePdf(invoice, returnedShoppings));
    }

    public byte[] buildInvoicePdf(Invoice invoice, Shopping[] shoppings) {
        final String path = "/tpv-pdfs/invoices/invoice-" + invoice.getId();
        PdfBuilder pdf = new PdfBuilder(path);
        this.addHead(pdf);
        this.addCostumerHead(pdf, invoice.getUser());
        pdf.paragraphEmphasized("Invoice Date:" +
                invoice.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        PdfTableBuilder table = pdf.table(TABLE_COLUMNS_SIZES_INVOICES).tableColumnsHeader(TABLE_COLUMNS_HEADERS_INVOICES);
        BigDecimal total = Stream.of(shoppings)
                .map(Shopping::getShoppingTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (int i = 0; i < shoppings.length; i++) {
            Shopping shopping = shoppings[i];
            table.tableCell(String.valueOf(i + 1), shopping.getDescription(),
                    shopping.getTotalUnitPrice().setScale(2, RoundingMode.HALF_UP) + "€", "" + shopping.getAmount(),
                    shopping.getShoppingTotal().setScale(2, RoundingMode.HALF_UP) + "€");
        }
        table.tableColspanRight("TAX BASE");
        table.tableColspanRight(invoice.getBaseTax().setScale(2, RoundingMode.HALF_UP) + "€");
        table.tableColspanRight("TOTAL TAX");
        table.tableColspanRight(invoice.getTax().setScale(2, RoundingMode.HALF_UP) + "€");
        table.tableColspanRight("TOTAL");
        table.tableColspanRight(total.setScale(2, RoundingMode.HALF_UP) + "€").build();
        return pdf.build();
    }

    private void addOfferValue(PdfBuilder pdf, Offer offer) {
        pdf.paragraphEmphasized(offer.getDiscount() + " %")
                .paragraphEmphasized(" ").line();
    }

    public Mono<byte[]> generateOffer(Mono<Offer> offerReact) {
        return offerReact.map(offer -> {
            final String path = "/tpv-pdfs/offers/offer-" + offer.getId();
            PdfBuilder pdf = new PdfBuilder(path);
            this.addHead(pdf);
            pdf.qrCode(offer.getId());
            pdf.paragraphEmphasized(offer.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            this.addOfferValue(pdf, offer);
            return pdf.build();
        });
    }

    public byte[] readPdf(String filepath) {
        return fileService.read(System.getProperty(PdfBuilder.USER_HOME) + filepath + PdfBuilder.PDF_FILE_EXT);
    }

    public byte[] getPdfFromTicketId(String ticketId) throws IOException {
        final String path = "/tpv-pdfs/tickets/ticket-" + ticketId;
        String filename = System.getProperty(USER_HOME) + path + PDF_FILE_EXT;
        File file = new File(filename);
        if(!file.exists()){
            throw new NotFoundException("File with ticket id " +ticketId+" not found");
        }
        return Files.readAllBytes(file.toPath());
    }

    public Mono<byte[]> generateTag(Mono<Tag> tagReact){
        return tagReact.map(tag -> {
            final String path = "/tpv-pdfs/tags/tag-" + tag.getId();
            PdfBuilder pdf = new PdfBuilder(path);
            this.addHead(pdf);
            pdf.qrCode(tag.getId());
            return pdf.build();
        });
    }
}
