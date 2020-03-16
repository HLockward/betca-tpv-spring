package es.upm.miw.betca_tpv_spring.api_rest_controllers;

import es.upm.miw.betca_tpv_spring.documents.Staff;
import es.upm.miw.betca_tpv_spring.dtos.StaffDto;
import es.upm.miw.betca_tpv_spring.repositories.StaffReactRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static es.upm.miw.betca_tpv_spring.api_rest_controllers.StaffResource.STAFFS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ApiTestConfig
public class StaffResourceIT {
    @Autowired
    private RestService restService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private StaffReactRepository staffReactRepository;
    private LinkedList<Staff> listStaff;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @BeforeEach
    void seedDatabase() {
        listStaff = new LinkedList<>();
        listStaff.add( new Staff(
                        "6662",
                        "2020",
                        "3",
                        "13",
                        4.00f,
                        LocalDateTime.of(2020,3,13,9,0,0)
                    ));
        listStaff.add( new Staff(
                "6669",
                "2020",
                "3",
                "14",
                4.00f,
                LocalDateTime.of(2020,3,14,9,0,0)
        ));
        this.staffReactRepository.saveAll(listStaff);

        System.out.println(listStaff.toString());
    }


    @Test
    void createStaffRecordTest(){
        this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + STAFFS)
                .body(BodyInserters.fromObject(listStaff.get(1)))
                .exchange()
                .expectStatus().isOk();

        System.out.println(this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", null)
                        .queryParam("year", null)
                        .queryParam("month", null)
                        .queryParam("day", null)
                        .build()
                )
                .exchange()
                .expectStatus().isOk().expectBodyList(StaffDto.class)
                .returnResult().getResponseBody().toString()
        );

    }


    @Test
    void createStaffRecordTest2(){
        this.restService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + STAFFS)
                .body(BodyInserters.fromObject(new Staff("6669",
                        "2020",
                        "3",
                        "14",
                        4.00f,
                        LocalDateTime.of(2020,3,14,9,0,0))))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testReadAll(){
        this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", "")
                        .queryParam("year", "")
                        .queryParam("month", "")
                        .queryParam("day", "")
                        .build()
                )
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testReadAll2(){
        List<StaffDto> newStaffList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", null)
                        .queryParam("year", null)
                        .queryParam("month", null)
                        .queryParam("day", null)
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffDto.class)
                .returnResult().getResponseBody();
        System.out.println(newStaffList.toString());
    }

    @Test
    void testReadAll3(){
        List<StaffDto> newStaffList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + STAFFS + '?'
                        + "mobile=" + null + "&year=" + null + "&month=" + null + "&day=" + null
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffDto.class)
                .returnResult().getResponseBody();
        System.out.println(newStaffList.toString());
    }






    @Test
    void testReadByMobileMonthDay1(){
        List<StaffDto> newStaffList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(contextPath + STAFFS + '?'
                        + "mobile=" + "" + "&year=" + "2020" + "&month=" + "" + "&day=" + null
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffDto.class)
                .returnResult().getResponseBody();
        System.out.println(newStaffList.toString());

    }



    @Test
    void testReadByMobileMonthDay(){
        List<StaffDto> newStaffList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", listStaff.get(0).getMobile())
                        .queryParam("year", listStaff.get(0).getYear())
                        .queryParam("month", listStaff.get(0).getMonth())
                        .queryParam("day", listStaff.get(0).getDay())
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffDto.class)
                .returnResult().getResponseBody();

        System.out.println(newStaffList.toString());

        assertNotNull(newStaffList);
        assertEquals(listStaff.get(0).getMobile(), newStaffList.get(0).getMobile());
    }

    @Test
    void testReadByMobileMonthDay2(){
        List<StaffDto> newStaffList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", "")
                        .queryParam("year", "")
                        .queryParam("month", "3")
                        .queryParam("day", "")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffDto.class)
                .returnResult().getResponseBody();

        System.out.println(newStaffList.toString());

        assertNotNull(newStaffList);
        assertEquals("3", newStaffList.get(0).getMonth());
    }


    @Test
    void testUpdate(){
        System.out.println(this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", null)
                        .queryParam("year", null)
                        .queryParam("month", null)
                        .queryParam("day", null)
                        .build()
                )
                .exchange()
                .expectStatus().isOk().expectBodyList(StaffDto.class)
                .returnResult().getResponseBody().toString()
        );

        List<StaffDto> newStaffList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", "6661")
                        .queryParam("year", "2020")
                        .queryParam("month", "3")
                        .queryParam("day", "13")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StaffDto.class)
                .returnResult().getResponseBody();

        this.restService.loginAdmin(this.webTestClient)
                .put()
                .uri(contextPath + STAFFS  + "/" + newStaffList.get(0).getId())
                .body(BodyInserters.fromObject(
                new Staff(
                        "6661",
                        "2020",
                        "3",
                        "13",
                        100.00f,
                        LocalDateTime.of(2020,3,13,9,0,0)
                )))
                .exchange()
                .expectStatus().isOk();


        List<StaffDto> lastStaffList = this.restService.loginAdmin(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + STAFFS)
                        .queryParam("mobile", "6661")
                        .queryParam("year", "2020")
                        .queryParam("month", "3")
                        .queryParam("day", "13")
                        .build()
                )
                .exchange()
                .expectStatus().isOk().expectBodyList(StaffDto.class)
                .returnResult().getResponseBody();


        assertNotNull(newStaffList);
        assertEquals(LocalDateTime.of(2020,3,13,9,0,0), lastStaffList.get(0).getLastLoginTime());
    }

}
