package es.upm.miw.betca_tpv_spring.dtos;

import javax.validation.constraints.NotNull;

public class GiftTicketCreationDto {

    private String personalizedMessage;
    @NotNull
    private String ticket;

    public GiftTicketCreationDto() {
        // Empty for framework
    }

    public GiftTicketCreationDto(String personalizedMessage, @NotNull String ticket) {
        this.personalizedMessage = personalizedMessage;
        this.ticket = ticket;
    }

    public String getPersonalizedMessage() {
        return personalizedMessage;
    }

    public void setPersonalizedMessage(String personalizedMessage) {
        this.personalizedMessage = personalizedMessage;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "GiftTicketCreationDto{" +
                "personalizedMessage='" + personalizedMessage + '\'' +
                ", ticket=" + ticket +
                '}';
    }

}
