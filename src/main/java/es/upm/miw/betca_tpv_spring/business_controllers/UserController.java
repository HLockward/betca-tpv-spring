package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.JwtService;
import es.upm.miw.betca_tpv_spring.documents.Messages;
import es.upm.miw.betca_tpv_spring.documents.Role;
import es.upm.miw.betca_tpv_spring.documents.User;
import es.upm.miw.betca_tpv_spring.dtos.*;
import es.upm.miw.betca_tpv_spring.exceptions.ConflictException;
import es.upm.miw.betca_tpv_spring.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.UserReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private UserReactRepository userReactRepository;
    private JwtService jwtService;

    @Autowired
    public UserController(UserReactRepository userReactRepository, JwtService jwtService) {
        this.userReactRepository = userReactRepository;
        this.jwtService = jwtService;
    }

    public Mono<TokenOutputDto> login(String mobile) {
        return this.userReactRepository.findByMobile(mobile).map(
                user -> {
                    String[] roles = Arrays.stream(user.getRoles()).map(Role::name).toArray(String[]::new);
                    return new TokenOutputDto(jwtService.createToken(user.getMobile(), user.getUsername(), roles));
                }
        );
    }

    private Mono<User> readAndValidate(String mobile, String claimMobile, List<String> claimRoles) {
        return this.userReactRepository.findByMobile(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile:" + mobile)))
                .handle((user, sink) -> {
                    if (!this.isAuthorized(claimMobile, claimRoles, mobile, Arrays.stream(user.getRoles())
                            .map(Role::roleName).collect(Collectors.toList()))) {
                        sink.error(new ForbiddenException("User mobile (" + mobile + ")"));
                    } else {
                        sink.next(user);
                    }
                });
    }

    private boolean isAuthorized(String claimMobile, List<String> claimRoles, String userMobile, List<String> userRoles) {
        if (claimRoles.contains(Role.ADMIN.roleName()) || claimMobile.equals(userMobile)) {
            return true;
        }
        if (claimRoles.contains(Role.MANAGER.roleName())
                && !userRoles.contains(Role.ADMIN.roleName()) && !userRoles.contains(Role.MANAGER.roleName())) {
            return true;
        }
        return claimRoles.contains(Role.OPERATOR.roleName())
                && userRoles.stream().allMatch(role -> role.equals(Role.CUSTOMER.roleName()));
    }

    private Mono<Void> noExistByMobile(String mobile) {
        return this.userReactRepository.findByMobile(mobile)
                .handle((document, sink) -> sink.error(new ConflictException("The mobile already exists")));
    }

    public Mono<UserDto> createUser(UserDto userDto) {
        Mono<Void> noExistByMobile = this.noExistByMobile(userDto.getMobile());
        User user = User.builder().mobile(userDto.getMobile()).username(userDto.getUsername()).email(userDto.getEmail()).dni(userDto.getDni()).address(userDto.getAddress()).build();
        return Mono.when(noExistByMobile).then(this.userReactRepository.save(user)).map(UserDto::new);
    }

    public Mono<UserDto> readUser(String mobile, String claimMobile, List<String> claimRoles) {
        return this.readAndValidate(mobile, claimMobile, claimRoles)
                .map(UserDto::new);
    }

    public Flux<UserMinimumDto> readAll() {
        return this.userReactRepository.findAllUsers();
    }

    public Mono<UserDto> updateUser(String mobile, UserDto userDto) {
        Mono<User> user = this.userReactRepository.findByMobile(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile:" + mobile)))
                .map(user1 -> {
                    user1.setMobile(userDto.getMobile());
                    user1.setUsername(userDto.getUsername());
                    user1.setEmail(userDto.getEmail());
                    user1.setDni(userDto.getDni());
                    user1.setAddress(userDto.getAddress());
                    user1.setActive(userDto.isActive());
                    return user1;
                });
        Mono<Void> noExistByMobile;
        if (mobile.equals(userDto.getMobile())) {
            noExistByMobile = Mono.empty();
        } else {
            noExistByMobile = this.noExistByMobile(userDto.getMobile());
        }
        return Mono.when(user, noExistByMobile).then(this.userReactRepository.saveAll(user).next()).map(UserDto::new);
    }


    public Mono<MessagesDto> sendMessageToUser(MessagesDto messagesDto) {
        Mono<User> userFrom = this.userReactRepository.findByMobile(messagesDto.getFromUserMobile())
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile:" + messagesDto.getFromUserMobile())));
        Mono<User> userTo = this.userReactRepository.findByMobile(messagesDto.getToUserMobile())
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile:" + messagesDto.getToUserMobile())))
                .map(user1 -> {
                    user1.getMessagesList().add(new Messages(
                            messagesDto.getFromUserMobile(),
                            messagesDto.getToUserMobile(),
                            messagesDto.getMessageContent(),
                            messagesDto.getSentDate(),
                            null
                    ));
                    return user1;
                });
        return Mono.when(userFrom, userTo).then(this.userReactRepository.saveAll(userTo).next())
                .map(user1-> new MessagesDto(user1.getMessagesList().get(user1.getMessagesList().size()-1)));
    }

    public Mono<UserDto> changePassword(String mobile, UserCredentialDto userCredentialDto) {
        Mono<User> user = this.userReactRepository.findByMobile(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile:" + mobile)))
                .map(user1 -> {
                    user1.setPassword(userCredentialDto.getNewPassword());
                    return user1;
                });
        return Mono.when(user).then(this.userReactRepository.saveAll(user).next()).map(UserDto::new);
    }

    public Mono<UserDto> updateRoles(String mobile, UserMinimumDto userMinimumDto) {
        Mono<User> user = this.userReactRepository.findByMobile(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile:" + mobile)))
                .map(user1 -> {
                    user1.setRoles(userMinimumDto.getRoles());
                    return user1;
                });
        return Mono.when(user).then(this.userReactRepository.saveAll(user).next()).map(UserDto::new);
    }
}
