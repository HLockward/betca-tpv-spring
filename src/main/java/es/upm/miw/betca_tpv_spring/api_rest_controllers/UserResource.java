package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.business_controllers.UserController;
import es.upm.miw.betca_tpv_spring.dtos.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OPERATOR')")
@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    public static final String USERS = "/users";
    public static final String TOKEN = "/token";
    public static final String MOBILE_ID = "/{mobile}";
    public static final String SEARCH = "/search";

    private UserController userController;

    @Autowired
    public UserResource(UserController userController) {
        this.userController = userController;
    }

    @PreAuthorize("authenticated")
    @PostMapping(value = TOKEN)
    public Mono<TokenOutputDto> login(@AuthenticationPrincipal User activeUser) {
        return userController.login(activeUser.getUsername())
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PostMapping
    public Mono<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return this.userController.createUser(userDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = MOBILE_ID)
    public Mono<UserDto> read(@PathVariable String mobile) {
        return this.userController.readUser(mobile, SecurityContextHolder.getContext().getAuthentication().getName(),
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping
    public Flux<UserMinimumDto> readAll() {
        return this.userController.readAll()
                .doOnEach(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PutMapping(value = MOBILE_ID)
    public Mono<UserDto> updateUser(@PathVariable String mobile, @Valid @RequestBody UserDto userDto) {
        return this.userController.updateUser(mobile, userDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PatchMapping(value = "/password" + MOBILE_ID)
    public Mono<UserDto> changePassword(@PathVariable String mobile, @Valid @RequestBody UserCredentialDto userCredentialDto) {
        return this.userController.changePassword(mobile, userCredentialDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @PatchMapping(value = MOBILE_ID)
    public Mono<UserDto> updateRoles(@PathVariable String mobile, @Valid @RequestBody UserMinimumDto userMinimumDto) {
        return this.userController.updateRoles(mobile, userMinimumDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

    @GetMapping(value = SEARCH)
    public Flux<UserDto> findByMobileOrUsernameOrDniOrAddress(@RequestParam(required = false) String mobile,
                                                              @RequestParam(required = false) String username,
                                                              @RequestParam(required = false) String dni,
                                                              @RequestParam(required = false) String address) {

        UserDto userDto = new UserDto();
        userDto.setMobile(mobile);
        userDto.setUsername(username);
        userDto.setDni(dni);
        userDto.setAddress(address);
        return this.userController.findByMobileOrUsernameOrDniOrAddress(userDto)
                .doOnNext(log -> LogManager.getLogger(this.getClass()).debug(log));
    }

}
