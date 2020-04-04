package es.upm.miw.betca_tpv_spring.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

public class Messages {

    @Id
    private String id;
    @DBRef
    private User fromUser;
    @DBRef
    private User toUser;
    private String messageContent;
    private LocalDateTime sentDate;
    private LocalDateTime readDate;

    public Messages(String id, User fromUser, User toUser, String messageContent, LocalDateTime sentDate, LocalDateTime readDate) {
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

    public int getIdParsedToInteger() {
        return Integer.parseInt(id);
    }

    public void setIdFromInt(int idInt) {
        this.id = "" + idInt;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
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
        return "Messages{" +
                "id='" + id + '\'' +
                ", fromUser='" + fromUser +
                ", toUser='" + toUser +
                ", messageContent='" + messageContent + "\'" +
                ", sentDate='" + sentDate + "\'" +
                ", readDate='" + readDate + "\'" +
                "}";
    }
}
