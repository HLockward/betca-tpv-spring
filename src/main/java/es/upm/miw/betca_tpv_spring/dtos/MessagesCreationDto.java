package es.upm.miw.betca_tpv_spring.dtos;

import java.time.LocalDateTime;

public class MessagesCreationDto {

    private String fromUserMobile;
    private String toUserMobile;
    private String messageContent;
    private LocalDateTime sentDate;

    public MessagesCreationDto() {
        // Empty for framework
    }

    public MessagesCreationDto(String fromUserMobile, String toUserMobile, String messageContent, LocalDateTime sentDate) {
        this.fromUserMobile = fromUserMobile;
        this.toUserMobile = toUserMobile;
        this.messageContent = messageContent;
        this.sentDate = sentDate;
    }

    public String getFromUserMobile() {
        return fromUserMobile;
    }

    public void setFromUserMobile(String fromUserMobile) {
        this.fromUserMobile = fromUserMobile;
    }

    public String getToUserMobile() {
        return toUserMobile;
    }

    public void setToUserMobile(String toUserMobile) {
        this.toUserMobile = toUserMobile;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public String toString() {
        return "MessagesCreationDto{" +
                "fromUserMobile=" + fromUserMobile +
                ", toUserMobile=" + toUserMobile +
                ", messageContent='" + messageContent + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }
}
