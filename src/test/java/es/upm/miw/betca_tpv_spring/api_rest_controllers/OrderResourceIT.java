package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.data_services.DatabaseSeederService;
import es.upm.miw.betca_tpv_spring.documents.Order;
import es.upm.miw.betca_tpv_spring.documents.OrderLine;
import es.upm.miw.betca_tpv_spring.dtos.OrderCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.OrderDto;
import es.upm.miw.betca_tpv_spring.dtos.OrderLineCreationDto;
import es.upm.miw.betca_tpv_spring.dtos.OrderLineDto;
import es.upm.miw.betca_tpv_spring.repositories.ArticleRepository;
import es.upm.miw.betca_tpv_spring.repositories.OrderRepository;
import es.upm.miw.betca_tpv_spring.repositories.ProviderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.OrderResource.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

@ApiTestConfig
public class OrderResourceIT {

    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private LinkedList<Order> ordersList;

    @BeforeEach
    void seedDatabase() {
        ordersList = new LinkedList<>();
        OrderLine[] orderLines = {
                new OrderLine(this.articleRepository.findAll().get(0), 10),
                new OrderLine(this.articleRepository.findAll().get(1), 8),
                new OrderLine(this.articleRepository.findAll().get(2), 6),
                new OrderLine(this.articleRepository.findAll().get(3), 4),
        };

        Stream.iterate(0, i -> i + 1).limit(5)
                .map(i -> new Order("order" + i, this.providerRepository.findAll().get(1), orderLines))
                .forEach(ordersList::add);

        this.orderRepository.saveAll(ordersList);
    }

    @Test
    void testSearchOrderWithOnlyDescriptionField() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ORDERS)
                .queryParam("description", "order")
                .queryParam("provider", "null")
                .queryParam("closingDate", "null")
                .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSearchOrderWithOnlyProviderField() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ORDERS)
                .queryParam("description", "order")
                .queryParam("provider", this.providerRepository.findAll().get(1).getId())
                .queryParam("closingDate", "null")
                .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSearchOrderWithOnlyClosingDateNotNullField() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ORDERS)
                .queryParam("description", "null")
                .queryParam("provider", "null")
                .queryParam("closingDate", LocalDateTime.now().toString())
                .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSearchOrderWithAllFields() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ORDERS)
                .queryParam("description", "order")
                .queryParam("provider", this.providerRepository.findAll().get(1).getId())
                .queryParam("closingDate", LocalDateTime.now().toString())
                .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testSearchOrderWithAllFieldsExceptClosingDate() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(uriBuilder -> uriBuilder
                .path(contextPath + ORDERS)
                .queryParam("description", "order")
                .queryParam("provider", this.providerRepository.findAll().get(1).getId())
                .queryParam("closingDate", null)
                .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateOrder() {
        OrderLineCreationDto[] orderLines = {
                new OrderLineCreationDto(this.articleRepository.findAll().get(0).getCode(), 10),
                new OrderLineCreationDto(this.articleRepository.findAll().get(1).getCode(), 8),
                new OrderLineCreationDto(this.articleRepository.findAll().get(2).getCode(), 6),
                new OrderLineCreationDto(this.articleRepository.findAll().get(3).getCode(), 4),
        };

        OrderDto orderDto = this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + ORDERS)
                .body(BodyInserters.fromObject(
                        new OrderCreationDto("orderPruebaas", this.providerRepository.findAll().get(1).getId(), orderLines)
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .returnResult().getResponseBody();

        assertNotNull(orderDto.getOpeningDate());
        assertEquals(4, orderDto.getOrderLines().length);
        assertEquals(this.providerRepository.findAll().get(1).getId(), orderDto.getProvider());
        assertEquals("orderPruebaas", orderDto.getDescription());
    }

    @Test
    void testCreateOrderWithNullProvider() {
        OrderLineCreationDto[] orderLines = {
                new OrderLineCreationDto(this.articleRepository.findAll().get(0).getCode(), 10),
                new OrderLineCreationDto(this.articleRepository.findAll().get(1).getCode(), 8),
                new OrderLineCreationDto(this.articleRepository.findAll().get(2).getCode(), 6),
                new OrderLineCreationDto(this.articleRepository.findAll().get(3).getCode(), 4),
        };

        this.restService.loginAdmin(webTestClient)
                .post().uri(contextPath + ORDERS)
                .body(BodyInserters.fromObject(
                        new OrderCreationDto("orderPruebaas", null, orderLines)
                ))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testDeleteOrderWithCorrectId() {
        this.restService.loginAdmin(webTestClient)
                .delete().uri(contextPath + ORDERS + ORDER_ID, this.ordersList.get(2).getId())
                .exchange()
                .expectStatus().isOk();
        assertEquals(Optional.empty(), this.orderRepository.findById(this.ordersList.get(2).getId()));
    }

    @Test
    void testUpdateOrder() {
        OrderLineDto[] orderLines = {
                new OrderLineDto(this.articleRepository.findAll().get(1).getCode(), 8),
                new OrderLineDto(this.articleRepository.findAll().get(2).getCode(), 6),
        };
        OrderDto orderDto = this.restService.loginAdmin(webTestClient)
                .put().uri(contextPath + ORDERS + ORDER_ID, this.ordersList.get(1).getId())
                .body(BodyInserters.fromObject(
                        new OrderDto("cambiado", this.providerRepository.findAll().get(1).getId(), LocalDateTime.now(), orderLines)
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .returnResult().getResponseBody();

        Order order = this.orderRepository.findById(this.ordersList.get(1).getId()).get();
        assertEquals(order.getId(), orderDto.getId());
        assertEquals(order.getDescription(), orderDto.getDescription());
        assertEquals(order.getOrderLines().length, orderDto.getOrderLines().length);
    }

    @Test
    void testUpdateOrderWithWrongArticle() {
        OrderLineDto[] orderLinesWithWrongArticleCode = {
                new OrderLineDto(this.articleRepository.findAll().get(0).getCode(), 10),
                new OrderLineDto("WrongCode", 8),
                new OrderLineDto(this.articleRepository.findAll().get(2).getCode(), 6),
        };
        OrderDto orderDtoToUpdate = new OrderDto("orderCloseWrong", this.providerRepository.findAll().get(0).getId(), orderLinesWithWrongArticleCode);

        this.restService.loginAdmin(webTestClient)
                .put().uri(contextPath + ORDERS + ORDER_ID, this.ordersList.get(1).getId())
                .body(BodyInserters.fromObject(
                        orderDtoToUpdate
                ))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetOrder() {
        OrderDto order = this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + ORDERS + ORDER_ID, this.ordersList.get(1).getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .returnResult().getResponseBody();
        assertEquals(this.orderRepository.findById(this.ordersList.get(1).getId()).get().getId(), order.getId());
    }

    @Test
    void testGetOrderNotFound() {
        this.restService.loginAdmin(webTestClient)
                .get().uri(contextPath + ORDERS + ORDER_ID, "idIncorrecto")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCloseOrder() {
        OrderDto orderDtoClosed = new OrderDto(this.ordersList.get(0));
        for (OrderLineDto orderLineDto : orderDtoClosed.getOrderLines()) {
            orderLineDto.setFinalAmount(5);
        }

        OrderDto orderDto = this.restService.loginAdmin(webTestClient)
                .put().uri(contextPath + ORDERS + ORDER_CLOSE + ORDER_ID, this.ordersList.get(0).getId())
                .body(BodyInserters.fromObject(
                        orderDtoClosed
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .returnResult().getResponseBody();

        assertNotNull(orderDto.getClosingDate());
        assertEquals(5, orderDto.getOrderLines()[0].getFinalAmount().intValue());
        assertEquals(5, orderDto.getOrderLines()[1].getFinalAmount().intValue());
        assertEquals(5, orderDto.getOrderLines()[2].getFinalAmount().intValue());
        //assertEquals(5, orderDto.getOrderLines()[3].getFinalAmount().intValue());
    }

    @Test
    void testCloseOrderArticleNotFound() {
        OrderLineDto[] orderLinesWithWrongArticleCode = {
                new OrderLineDto(this.articleRepository.findAll().get(0).getCode(), 10),
                new OrderLineDto("WrongCode", 8),
                new OrderLineDto(this.articleRepository.findAll().get(2).getCode(), 6),
        };
        OrderDto orderDtoClosed = new OrderDto("orderCloseWrong", this.providerRepository.findAll().get(0).getId(), orderLinesWithWrongArticleCode);

        this.restService.loginAdmin(webTestClient)
                .put().uri(contextPath + ORDERS + ORDER_CLOSE + ORDER_ID, this.ordersList.get(0).getId())
                .body(BodyInserters.fromObject(
                        orderDtoClosed
                ))
                .exchange()
                .expectStatus().isNotFound();
    }
}
