package es.upm.miw.betca_tpv_spring.documents;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void testUserBuilder() {
        LocalDateTime registrationDate = LocalDateTime.now();
        Role[] roles = {Role.OPERATOR};
        User user = User.builder().mobile("666666666").registrationDate(registrationDate).username("user").active(false).email("user@gmail.com")
                .dni("55555555U").address("C/User").roles(roles).build();
        assertEquals("666666666", user.getMobile());
        assertEquals(registrationDate, user.getRegistrationDate());
        assertEquals("user", user.getUsername());
        assertEquals(false, user.isActive());
        assertEquals("user@gmail.com", user.getEmail());
        assertEquals("55555555U", user.getDni());
        assertEquals("C/User", user.getAddress());
        assertEquals(roles, user.getRoles());
    }
}