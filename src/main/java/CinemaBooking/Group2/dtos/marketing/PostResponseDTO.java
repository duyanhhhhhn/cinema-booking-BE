package CinemaBooking.Group2.dtos.marketing;

import java.time.LocalDateTime;

public class PostResponseDTO {

		private int id;
	    private String title;
	    private String slug;
	    private String excerpt;
	    private String content;
	    private String category;
	    private String coverUrl;
	    private int published;
	    private LocalDateTime publishedAt;
	    private LocalDateTime createdAt;
	    private String message;
	    private boolean isSuccess;
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public boolean getIsSuccess() {
			return isSuccess;
		}
		public void setIsSuccess(boolean status) {
			this.isSuccess = status;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getSlug() {
			return slug;
		}
		public void setSlug(String slug) {
			this.slug = slug;
		}
		public String getExcerpt() {
			return excerpt;
		}
		public void setExcerpt(String excerpt) {
			this.excerpt = excerpt;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getCoverUrl() {
			return coverUrl;
		}
		public void setCoverUrl(String coverUrl) {
			this.coverUrl = coverUrl;
		}
		public int getPublished() {
			return published;
		}
		public void setPublished(int published) {
			this.published = published;
		}
		public LocalDateTime getPublishedAt() {
			return publishedAt;
		}
		public void setPublishedAt(LocalDateTime publishedAt) {
			this.publishedAt = publishedAt;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		/**
		 * @param id
		 * @param title
		 * @param slug
		 * @param excerpt
		 * @param content
		 * @param category
		 * @param coverUrl
		 * @param published
		 * @param publishedAt
		 * @param createdAt
		 */
	 public PostResponseDTO(int id, String title, String slug, String excerpt, String content, String category, String coverUrl,
				int published, LocalDateTime publishedAt, LocalDateTime createdAt) {
			super();
			this.id = id;
			this.title = title;
			this.slug = slug;
			this.excerpt = excerpt;
			this.content = content;
			this.category = category;
			this.coverUrl = coverUrl;
			this.published = published;
			this.publishedAt = publishedAt;
			this.createdAt = createdAt;
		}
	 public PostResponseDTO() {};
	 public PostResponseDTO(boolean status,String m) {
		 super();
		 this.isSuccess = status;
		 this.message = m;
	 };

}
