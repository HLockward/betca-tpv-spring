package es.upm.miw.betca_tpv_spring.business_controllers;

import es.upm.miw.betca_tpv_spring.business_services.PdfService;
import es.upm.miw.betca_tpv_spring.documents.GiftTicket;
import es.upm.miw.betca_tpv_spring.documents.Ticket;
import es.upm.miw.betca_tpv_spring.dtos.GiftTicketCreationDto;
import es.upm.miw.betca_tpv_spring.repositories.GiftTicketReactRepository;
import es.upm.miw.betca_tpv_spring.repositories.TicketReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Controller
public class GiftTicketController {

    private GiftTicketReactRepository giftTicketReactRepository;
    private TicketReactRepository ticketReactRepository;
    private PdfService pdfService;

    @Autowired
    public GiftTicketController(GiftTicketReactRepository giftTicketReactRepository, TicketReactRepository ticketReactRepository, PdfService pdfService) {
        this.giftTicketReactRepository = giftTicketReactRepository;
        this.ticketReactRepository = ticketReactRepository;
        this.pdfService = pdfService;
    }

    private Mono<GiftTicket> createGiftTicket(GiftTicketCreationDto giftTicketCreationDto) {

        GiftTicket giftTicket = new GiftTicket(giftTicketCreationDto.getPersonalizedMessage(), null);

        Mono<Ticket> ticket = this.ticketReactRepository.findFirstByOrderByCreationDateDescIdDesc()
                .doOnNext(giftTicket::setTicket);

        return Mono.when(ticket)
                .then(this.giftTicketReactRepository.save(giftTicket));
    }

    @Transactional
    public Mono<byte[]> createGiftTicketAndPdf(GiftTicketCreationDto giftTicketCreationDto) {
        return pdfService.generateGiftTicket(this.createGiftTicket(giftTicketCreationDto));
    }

}
