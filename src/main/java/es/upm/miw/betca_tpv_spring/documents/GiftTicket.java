package es.upm.miw.betca_tpv_spring.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class GiftTicket {

    @Id
    private String id;
    private LocalDateTime expirationDate;
    private String personalizedMessage;
    @DBRef
    private Ticket ticket;

    public GiftTicket() {
        this.id = new Encode().generateUUIDUrlSafe();
        this.expirationDate = LocalDateTime.now().plusDays(15);
        this.personalizedMessage = "";
    }

    public GiftTicket(String personalizedMessage, Ticket ticket) {
        this();
        this.personalizedMessage = personalizedMessage;
        this.ticket = ticket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPersonalizedMessage() {
        return personalizedMessage;
    }

    public void setPersonalizedMessage(String personalizedMessage) {
        this.personalizedMessage = personalizedMessage;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && id.equals(((GiftTicket) obj).id);
    }

    @Override
    public String toString() {
        return "GiftTicket{" +
                "id='" + id + '\'' +
                ", expirationDate=" + expirationDate +
                ", personalizedMessage='" + personalizedMessage + '\'' +
                ", ticket=" + ticket +
                '}';
    }
}
