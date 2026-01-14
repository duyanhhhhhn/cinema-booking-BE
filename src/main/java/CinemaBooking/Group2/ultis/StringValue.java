package CinemaBooking.Group2.ultis;

public class StringValue {
	public static String tbl_combo="`combo`";
	public static String tbl_comboItem="`combo_item`";
	public static String  tbl_product="product";
	public static String tbl_shift="work_shift";
	public static String tbl_voucher = "voucher";
	public static String tbl_banner = "banner";
	public static String tbl_payment = "payment";
	public static String tbl_post = "post";
	public static String tbl_schedule = "staff_schedule";
	public static float calculateTotalPage(float totalSize,int size) {
		try {
			float totalPage;
			float left = totalSize / (float) size;
			int right = (int) totalSize / size;
			if (left > right) {
				totalPage = right + 1;
			} else {
				totalPage = right;
			}
			return totalPage;
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return 0;
	}
}
