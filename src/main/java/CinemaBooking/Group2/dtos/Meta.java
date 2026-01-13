package CinemaBooking.Group2.dtos;

public class Meta {
	private int page;
    private long total;
    private int perPage;

    public Meta(int page, long total, int perPage) {
        this.page = page;
        this.total = total;
        this.perPage = perPage;
    }

    // Getters và Setters
    public int getPage() { return page; }
    public long getTotal() { return total; }
    public int getPerPage() { return perPage; }
}
