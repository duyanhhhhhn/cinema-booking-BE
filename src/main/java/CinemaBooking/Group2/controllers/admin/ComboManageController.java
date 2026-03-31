package CinemaBooking.Group2.controllers.admin;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.concession.CnPResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboListResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboRequestDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.dtos.concession.ProductRequestDTO;
import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.service.concessions.ComboService;
import CinemaBooking.Group2.service.concessions.ProductService;

@RestController
@ResponseBody
public class ComboManageController {

    @Autowired
    private ComboService comboService;

    @Autowired
    private ProductService productService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(MultipartFile.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(null);
            }
        });
    }

    @GetMapping("/public/combo")
    @CrossOrigin
    public ResponseEntity<ComboListResponseDTO> getCombo() {
        ComboListResponseDTO list = new ComboListResponseDTO();
        List<ComboResponseDTO> items = comboService.getCombo();

        list.setCombo(items);
        list.setMessage(items.isEmpty() ? "Not found any Combo" : "Success");
        list.setSuccess(!items.isEmpty());

        return items.isEmpty()
            ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(list)
            : ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/api/combos")
    @CrossOrigin
    public ResponseEntity<ApiResponse<List<CnPResponseDTO>>> getAdminCombos(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) Integer perPage,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String filterType
    ) {
        int effectivePerPage = perPage != null ? perPage : (size != null ? size : 10);
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(effectivePerPage, 1);

        List<CnPResponseDTO> allItems = comboService.getAllConcessionsIncludingInactive(filterType);
        int fromIndex = Math.min((safePage - 1) * safeSize, allItems.size());
        int toIndex = Math.min(fromIndex + safeSize, allItems.size());
        List<CnPResponseDTO> pageItems = new ArrayList<>(allItems.subList(fromIndex, toIndex));

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", safePage);
        meta.put("perPage", safeSize);
        meta.put("total", allItems.size());

        return ResponseEntity.ok(new ApiResponse<>("Success", pageItems, meta));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/api/combos/{id}")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> getAdminComboById(@PathVariable("id") int id) {
        ComboResponseDTO item = comboService.comboInfoIncludingInactive(id);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Combo not found", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("Success", item));
    }

    @PutMapping(
        value = "/api/public/combo/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> edit(
        @PathVariable("id") int id,
        @ModelAttribute ComboRequestDTO dto
    ) {
        return updateCombo(id, dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping(
        value = "/api/combos/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> editAdmin(
        @PathVariable("id") int id,
        @ModelAttribute ComboRequestDTO dto
    ) {
        return updateCombo(id, dto);
    }

    private ResponseEntity<ApiResponse<ComboResponseDTO>> updateCombo(int id, ComboRequestDTO dto) {
        ComboResponseDTO updated = comboService.updateCombo(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("Combo updated", updated));
    }

    @PatchMapping("/api/public/combo/{id}/toggle-active")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> toggleComboActive(@PathVariable("id") int id) {
        ComboResponseDTO updated = comboService.toggleComboActive(id);
        return ResponseEntity.ok(new ApiResponse<>("Combo status updated", updated));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PatchMapping("/api/combos/{id}/toggle-active")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> toggleComboActiveAdmin(@PathVariable("id") int id) {
        ComboResponseDTO updated = comboService.toggleComboActive(id);
        return ResponseEntity.ok(new ApiResponse<>("Combo status updated", updated));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/api/combos/{id}/activate")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> activateComboAdmin(@PathVariable("id") int id) {
        ComboResponseDTO updated = comboService.activateCombo(id);
        return ResponseEntity.ok(new ApiResponse<>("Combo activated", updated));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/api/combos/{id}/deactivate")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> deactivateComboAdmin(@PathVariable("id") int id) {
        ComboResponseDTO updated = comboService.deactivateCombo(id);
        return ResponseEntity.ok(new ApiResponse<>("Combo deactivated", updated));
    }

    @PostMapping(
        value = "/api/public/combo/add",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> add(@ModelAttribute ComboRequestDTO dto) {
        return createCombo(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping(
        value = "/api/combos",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ComboResponseDTO>> addAdmin(@ModelAttribute ComboRequestDTO dto) {
        return createCombo(dto);
    }

    private ResponseEntity<ApiResponse<ComboResponseDTO>> createCombo(ComboRequestDTO dto) {
        ComboResponseDTO created = comboService.createCombo(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Combo created", created));
    }

    @PostMapping(
        value = {"/api/product/add", "/api/public/product/add"},
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> addProduct(@ModelAttribute ProductRequestDTO dto) {
        return createProduct(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping(
        value = "/api/products",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> addProductAdmin(@ModelAttribute ProductRequestDTO dto) {
        return createProduct(dto);
    }

    private ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(ProductRequestDTO dto) {
        ProductResponseDTO created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Product created", created));
    }

    @PutMapping(
        value = "/api/public/product/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> editProduct(
        @PathVariable("id") int id,
        @ModelAttribute ProductRequestDTO dto
    ) {
        return updateProduct(id, dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping(
        value = "/api/products/{id}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> editProductAdmin(
        @PathVariable("id") int id,
        @ModelAttribute ProductRequestDTO dto
    ) {
        return updateProduct(id, dto);
    }

    private ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(int id, ProductRequestDTO dto) {
        ProductResponseDTO updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(new ApiResponse<>("Product updated", updated));
    }

    @PatchMapping({
        "/api/public/product/{id}/toggle-active",
        "/api/product/{id}/toggle-active"
    })
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> toggleProductActive(@PathVariable("id") int id) {
        ProductResponseDTO updated = productService.toggleProductActive(id);
        return ResponseEntity.ok(new ApiResponse<>(buildProductToggleMessage(updated), updated));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PatchMapping({
        "/api/products/{id}/toggle-active",
        "/api/product/{id}/admin/toggle-active"
    })
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> toggleProductActiveAdmin(@PathVariable("id") int id) {
        ProductResponseDTO updated = productService.toggleProductActive(id);
        return ResponseEntity.ok(new ApiResponse<>(buildProductToggleMessage(updated), updated));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/api/products/{id}/activate")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> activateProductAdmin(@PathVariable("id") int id) {
        ProductResponseDTO updated = productService.activateProduct(id);
        return ResponseEntity.ok(new ApiResponse<>("Da bat san pham", updated));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PutMapping("/api/products/{id}/deactivate")
    @CrossOrigin
    public ResponseEntity<ApiResponse<ProductResponseDTO>> deactivateProductAdmin(@PathVariable("id") int id) {
        ProductResponseDTO updated = productService.deactivateProduct(id);
        return ResponseEntity.ok(new ApiResponse<>("Da tat san pham", updated));
    }

    private String buildProductToggleMessage(ProductResponseDTO updated) {
        return updated != null && updated.getIsActive() == 1
            ? "Da bat san pham"
            : "Da tat san pham";
    }

    @DeleteMapping("/api/public/combo/{id}")
    @CrossOrigin
    public ResponseEntity<ApiResponse<Combo>> deleteCombo(@PathVariable("id") int id) {
        int rs = comboService.DeleteCombo(id);
        if (rs == 1) {
            return ResponseEntity.ok(new ApiResponse<>("Success", null));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>("Error", null));
    }

    @DeleteMapping("/api/public/product/{id}")
    @CrossOrigin
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable("id") int id) {
        int rs = comboService.DeleteProduct(id);
        if (rs == 1) {
            return ResponseEntity.ok(new ApiResponse<>("Success", null));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse<>("Error", null));
    }
}
