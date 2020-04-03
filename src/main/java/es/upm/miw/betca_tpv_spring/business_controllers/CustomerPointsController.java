package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.documents.CustomerPoints;
import es.upm.miw.betca_tpv_spring.documents.User;
import es.upm.miw.betca_tpv_spring.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_spring.repositories.CustomerPointsReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.UserReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class CustomerPointsController {

    private CustomerPointsReactRepository customerPointsReactRepository;
    private UserReactRepository userReactRepository;

    @Autowired
    public CustomerPointsController(CustomerPointsReactRepository customerPointsReactRepository, UserReactRepository userReactRepository) {
        this.customerPointsReactRepository = customerPointsReactRepository;
        this.userReactRepository = userReactRepository;
    }

    public Mono<Integer> getCustomerPointsByUserMobile(String mobile) {
        Mono<User> user = this.findUserByMobile(mobile);
        return this.customerPointsReactRepository.findByUser(user)
                .switchIfEmpty(Mono.error(new NotFoundException("Customer points by user mobile:" + mobile)))
                .map(CustomerPoints::getPoints);
    }

    public Mono<Void> setCustomerPointsByUserMobile(String mobile, Integer points) {
        Mono<User> user = this.findUserByMobile(mobile);
        Mono<CustomerPoints> customerPointsMono = this.customerPointsReactRepository.findByUser(user)
                .switchIfEmpty(Mono.error(new NotFoundException("Customer points by user mobile:" + mobile)))
                .map(doc -> {
                    doc.setPoints(points);
                    return doc;
                });
        return this.customerPointsReactRepository.saveAll(customerPointsMono).then();
    }

    public Mono<String> createCustomerPointsByExistingUserMobile(String mobile) {
        CustomerPoints customerPoints = new CustomerPoints();
        Mono<User> user = this.findUserByMobile(mobile).doOnNext(userDoc -> {
            customerPoints.setPoints(0);
            customerPoints.setUser(userDoc);
        });
        return Mono.when(user).then(this.customerPointsReactRepository.save(customerPoints)).map(newCustomerPoints -> newCustomerPoints.getUser().getMobile());
    }

    private Mono<User> findUserByMobile(String mobile) {
        return this.userReactRepository.findByMobile(mobile)
                .switchIfEmpty(Mono.error(new NotFoundException("User mobile:" + mobile)));
    }
}
