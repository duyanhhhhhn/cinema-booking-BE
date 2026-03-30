package CinemaBooking.Group2.service.concessions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.ProductRequestDTO;
import CinemaBooking.Group2.dtos.concession.ProductResponseDTO;
import CinemaBooking.Group2.mappers.ProductMapper;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.repositories.concessions.ProductRepository;
import CinemaBooking.Group2.ultis.FileUltility;

@Service
public class ProductService {

    private static final String DEFAULT_IMAGE_DATA_URI =
        "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponseDTO> getProducts() {
        return productRepository.getAll().stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getProducts(int page, int size) {
        return productRepository.getAll(page, size).stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public ProductResponseDTO productInfo(int id) {
        Product item = productRepository.findById(id);
        return toResponseDto(item);
    }

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        validateForCreate(dto);

        Product product = new Product();
        product.setName(normalizeCreateText(dto.getName()));
        product.setDescription(normalizeCreateText(dto.getDescription()));
        product.setPrice(validatePrice(dto.getPrice(), "Product price is required"));
        product.setStock(normalizeStock(dto.getStock()));
        product.setCreatedAt(LocalDateTime.now());
        product.setIsActive(1);

        if (dto.getBannerFile() != null && !dto.getBannerFile().isEmpty()) {
            product.setImageUrl(FileUltility.uploadFileImage(
                dto.getBannerFile(),
                "uploads/concessions/product",
                "concessions/product"
            ));
        }

        int newId = productRepository.createReturningId(product);
        if (newId <= 0) {
            throw new RuntimeException("Create product failed");
        }

        product.setId(newId);
        return toResponseDto(product);
    }

    public ProductResponseDTO updateProduct(int id, ProductRequestDTO dto) {
        if (id <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        if (dto == null) {
            throw new IllegalArgumentException("Product payload is required");
        }

        Product existing = productRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (hasText(dto.getName())) {
            existing.setName(dto.getName().trim());
        }

        if (hasText(dto.getDescription())) {
            existing.setDescription(dto.getDescription().trim());
        }

        if (dto.getPrice() != null) {
            existing.setPrice(validatePrice(dto.getPrice(), "Product price is invalid"));
        }

        if (dto.getStock() != null) {
            existing.setStock(normalizeStock(dto.getStock()));
        }

        if (dto.getBannerFile() != null && !dto.getBannerFile().isEmpty()) {
            existing.setImageUrl(FileUltility.uploadFileImage(
                dto.getBannerFile(),
                "uploads/concessions/product",
                "concessions/product"
            ));
        }

        if (existing.getIsActive() == 0) {
            existing.setIsActive(1);
        }

        int rs = productRepository.update(existing);
        if (rs != 1) {
            throw new RuntimeException("Update product failed");
        }

        return toResponseDto(productRepository.findById(id));
    }

    public String editProduct(Product item) {
        if (item == null || item.getId() <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (item.getPrice() == null || item.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Product price is invalid");
        }

        if (item.getStock() < 0) {
            throw new IllegalArgumentException("Product stock is invalid");
        }

        item.setName(item.getName().trim());
        item.setDescription(normalizeText(item.getDescription()));
        if (item.getIsActive() == 0) {
            item.setIsActive(1);
        }

        int rs = productRepository.update(item);
        return rs == 1 ? "Success" : "Error";
    }

    public String createProduct(Product item) {
        if (item == null) {
            throw new IllegalArgumentException("Product is required");
        }

        if (item.getCreatedAt() == null) {
            item.setCreatedAt(LocalDateTime.now());
        }

        if (item.getIsActive() == 0) {
            item.setIsActive(1);
        }

        int newId = productRepository.createReturningId(item);
        if (newId <= 0) {
            return "Failed to create a new product";
        }

        item.setId(newId);
        return "Success";
    }

    public long countActiveProducts() {
        return productRepository.countActive();
    }

    public ProductResponseDTO toggleProductActive(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        Product existing = productRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Product not found");
        }

        int nextActive = existing.getIsActive() == 1 ? 0 : 1;
        int rs = productRepository.changeActive(id, nextActive);
        if (rs != 1) {
            throw new RuntimeException("Update product active state failed");
        }

        return toResponseDto(productRepository.findById(id));
    }

    public ProductResponseDTO activateProduct(int id) {
        return updateProductActiveState(id, 1);
    }

    public ProductResponseDTO deactivateProduct(int id) {
        return updateProductActiveState(id, 0);
    }

    private ProductResponseDTO toResponseDto(Product item) {
        ProductResponseDTO dto = ProductMapper.toResponseDTO(item);
        if (dto != null) {
            dto.setImageUrl(formatImageUrl(dto.getImageUrl()));
        }
        return dto;
    }

    private ProductResponseDTO updateProductActiveState(int id, int active) {
        if (id <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        Product existing = productRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (existing.getIsActive() == active) {
            return toResponseDto(existing);
        }

        int rs = productRepository.changeActive(id, active);
        if (rs != 1) {
            throw new RuntimeException("Update product active state failed");
        }

        return toResponseDto(productRepository.findById(id));
    }

    private void validateForCreate(ProductRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Product payload is required");
        }

        validatePrice(dto.getPrice(), "Product price is required");

        if (dto.getStock() != null && dto.getStock() < 0) {
            throw new IllegalArgumentException("Product stock is invalid");
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeCreateText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private int normalizeStock(Integer stock) {
        if (stock == null) {
            return 0;
        }

        if (stock < 0) {
            throw new IllegalArgumentException("Product stock is invalid");
        }

        return stock;
    }

    private BigDecimal validatePrice(BigDecimal value, String message) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String formatImageUrl(String imageUrl) {
        if (isMissingImage(imageUrl)) {
            return DEFAULT_IMAGE_DATA_URI;
        }

        if (imageUrl.startsWith("data:")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/media/")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/")) {
            return "/media" + imageUrl;
        }

        return "/media/" + imageUrl;
    }

    private boolean isMissingImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return true;
        }

        String normalized = imageUrl.trim();
        return "no_img.jpg".equalsIgnoreCase(normalized)
            || "/media/no_img.jpg".equalsIgnoreCase(normalized)
            || "media/no_img.jpg".equalsIgnoreCase(normalized);
    }
}
