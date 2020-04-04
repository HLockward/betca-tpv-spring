package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.Messages;
import es.upm.miw.betca_tpv_spring.documents.User;

import java.time.LocalDateTime;

public class MessagesDto {

    private String id;
    private User fromUser;
    private User toUser;
    private String messageContent;
    private LocalDateTime sentDate;
    private LocalDateTime readDate;

    public MessagesDto() {
        // Empty for framework
    }

    public MessagesDto(String id, User fromUser, User toUser, String messageContent, LocalDateTime sentDate, LocalDateTime readDate) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageContent = messageContent;
        this.sentDate = sentDate;
        this.readDate = readDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public MessagesDto(Messages messages) {
        this.fromUser = messages.getFromUser();
        this.toUser = messages.getToUser();
        this.messageContent = messages.getMessageContent();
        this.sentDate = messages.getSentDate();
        this.readDate = messages.getReadDate();
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
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
        return "MessagesDto [" +
                "id=" + id +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
                ", messageContent=" + messageContent +
                ", sentDate=" + sentDate +
                ", readDate=" + readDate +
                ']';
    }
}
