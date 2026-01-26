package CinemaBooking.Group2.dtos;

import java.util.List;

public class PageResponse<T> {
    private String message;
    private List<T> data;
    private Meta meta;

    public PageResponse() {
    }

    public PageResponse(String message, List<T> data, Meta meta) {
        this.message = message;
        this.data = data;
        this.meta = meta;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public static class Meta {
        private long total;
        private int perPage;
        private int page;

        public Meta() {
        }

        public Meta(long total, int perPage, int page) {
            this.total = total;
            this.perPage = perPage;
            this.page = page;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }
}