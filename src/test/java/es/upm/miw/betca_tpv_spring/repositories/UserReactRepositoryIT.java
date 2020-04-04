package es.upm.miw.betca_tpv_spring.repositories;

import es.upm.miw.betca_tpv_spring.TestConfig;
import es.upm.miw.betca_tpv_spring.documents.Messages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ManagedList;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class UserReactRepositoryIT {

    @Autowired
    private UserReactRepository userReactRepository;

    @Test
    void testFindAllAndDatabaseSeeder() {
        StepVerifier
                .create(this.userReactRepository.findAll())
                .expectNextCount(1)
                .expectNextMatches(user -> {
                    assertEquals("666666000", user.getMobile());
                    assertEquals("all-roles", user.getUsername());
                    assertNotEquals("p000", user.getPassword());
                    assertEquals("C/TPV, 0, MIW", user.getAddress());
                    assertEquals("u000@gmail.com", user.getEmail());
                    assertNull(user.getDni());
                    assertNotNull(user.getRegistrationDate());
                    assertTrue(user.isActive());
                    assertNotNull(user.getRoles());
                    assertFalse(user.toString().matches("@"));
                    return true;
                })
                .thenCancel()
                .verify();
    }

}

