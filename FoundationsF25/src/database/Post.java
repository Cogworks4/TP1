package database;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Post entity. Soft-deleted via isDeleted flag. 
 * Validation rules:
 *  - title: 1..120 characters
 *  - body: non-empty
 *  - thread: must be in allowed threads
 */
/**
 * Represents a discussion Post (CRUD target) with validation and soft-delete. -
 * Default thread "General" when not provided. - Title 1–120 chars; Body
 * non-empty (trimmed). - Soft-delete preserves replies; UI displays 'original
 * post deleted'.
 * 
 */

public class Post {
	/**
	 * Fields — persisted attributes of the domain entity. Timestamps use Instant;
	 * IDs typically UUID. isDeleted indicates soft-delete status (preserve
	 * historical context).
	 */

	private final UUID id;
	private final String authorId;
	private String title;
	private String body;
	private String thread;
	private List<String> tags;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean isDeleted;

	public Post(UUID id, String authorId, String title, String body, String thread, List<String> tags,
			LocalDateTime createdAt, LocalDateTime updatedAt, boolean isDeleted) {
		this.id = id == null ? UUID.randomUUID() : id;
		this.authorId = Objects.requireNonNull(authorId, "authorId");
		this.title = title;
		this.body = body;
		this.thread = thread == null ? "General" : thread;
		this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
		this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
		this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;
		this.isDeleted = isDeleted;
	}

	/** Convenience factory */
	public static Post create(String authorId, String title, String body, String thread, List<String> tags) {
		return new Post(null, authorId, title, body, thread, tags, null, null, false);
	}

	// --- Getters/Setters ---
	public UUID getId() {
		return id;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getThread() {
		return thread;
	}

	public List<String> getTags() {
		return Collections.unmodifiableList(tags);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setThread(String thread) {
		this.thread = thread == null ? "General" : thread;
	}

	public void setTags(List<String> tags) {
		this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	@Override
	/**
	 * toString — See implementation notes. Follows validation rules: title 1–120,
	 * non-empty body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	public String toString() {
		return "Post{" + "id=" + id + ", authorId='" + authorId + '\'' + ", title='" + title + '\'' + ", thread='"
				+ thread + '\'' + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", isDeleted=" + isDeleted
				+ '}';
	}
}