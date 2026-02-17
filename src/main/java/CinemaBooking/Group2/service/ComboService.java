package CinemaBooking.Group2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.concession.CnPResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboCRUDResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboItemResponseDTO;
import CinemaBooking.Group2.dtos.concession.ComboResponseDTO;
import CinemaBooking.Group2.mappers.ComboItemMapper;
import CinemaBooking.Group2.mappers.ComboMapper;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.ComboItem;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.Enum.ResponseStatus;
import CinemaBooking.Group2.pattern.Concessions;
import CinemaBooking.Group2.repositories.UserRepository;

@Service
public class ComboService {
	@Autowired
	private UserRepository rep;
	@Autowired
	private Concessions con;
	@Autowired
	private ComboItemMapper mapper;

	public List<ComboResponseDTO> getCombo() {
		try {
			List<Combo> item = con.getCombo();
			// Only return active combos (assuming isActive == 1 means active)
			return item.stream()
					.filter(c -> c != null && c.getIsActive() == 1)
					.map(ComboMapper::toResponseDTO)
					.collect(Collectors.toList());
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}

	public List<CnPResponseDTO> getCombo(int page, int size) {
		return getCombo(page, size, null);
	}

	public List<CnPResponseDTO> getCombo(int page, int size, String filterType) {
		try {
			List<CnPResponseDTO> list = new ArrayList<>();
			
			System.out.println("=== DEBUG: getCombo called with filterType: " + filterType);

			// Fetch COMBO data if filterType is "COMBO" or null
			if (filterType == null || "COMBO".equalsIgnoreCase(filterType)) {
				System.out.println("=== DEBUG: Fetching combos from database...");
				List<Combo> combos = con.pagingCombo(page, size);
				System.out.println("=== DEBUG: Found " + (combos != null ? combos.size() : 0) + " combos");
				
				if (combos != null) {
					for (Combo combo : combos) {
						if (combo == null || combo.getIsActive() != 1) {
							System.out.println("=== DEBUG: Skipping combo (null or inactive): " + (combo != null ? combo.getId() : "null"));
							continue;
						}

						System.out.println("=== DEBUG: Processing combo ID: " + combo.getId() + ", Name: " + combo.getName());
						
						CnPResponseDTO dto = new CnPResponseDTO();
						dto.setId(combo.getId());
						dto.setName(combo.getName());
						dto.setDescription(combo.getDescription());
						dto.setPrice(combo.getPrice());
						dto.setImageUrl(formatImageUrl(combo.getImageUrl()));
						dto.setStock(calculateComboStock(combo.getId()));
						dto.setType("COMBO");
						
						// Only set isActive for admin view (when filterType is null)
						if (filterType == null) {
							dto.setIsActive(combo.getIsActive());
						}

						// Get combo items with product names
						List<ComboItem> comboItems = con.getComboItem(combo.getId());
						System.out.println("=== DEBUG: Found " + (comboItems != null ? comboItems.size() : 0) + " items for combo " + combo.getId());
						
						if (comboItems != null && !comboItems.isEmpty()) {
							List<ComboItemResponseDTO> itemList = new ArrayList<>();
							for (ComboItem item : comboItems) {
								Product product = con.productInfo(item.getProductId());
								if (product != null) {
									ComboItemResponseDTO itemDTO = new ComboItemResponseDTO(
										item.getProductId(),
										product.getName(),
										item.getQuantity()
									);
									itemList.add(itemDTO);
								}
							}
							dto.setItemList(itemList);
						}
						list.add(dto);
					}
				}
			}

			// Fetch SINGLE (Product) data if filterType is null (admin view only)
			if (filterType == null) {
				System.out.println("=== DEBUG: Fetching products from database...");
				List<Product> products = con.getProduct(page, size);
				System.out.println("=== DEBUG: Found " + (products != null ? products.size() : 0) + " products");
				
				if (products != null) {
					for (Product product : products) {
						if (product == null || product.getIsActive() != 1)
							continue;

						CnPResponseDTO dto = new CnPResponseDTO();
						dto.setId(product.getId());
						dto.setName(product.getName());
						dto.setDescription(product.getDescription());
						dto.setPrice(product.getPrice());
						dto.setImageUrl(formatImageUrl(product.getImageUrl()));
						dto.setIsActive(product.getIsActive());
						dto.setStock(product.getStock());
						dto.setType("SINGLE");
						dto.setItemList(null); // Single products don't have itemList
						list.add(dto);
					}
				}
			}

			System.out.println("=== DEBUG: Total items in response: " + list.size());
			return list;
		} catch (Exception e) {
			System.err.println("=== ERROR in getCombo: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	// Helper method to format image URL with /media prefix
	private String formatImageUrl(String imageUrl) {
		if (imageUrl == null || imageUrl.isEmpty()) {
			return "/media/no_img.jpg";
		}
		// If already has /media prefix, return as is
		if (imageUrl.startsWith("/media/")) {
			return imageUrl;
		}
		// If starts with /, just add media before it
		if (imageUrl.startsWith("/")) {
			return "/media" + imageUrl;
		}
		// Otherwise add /media/ prefix
		return "/media/" + imageUrl;
	}
	public ComboResponseDTO comboInfo(int id){
		try {
			Combo item =  con.comboInfo(id);
			return ComboMapper.toResponseDTO(item);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
			// TODO: handle exception
		}
	}

	// Count total active items (active combos + active products)
	public int countActiveItems() {
		try {
			int total = 0;
			List<Combo> combos = con.getCombo();
			if (combos != null) {
				for (Combo c : combos) {
					if (c != null && c.getIsActive() == 1)
						total++;
				}
			}
			List<Product> products = con.getProduct();
			if (products != null) {
				for (Product p : products) {
					if (p != null && p.getIsActive() == 1)
						total++;
				}
			}
			return total;
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return 0;
	}

	public int calculateComboStock(int id) {
		try {
			List<ComboItem> list = con.getComboItem(id);
			int stock = Integer.MAX_VALUE;
			for (ComboItem item : list) {
				int pro_stock = con.productInfo(item.getProductId()).getStock();
				int item_stock = item.getQuantity();
				int possible = pro_stock / item_stock;
				stock = Math.min(stock, possible);
			}
			return stock;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}

	public ComboCRUDResponseDTO AddCombo(Combo combo) {
		ComboCRUDResponseDTO res = new ComboCRUDResponseDTO();
		try {
			int rs = con.CreateCombo(combo);
			if (rs == 1) {
				res.setMessage("Success");
				res.setStatus(ResponseStatus.SUCCESS);
			} else if (rs == 0) {
				res.setMessage("Error");
				res.setStatus(ResponseStatus.ERROR);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return res;
	}

	public String EditCombo(Combo combo) {
		String s = "Error";
		try {
			int rs = con.updateCombo(combo);
			if (rs == 1) {
				s = "Edit Success";
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e);
		}
		return s;
	}

	public int DeleteCombo(int id) {
		return con.deleteCombo(id);
	}

	public int ChangeActive(int id, int active) {
		return con.changeStatusCombo(id, active);
	}

	public int checkAdmin(int id) {
		try {
			User user = rep.findById(id);
			if (user.getRoleId() == 1) {
				return 1;
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.getMessage());
		}
		return 0;
	}
}
