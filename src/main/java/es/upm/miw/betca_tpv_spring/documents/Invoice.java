package es.upm.miw.betca_tpv_spring.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Document
public class Invoice {

    private static final String DATE_FORMAT = "yyyy";

    @Id
    private String id;
    private LocalDateTime creationDate;
    private BigDecimal baseTax;
    private BigDecimal tax;
    @DBRef
    private Ticket ticket;
    @DBRef
    private User user;

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Invoice() {
        creationDate = LocalDateTime.now();
    }
    public Invoice(int idOfYear, User user, Ticket ticket) {
        this();
        this.id = new SimpleDateFormat(DATE_FORMAT).format(new Date()) + idOfYear;
        this.user = user;
        this.ticket = ticket;
        this.tax = BigDecimal.ZERO;
        this.baseTax = BigDecimal.ZERO;
    }

    public String getId() {
        return id;
    }

    public void setId(int idOfYear) {
        this.id = new SimpleDateFormat(DATE_FORMAT).format(new Date()) + idOfYear;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int simpleId() {
        return Integer.parseInt(String.valueOf(id).substring(DATE_FORMAT.length()));
    }

    public BigDecimal getBaseTax() {
        return baseTax;
    }

    public void setBaseTax(BigDecimal baseTax) {
        this.baseTax = baseTax;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && (id.equals(((Invoice) obj).id));
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", creationDate=" + creationDate +
                ", baseTax=" + baseTax +
                ", tax=" + tax +
                ", ticket=" + ticket +
                ", user=" + user +
                '}';
    }
}
