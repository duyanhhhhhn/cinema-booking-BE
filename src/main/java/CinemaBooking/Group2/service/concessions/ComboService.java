package CinemaBooking.Group2.service.concessions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import CinemaBooking.Group2.dtos.concession.CnPResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboCRUDResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboItemResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboRequestDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.Enum.ResponseStatus;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.repositories.concessions.ComboItemRepository;
import CinemaBooking.Group2.repositories.concessions.ComboRepository;
import CinemaBooking.Group2.repositories.concessions.ProductRepository;
import CinemaBooking.Group2.ultis.FileUltility;

@Service
public class ComboService {

    private static final String DEFAULT_IMAGE_DATA_URI =
        "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";

    private final UserRepository userRepository;
    private final ComboRepository comboRepository;
    private final ComboItemRepository comboItemRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ComboService(
        UserRepository userRepository,
        ComboRepository comboRepository,
        ComboItemRepository comboItemRepository,
        ProductRepository productRepository
    ) {
        this.userRepository = userRepository;
        this.comboRepository = comboRepository;
        this.comboItemRepository = comboItemRepository;
        this.productRepository = productRepository;
    }

    public List<ComboResponseDTO> getCombo() {
        return comboRepository.findAllActive().stream()
            .map(combo -> toComboResponse(combo, true))
            .collect(Collectors.toList());
    }

    public List<ComboResponseDTO> getAllCombosIncludingInactive() {
        return comboRepository.findAllIncludingInactive().stream()
            .map(combo -> toComboResponse(combo, true))
            .collect(Collectors.toList());
    }

    public List<CnPResponseDTO> getAllConcessionsIncludingInactive(String filterType) {
        String normalized = normalizeFilterType(filterType);

        List<Product> products = productRepository.getAll();
        Map<Integer, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, product -> product, (left, right) -> left, HashMap::new));

        List<CnPResponseDTO> list = new ArrayList<>();

        if (!"SINGLE".equals(normalized) && !"PRODUCT".equals(normalized)) {
            List<Combo> combos = comboRepository.findAllIncludingInactive();
            for (Combo combo : combos) {
                list.add(toConcessionResponse(combo, productMap));
            }
        }

        if (!"COMBO".equals(normalized)) {
            for (Product product : products) {
                list.add(toConcessionResponse(product));
            }
        }

        list.sort(Comparator
            .comparing(CnPResponseDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(CnPResponseDTO::getId, Comparator.reverseOrder()));

        return list;
    }

    public List<CnPResponseDTO> getCombo(int page, int size) {
        return getCombo(page, size, null);
    }

    public List<CnPResponseDTO> getCombo(int page, int size, String filterType) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);

        List<CnPResponseDTO> allItems = buildConcessionList(filterType);

        int fromIndex = Math.min((safePage - 1) * safeSize, allItems.size());
        int toIndex = Math.min(fromIndex + safeSize, allItems.size());

        return new ArrayList<>(allItems.subList(fromIndex, toIndex));
    }

    public ComboResponseDTO comboInfo(int id) {
        Combo combo = comboRepository.findById(id);
        if (combo == null) {
            return null;
        }
        return toComboResponse(combo, true);
    }

    public ComboResponseDTO comboInfoIncludingInactive(int id) {
        Combo combo = comboRepository.findByIdIncludingInactive(id);
        if (combo == null) {
            return null;
        }
        return toComboResponse(combo, true);
    }

    public long countAllCombos() {
        return comboRepository.countAll();
    }

    public int countActiveItems() {
        return countActiveItems(null);
    }

    public int countActiveItems(String filterType) {
        String normalized = normalizeFilterType(filterType);

        if ("COMBO".equals(normalized)) {
            return (int) comboRepository.countActive();
        }

        if ("SINGLE".equals(normalized) || "PRODUCT".equals(normalized)) {
            return (int) productRepository.countActive();
        }

        return (int) (comboRepository.countActive() + productRepository.countActive());
    }

    public int calculateComboStock(int id) {
        Map<Integer, Product> productMap = productRepository.getAllActive().stream()
            .collect(Collectors.toMap(Product::getId, product -> product, (left, right) -> left, HashMap::new));

        return calculateComboStock(comboItemRepository.getByCombo(id), productMap);
    }

    public ComboCRUDResponseDTO AddCombo(Combo combo) {
        ComboCRUDResponseDTO res = new ComboCRUDResponseDTO();

        int comboId = comboRepository.create(combo);
        if (comboId <= 0) {
            res.setMessage("Error");
            res.setStatus(ResponseStatus.ERROR);
            return res;
        }

        combo.setId(comboId);
        res.setMessage("Success");
        res.setStatus(ResponseStatus.SUCCESS);
        res.setCombo(comboRepository.findByIdIncludingInactive(comboId));
        return res;
    }

    public int addComboItem(ComboItem item) {
        validateComboItem(item);
        ensureProductExists(item.getProductId());
        return comboItemRepository.create(item);
    }

    @Transactional(rollbackFor = Exception.class)
    public String editCombo(Combo combo, List<ComboItem> items, boolean hasItemField) {
        if (combo == null) {
            throw new IllegalArgumentException("Combo is null");
        }

        Combo existing = comboRepository.findByIdIncludingInactive(combo.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Combo not found");
        }

        validateEditableComboFields(combo);
        applyComboUpdate(combo, items, hasItemField);
        return "Edit Success";
    }

    @Transactional(rollbackFor = Exception.class)
    public ComboCRUDResponseDTO addComboWithItems(Combo combo, List<ComboItem> items) {
        validateCreateCombo(combo);

        if (items != null && !items.isEmpty()) {
            validateComboItems(items);
        }

        ComboCRUDResponseDTO res = AddCombo(combo);
        if (res.getCombo() == null || res.getCombo().getId() <= 0) {
            throw new RuntimeException("Create combo failed");
        }

        int comboId = res.getCombo().getId();
        if (items != null) {
            for (ComboItem item : items) {
                item.setComboId(comboId);
                int addRs = addComboItem(item);
                if (addRs != 1) {
                    throw new RuntimeException("Insert combo item failed");
                }
            }
        }

        res.setCombo(comboRepository.findByIdIncludingInactive(comboId));
        return res;
    }

    @Transactional(rollbackFor = Exception.class)
    public ComboResponseDTO createCombo(ComboRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Combo payload is required");
        }

        List<ComboItem> items = parseComboItems(dto.getItem());
        if (items == null) {
            items = new ArrayList<>();
        }
        if (!items.isEmpty()) {
            validateComboItems(items);
        }

        Combo combo = new Combo();
        combo.setName(normalizeCreateText(dto.getName()));
        combo.setDescription(normalizeCreateText(dto.getDescription()));
        combo.setPrice(resolveCreatePrice(dto.getPrice(), items));
        combo.setCreatedAt(LocalDateTime.now());
        combo.setIsActive(1);

        if (dto.getBannerFile() != null && !dto.getBannerFile().isEmpty()) {
            combo.setImageUrl(FileUltility.uploadFileImage(
                dto.getBannerFile(),
                "uploads/concessions/combo",
                "concessions/combo"
            ));
        }

        ComboCRUDResponseDTO created = addComboWithItems(combo, items);
        return toComboResponse(created.getCombo(), true);
    }

    @Transactional(rollbackFor = Exception.class)
    public ComboResponseDTO updateCombo(int id, ComboRequestDTO dto) {
        if (id <= 0) {
            throw new IllegalArgumentException("Combo id is invalid");
        }

        if (dto == null) {
            throw new IllegalArgumentException("Combo payload is required");
        }

        Combo existing = comboRepository.findByIdIncludingInactive(id);
        if (existing == null) {
            throw new IllegalArgumentException("Combo not found");
        }

        Combo patch = new Combo();
        patch.setId(id);

        boolean hasItemField = hasMeaningfulItemPayload(dto.getItem());
        List<ComboItem> items = hasItemField ? parseComboItems(dto.getItem()) : null;

        if (hasText(dto.getName())) {
            patch.setName(dto.getName().trim());
        }

        if (hasText(dto.getDescription())) {
            patch.setDescription(dto.getDescription().trim());
        }

        BigDecimal resolvedPrice = resolveUpdatePrice(dto.getPrice(), dto.isPriceProvided());
        if (resolvedPrice != null) {
            patch.setPrice(resolvedPrice);
        }

        if (dto.getBannerFile() != null && !dto.getBannerFile().isEmpty()) {
            patch.setImageUrl(FileUltility.uploadFileImage(
                dto.getBannerFile(),
                "uploads/concessions/combo",
                "concessions/combo"
            ));
        }

        applyComboUpdate(patch, items, hasItemField);
        return comboInfoIncludingInactive(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public int DeleteCombo(int id) {
        comboItemRepository.deleteByCombo(id);
        return comboRepository.delete(id);
    }

    public int DeleteProduct(int id) {
        return productRepository.delete(id);
    }

    public int ChangeActive(int id, int active) {
        return comboRepository.ChangeActive(id, active);
    }

    @Transactional(rollbackFor = Exception.class)
    public ComboResponseDTO toggleComboActive(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Combo id is invalid");
        }

        Combo existing = comboRepository.findByIdIncludingInactive(id);
        if (existing == null) {
            throw new IllegalArgumentException("Combo not found");
        }

        int nextActive = existing.getIsActive() == 1 ? 0 : 1;
        int rs = comboRepository.ChangeActive(id, nextActive);
        if (rs != 1) {
            throw new RuntimeException("Update combo active state failed");
        }

        return comboInfoIncludingInactive(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public ComboResponseDTO activateCombo(int id) {
        return updateComboActiveState(id, 1);
    }

    @Transactional(rollbackFor = Exception.class)
    public ComboResponseDTO deactivateCombo(int id) {
        return updateComboActiveState(id, 0);
    }

    public int checkAdmin(int id) {
        try {
            User user = userRepository.findById(id);
            if (user != null && user.getRoleId() == 1) {
                return 1;
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    private void applyComboUpdate(Combo combo, List<ComboItem> items, boolean hasItemField) {
        boolean hasComboInfoChanged =
            combo.getName() != null ||
            combo.getPrice() != null ||
            combo.getImageUrl() != null ||
            combo.getDescription() != null;

        if (!hasComboInfoChanged && !hasItemField) {
            return;
        }

        if (hasComboInfoChanged) {
            int rs = comboRepository.updateComboPartial(combo);
            if (rs != 1) {
                throw new RuntimeException("Update combo failed");
            }
        }

        if (hasItemField) {
            if (items != null && !items.isEmpty()) {
                validateComboItems(items);
            }

            comboItemRepository.deleteByCombo(combo.getId());

            if (items != null) {
                for (ComboItem comboItem : items) {
                    comboItem.setComboId(combo.getId());
                    int addRs = addComboItem(comboItem);
                    if (addRs != 1) {
                        throw new RuntimeException("Insert combo item failed");
                    }
                }
            }
        }
    }

    private ComboResponseDTO updateComboActiveState(int id, int active) {
        if (id <= 0) {
            throw new IllegalArgumentException("Combo id is invalid");
        }

        Combo existing = comboRepository.findByIdIncludingInactive(id);
        if (existing == null) {
            throw new IllegalArgumentException("Combo not found");
        }

        if (existing.getIsActive() == active) {
            return comboInfoIncludingInactive(id);
        }

        int rs = comboRepository.ChangeActive(id, active);
        if (rs != 1) {
            throw new RuntimeException("Update combo active state failed");
        }

        return comboInfoIncludingInactive(id);
    }

    private List<CnPResponseDTO> buildConcessionList(String filterType) {
        String normalized = normalizeFilterType(filterType);

        List<Product> activeProducts = productRepository.getAllActive();
        Map<Integer, Product> productMap = activeProducts.stream()
            .collect(Collectors.toMap(Product::getId, product -> product, (left, right) -> left, HashMap::new));

        List<CnPResponseDTO> list = new ArrayList<>();

        if (!"SINGLE".equals(normalized) && !"PRODUCT".equals(normalized)) {
            List<Combo> combos = comboRepository.findAllActive();
            for (Combo combo : combos) {
                list.add(toConcessionResponse(combo, productMap));
            }
        }

        if (!"COMBO".equals(normalized)) {
            for (Product product : activeProducts) {
                list.add(toConcessionResponse(product));
            }
        }

        list.sort(Comparator
            .comparing(CnPResponseDTO::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(CnPResponseDTO::getId, Comparator.reverseOrder()));

        return list;
    }

    private ComboResponseDTO toComboResponse(Combo combo, boolean includeItems) {
        if (combo == null) {
            return null;
        }

        ComboResponseDTO dto = new ComboResponseDTO();
        dto.setId(combo.getId());
        dto.setName(combo.getName());
        dto.setDescription(combo.getDescription());
        dto.setPrice(combo.getPrice());
        dto.setImageUrl(formatImageUrl(combo.getImageUrl()));
        dto.setIsActive(combo.getIsActive());
        dto.setCreatedAt(combo.getCreatedAt());

        if (includeItems) {
            dto.setComboItems(comboItemRepository.getByCombo(combo.getId()));
        }

        return dto;
    }

    private CnPResponseDTO toConcessionResponse(Product product) {
        CnPResponseDTO dto = new CnPResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(formatImageUrl(product.getImageUrl()));
        dto.setStock(product.getStock());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setType("SINGLE");
        dto.setItemList(null);
        return dto;
    }

    private CnPResponseDTO toConcessionResponse(Combo combo, Map<Integer, Product> productMap) {
        CnPResponseDTO dto = new CnPResponseDTO();
        dto.setId(combo.getId());
        dto.setName(combo.getName());
        dto.setDescription(combo.getDescription());
        dto.setPrice(combo.getPrice());
        dto.setImageUrl(formatImageUrl(combo.getImageUrl()));
        dto.setIsActive(combo.getIsActive());
        dto.setCreatedAt(combo.getCreatedAt());
        dto.setType("COMBO");

        List<ComboItem> comboItems = comboItemRepository.getByCombo(combo.getId());
        List<ComboItemResponseDTO> itemList = new ArrayList<>();

        for (ComboItem item : comboItems) {
            Product product = productMap.get(item.getProductId());
            if (product == null) {
                continue;
            }

            itemList.add(new ComboItemResponseDTO(
                item.getId(),
                item.getProductId(),
                product.getName(),
                item.getQuantity(),
                product.getPrice(),
                formatImageUrl(product.getImageUrl())
            ));
        }

        dto.setItemList(itemList);
        dto.setStock(calculateComboStock(comboItems, productMap));
        return dto;
    }

    private int calculateComboStock(List<ComboItem> items, Map<Integer, Product> productMap) {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int stock = Integer.MAX_VALUE;

        for (ComboItem item : items) {
            Product product = productMap.get(item.getProductId());
            if (product == null || item.getQuantity() <= 0) {
                return 0;
            }

            int possible = product.getStock() / item.getQuantity();
            stock = Math.min(stock, possible);
        }

        return stock == Integer.MAX_VALUE ? 0 : stock;
    }

    private List<ComboItem> parseComboItems(String rawItems) {
        if (rawItems == null) {
            return null;
        }

        if (rawItems.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(rawItems, new TypeReference<List<ComboItem>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Combo item payload is invalid", e);
        }
    }

    private void validateCreateCombo(Combo combo) {
        if (combo == null) {
            throw new IllegalArgumentException("Combo must not be null");
        }

        if (combo.getPrice() == null || combo.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Combo price is invalid");
        }
    }

    private void validateEditableComboFields(Combo combo) {
        if (combo.getId() <= 0) {
            throw new IllegalArgumentException("Combo id is invalid");
        }

        if (combo.getPrice() != null && combo.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Combo price is invalid");
        }
    }

    private void validateComboItems(List<ComboItem> items) {
        Set<Integer> productIds = new HashSet<>();

        for (ComboItem item : items) {
            validateComboItem(item);

            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException("Duplicate product in combo: " + item.getProductId());
            }

            ensureProductExists(item.getProductId());
        }
    }

    private void validateComboItem(ComboItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Combo item is null");
        }

        if (item.getComboId() < 0) {
            throw new IllegalArgumentException("Combo id is invalid");
        }

        if (item.getProductId() <= 0) {
            throw new IllegalArgumentException("Product id is invalid");
        }

        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    private void ensureProductExists(int productId) {
        if (productRepository.findById(productId) == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
    }

    private String normalizeCreateText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private BigDecimal validatePrice(BigDecimal value, String message) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private BigDecimal resolveCreatePrice(String rawPrice, List<ComboItem> items) {
        return resolveComboPrice(rawPrice, items, "Combo price is required", true);
    }

    private BigDecimal resolveUpdatePrice(String rawPrice, boolean priceProvided) {
        if (!priceProvided) {
            return null;
        }

        if (!hasText(rawPrice)) {
            return null;
        }

        BigDecimal parsedPrice = parsePrice(rawPrice, "Combo price is invalid");
        return validatePrice(parsedPrice, "Combo price is invalid");
    }

    private BigDecimal resolveComboPrice(String rawPrice, List<ComboItem> items, String message, boolean requiredWhenMissing) {
        BigDecimal calculatedPrice = calculateComboPrice(items);

        if (!hasText(rawPrice)) {
            if (isPositive(calculatedPrice)) {
                return calculatedPrice;
            }

            if (requiredWhenMissing) {
                throw new IllegalArgumentException(message);
            }

            return null;
        }

        try {
            BigDecimal parsedPrice = parsePrice(rawPrice, message);
            if (isPositive(parsedPrice)) {
                return parsedPrice;
            }
        } catch (IllegalArgumentException ex) {
            if (isPositive(calculatedPrice)) {
                return calculatedPrice;
            }
            throw ex;
        }

        if (isPositive(calculatedPrice)) {
            return calculatedPrice;
        }

        throw new IllegalArgumentException(message);
    }

    private BigDecimal calculateComboPrice(List<ComboItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ComboItem item : items) {
            if (item == null || item.getProductId() <= 0 || item.getQuantity() <= 0) {
                continue;
            }

            Product product = productRepository.findById(item.getProductId());
            if (product == null || product.getPrice() == null) {
                continue;
            }

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        return total.signum() > 0 ? total : null;
    }

    private BigDecimal parsePrice(String rawPrice, String message) {
        if (!hasText(rawPrice)) {
            return null;
        }

        String sanitized = rawPrice.trim()
            .replaceAll("\\s+", "")
            .replaceAll("[^0-9,.-]", "");

        if (sanitized.isEmpty() || "-".equals(sanitized)) {
            throw new IllegalArgumentException(message);
        }

        boolean negative = sanitized.startsWith("-");
        sanitized = sanitized.replace("-", "");

        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException(message);
        }

        int lastComma = sanitized.lastIndexOf(',');
        int lastDot = sanitized.lastIndexOf('.');
        int separatorIndex = Math.max(lastComma, lastDot);

        Character decimalSeparator = null;
        if (separatorIndex >= 0) {
            int digitsAfterSeparator = sanitized.length() - separatorIndex - 1;
            if (digitsAfterSeparator > 0 && digitsAfterSeparator <= 2) {
                decimalSeparator = sanitized.charAt(separatorIndex);
            }
        }

        StringBuilder normalized = new StringBuilder();
        for (int i = 0; i < sanitized.length(); i++) {
            char current = sanitized.charAt(i);
            if (Character.isDigit(current)) {
                normalized.append(current);
                continue;
            }

            if (decimalSeparator != null && i == separatorIndex && current == decimalSeparator) {
                normalized.append('.');
            }
        }

        if (normalized.length() == 0 || ".".contentEquals(normalized)) {
            throw new IllegalArgumentException(message);
        }

        String normalizedValue = negative ? "-" + normalized : normalized.toString();

        try {
            return new BigDecimal(normalizedValue);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(message, ex);
        }
    }

    private boolean isPositive(BigDecimal value) {
        return value != null && value.signum() > 0;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean hasMeaningfulItemPayload(String rawItems) {
        return rawItems != null && !rawItems.trim().isEmpty();
    }

    private String normalizeFilterType(String filterType) {
        if (filterType == null || filterType.trim().isEmpty()) {
            return null;
        }

        return filterType.trim().toUpperCase(Locale.ROOT);
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
