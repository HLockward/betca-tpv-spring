package es.upm.miw.betca_tpv_spring.dtos;

import es.upm.miw.betca_tpv_spring.documents.User;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class MessagesCreationDto {

    private User fromUser;
    private User toUser;
    private String messageContent;
    private LocalDateTime sentDate;

    public MessagesCreationDto() {
        // Empty for framework
    }

    public MessagesCreationDto(User fromUser, User toUser, String messageContent, LocalDateTime sentDate) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageContent = messageContent;
        this.sentDate = sentDate;
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

    @Override
    public String toString() {
        return "MessagesCreationDto{" +
                "fromUser=" + fromUser +
                ", toUser=" + toUser +
                ", messageContent='" + messageContent + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }
}
