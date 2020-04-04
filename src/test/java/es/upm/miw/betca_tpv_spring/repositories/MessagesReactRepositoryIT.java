package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class MessagesReactRepositoryIT {
    @Autowired
    private MessagesReactRepository messagesReactRepository;

    private LocalDateTime fixedLdt = LocalDateTime.of(2020, 4, 1, 1, 1, 1);

    @Test
    void testFindAllAndDatabaseSeeder() {
        StepVerifier
                .create(this.messagesReactRepository.findAll())
                .expectNextMatches(messages -> {
                    assertEquals("1", messages.getId());
                    assertNotNull(messages.getFromUser());
                    assertEquals("666666001", messages.getFromUser().getMobile());
                    assertNotNull(messages.getToUser());
                    assertEquals("666666007", messages.getToUser().getMobile());
                    assertEquals("Msg from 1 to 7", messages.getMessageContent());
                    assertEquals(fixedLdt, messages.getSentDate());
                    assertEquals(fixedLdt.plusDays(1), messages.getReadDate());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindById() {
        StepVerifier
                .create(this.messagesReactRepository.findById("2"))
                .expectNextMatches(messages -> {
                    assertEquals("2", messages.getId());
                    assertNotNull(messages.getFromUser());
                    assertEquals("666666002", messages.getFromUser().getMobile());
                    assertNotNull(messages.getToUser());
                    assertEquals("666666007", messages.getToUser().getMobile());
                    assertEquals("Msg from 2 to 7", messages.getMessageContent());
                    assertEquals(fixedLdt.plusDays(2), messages.getSentDate());
                    assertEquals(fixedLdt.plusDays(3), messages.getReadDate());
                    return true;
                })
                .expectComplete().verify();
    }

    @Test
    void testFindByIdNullReadDate() {
        StepVerifier
                .create(this.messagesReactRepository.findById("3"))
                .expectNextMatches(messages -> {
                    assertEquals("3", messages.getId());
                    assertNotNull(messages.getFromUser());
                    assertEquals("666666003", messages.getFromUser().getMobile());
                    assertNotNull(messages.getToUser());
                    assertEquals("666666007", messages.getToUser().getMobile());
                    assertEquals("Msg from 3 to 7", messages.getMessageContent());
                    assertEquals(fixedLdt.plusDays(4), messages.getSentDate());
                    assertNull(messages.getReadDate());
                    return true;
                })
                .expectComplete().verify();
    }
}
