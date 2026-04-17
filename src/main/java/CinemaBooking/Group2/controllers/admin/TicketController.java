package CinemaBooking.Group2.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.ticket.PrintTicketRequest;
import CinemaBooking.Group2.dtos.ticket.PrintTicketResponse;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.service.StaffScheduleService;
import CinemaBooking.Group2.service.TicketService;
import CinemaBooking.Group2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Ticket Management", description = "APIs for ticket printing and management at counter")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;
    @Autowired
    private StaffScheduleService staffScheduleService;

    @PostMapping("/print")
    @Operation(summary = "In vé tại quầy", description = "API dành cho nhân viên quầy để in vé cho khách hàng. " +
            "Chỉ có thể in vé cho booking đã thanh toán (PAID). " +
            "Nếu không truyền danh sách ticketCodes, sẽ in tất cả vé chưa in của booking.")
    public ResponseEntity<ApiResponse<PrintTicketResponse>> printTickets(
            @RequestBody PrintTicketRequest request) {
        User actor = userService.getCurrentAuthenticatedUser();
        staffScheduleService.assertCanAccessTicketSelling(actor);
        PrintTicketResponse response = ticketService.printTickets(request, actor.getId());
        return ResponseEntity.ok(new ApiResponse<>("Tickets printed successfully", response));
    }
}
