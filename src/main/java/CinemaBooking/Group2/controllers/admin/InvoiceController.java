package CinemaBooking.Group2.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.invoice.InvoiceListResponse;
import CinemaBooking.Group2.dtos.invoice.InvoiceResponse;
import CinemaBooking.Group2.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/invoices")
@Tag(name = "Admin - Invoice", description = "Quản lý hoá đơn")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * GET /api/admin/invoices
     * Lấy danh sách hoá đơn có lọc & phân trang
     */
    @GetMapping
    @Operation(
        summary = "Danh sách hoá đơn",
        description = "Lấy danh sách hoá đơn với các bộ lọc: từ khoá (mã booking / tên / email), trạng thái thanh toán, khoảng ngày tạo, rạp chiếu."
    )
    public ResponseEntity<PageResponse<InvoiceListResponse>> listInvoices(
            @Parameter(description = "Tìm theo mã booking, tên hoặc email khách hàng")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Trạng thái: PAID | PENDING | FAILED | CANCELLED")
            @RequestParam(required = false) String status,

            @Parameter(description = "Từ ngày (yyyy-MM-dd)")
            @RequestParam(required = false) String startDate,

            @Parameter(description = "Đến ngày (yyyy-MM-dd)")
            @RequestParam(required = false) String endDate,

            @Parameter(description = "ID rạp chiếu (tuỳ chọn)")
            @RequestParam(required = false) Integer cinemaId,

            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "10") int perPage
    ) {
        PageResponse<InvoiceListResponse> result =
                invoiceService.searchInvoices(keyword, status, startDate, endDate, cinemaId, page, perPage);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/admin/invoices/{bookingCode}
     * Lấy chi tiết hoá đơn theo mã booking
     */
    @GetMapping("/{bookingCode}")
    @Operation(
        summary = "Chi tiết hoá đơn",
        description = "Lấy chi tiết đầy đủ của hoá đơn: thông tin khách hàng, phim, suất chiếu, danh sách ghế, combo và tổng tiền."
    )
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceDetail(
            @Parameter(description = "Mã booking, VD: BK00000001")
            @PathVariable String bookingCode
    ) {
        InvoiceResponse invoice = invoiceService.getInvoiceByBookingCode(bookingCode);
        return ResponseEntity.ok(new ApiResponse<>("Success", invoice));
    }
}
