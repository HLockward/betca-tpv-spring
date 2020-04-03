package es.upm.miw.betca_tpv_spring.documents;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Messages {

    @Id
    private ObjectId id;

    private String fromUserMobile;
    private String toUserMobile;
    private String messageContent;
    private LocalDateTime sentDate;
    private LocalDateTime readDate;

    public Messages(String fromUserMobile, String toUserMobile, String messageContent, LocalDateTime sentDate, LocalDateTime readDate) {
        this.id = ObjectId.get();
        this.fromUserMobile = fromUserMobile;
        this.toUserMobile = toUserMobile;
        this.messageContent = messageContent;
        this.sentDate = sentDate;
        this.readDate = readDate;
    }

    public ObjectId getId() {
        return id;
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
        return "Messages{" +
                "id='" + id + '\'' +
                ", fromUserMobile='" + fromUserMobile + "\'" +
                ", toUserMobile='" + toUserMobile + "\'" +
                ", messageContent='" + messageContent + "\'" +
                ", sentDate='" + sentDate + "\'" +
                ", readDate='" + readDate + "\'" +
                "}";
    }
}
