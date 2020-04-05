package es.upm.miw.betca_tpv_spring.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Document
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String mobile;
    private LocalDateTime registrationDate;
    private String username;
    private String password;
    private Boolean active;
    private String email;
    private String dni;
    private String address;
    private Role[] roles;

    public User() {
        this.registrationDate = LocalDateTime.now();
        this.active = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) {
            this.password = UUID.randomUUID().toString();
        } else {
            this.password = new BCryptPasswordEncoder().encode(password);
        }
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role[] getRoles() {
        return roles;
    }

    public void setRoles(Role[] roles) {
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        return this.mobile.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass() && mobile.equals(((User) obj).mobile);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", registrationDate=" + registrationDate +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                ", email='" + email + '\'' +
                ", dni='" + dni + '\'' +
                ", address='" + address + '\'' +
                ", roles=" + Arrays.toString(roles) +
                '}';
    }

    public static class Builder {
        private User user;

        private Builder() {
            this.user = new User();
            this.user.setPassword(null);
            this.user.roles = new Role[]{Role.CUSTOMER};
        }

        public Builder mobile(String mobile) {
            this.user.mobile = mobile;
            return this;
        }

        public Builder registrationDate(LocalDateTime registrationDate) {
            this.user.registrationDate = registrationDate;
            return this;
        }

        public Builder username(String username) {
            this.user.username = username;
            return this;
        }

        public Builder password(String password) {
            this.user.setPassword(password);
            return this;
        }

        public Builder active(Boolean active) {
            this.user.active = active;
            return this;
        }

        public Builder email(String email) {
            this.user.email = email;
            return this;
        }

        public Builder dni(String dni) {
            this.user.dni = dni;
            return this;
        }

        public Builder address(String address) {
            this.user.address = address;
            return this;
        }

        public Builder roles(Role... roles) {
            this.user.roles = roles;
            return this;
        }

        public User build() {
            return this.user;
        }
    }
}
