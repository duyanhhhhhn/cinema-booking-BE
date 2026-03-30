package CinemaBooking.Group2.controllers.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.concession.CnPResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.service.concessions.ComboService;
import CinemaBooking.Group2.service.concessions.ProductService;

@RestController
@RequestMapping("/api")
public class ComboController {

    @Autowired
    private ComboService comboService;

    @Autowired
    private ProductService productService;

    @GetMapping({"/concessions", "/public/combo"})
    @CrossOrigin
    public ResponseEntity<ApiResponse<List<CnPResponseDTO>>> getConcessions(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) Integer perPage,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String filterType
    ) {
        int effectivePerPage = perPage != null ? perPage : (size != null ? size : 10);
        page = Math.max(page, 1);
        effectivePerPage = Math.max(effectivePerPage, 1);

        List<CnPResponseDTO> items = comboService.getCombo(page, effectivePerPage, filterType);
        int totalItem = comboService.countActiveItems(filterType);

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", page);
        meta.put("perPage", effectivePerPage);
        meta.put("total", totalItem);

        return ResponseEntity.ok(new ApiResponse<>("Success", items, meta));
    }

    @GetMapping("/public/combo/{id}")
    @CrossOrigin
    public ResponseEntity<ComboResponseDTO> comboInfo(@Validated @PathVariable("id") int id) {
        ComboResponseDTO item = comboService.comboInfo(id);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ComboResponseDTO("We don't have any combo with this id", false));
        }

        return ResponseEntity.ok(item);
    }

    @GetMapping("/products")
    @CrossOrigin
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProduct() {
        List<ProductResponseDTO> items = productService.getProducts();
        String message = items.isEmpty() ? "No products found" : "Success";
        return ResponseEntity.ok(new ApiResponse<>(message, items));
    }

    @GetMapping("/products/{id}")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> productInfo(@PathVariable("id") int id) {
        ProductResponseDTO item = productService.productInfo(id);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Product not found", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("Success", item));
    }
}
