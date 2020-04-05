package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.Messages;

import java.time.LocalDateTime;

public class MessagesOutputDto {

    private String id;
    private String fromUsername;
    private String toUsername;
    private String messageContent;
    private LocalDateTime sentDate;
    private LocalDateTime readDate;

    public MessagesOutputDto(){
        // Empty for framework
    }

    public MessagesOutputDto(String id, String fromUsername, String toUsername, String messageContent, LocalDateTime sentDate, LocalDateTime readDate) {
        this.id = id;
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.messageContent = messageContent;
        this.sentDate = sentDate;
        this.readDate = readDate;
    }

    public MessagesOutputDto(Messages messages) {
        this.id = messages.getId();
        this.fromUsername = messages.getFromUser().getUsername();
        this.toUsername = messages.getToUser().getUsername();
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

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
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
                ", fromUsername='" + fromUsername + '\'' +
                ", toUsername='" + toUsername + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", sentDate=" + sentDate +
                ", readDate=" + readDate +
                '}';
    }
}
