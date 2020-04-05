package es.upm.miw.betca_tpv_spring.dtos;

public class GiftTicketCreationDto {

    private String personalizedMessage;

    public GiftTicketCreationDto() {
        // Empty for framework
    }

    public GiftTicketCreationDto(String personalizedMessage) {
        this.personalizedMessage = personalizedMessage;
    }

    public String getPersonalizedMessage() {
        return personalizedMessage;
    }

    public void setPersonalizedMessage(String personalizedMessage) {
        this.personalizedMessage = personalizedMessage;
    }

    @Override
    public String toString() {
        return "GiftTicketCreationDto{" +
                "personalizedMessage='" + personalizedMessage + '\'' +
                '}';
    }
}
