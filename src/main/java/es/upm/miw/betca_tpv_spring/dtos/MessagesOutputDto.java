package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.Messages;

import java.time.LocalDateTime;

public class MessagesOutputDto {

    private String id;
    private String fromUserMobile;
    private String toUserMobile;
    private String messageContent;
    private LocalDateTime sentDate;
    private LocalDateTime readDate;

    public MessagesOutputDto(){
        // Empty for framework
    }

    public MessagesOutputDto(String id, String fromUserMobile, String toUserMobile, String messageContent, LocalDateTime sentDate, LocalDateTime readDate) {
        this.id = id;
        this.fromUserMobile = fromUserMobile;
        this.toUserMobile = toUserMobile;
        this.messageContent = messageContent;
        this.sentDate = sentDate;
        this.readDate = readDate;
    }

    public MessagesOutputDto(Messages messages) {
        this.id = messages.getId();
        this.fromUserMobile = messages.getFromUser().getMobile();
        this.toUserMobile = messages.getToUser().getMobile();
        this.messageContent = messages.getMessageContent();
        this.sentDate = messages.getSentDate();
        this.readDate = messages.getReadDate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDateTime getReadDate() {
        return readDate;
    }

    public void setReadDate(LocalDateTime readDate) {
        this.readDate = readDate;
    }

    @Override
    public String toString() {
        return "MessagesOutputDto{" +
                "id='" + id + '\'' +
                ", fromUserMobile='" + fromUserMobile + '\'' +
                ", toUserMobile='" + toUserMobile + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", sentDate=" + sentDate +
                ", readDate=" + readDate +
                '}';
    }
}
