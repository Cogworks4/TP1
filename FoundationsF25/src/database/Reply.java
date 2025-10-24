package database;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Reply entity bound to a Post by postId. Soft-deleted via isDeleted flag.
 * Validation rules:
 *  - body: non-empty 
 *  - postId: must exist & must not be a deleted post
 */
/**
 * Represents a Reply to a Post with validation and soft-delete. - Body
 * non-empty (trimmed). - Creation only allowed if target post exists and is not
 * deleted.
 *  
 */

public class Reply {
	/**
	 * Fields — persisted attributes of the domain entity. Timestamps use Instant;
	 * IDs typically UUID. isDeleted indicates soft-delete status (preserve
	 * historical context).
	 */

	private final UUID id;
	private final UUID postId;
	private final String authorId;
	private String body;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean isDeleted;

	public Reply(UUID id, UUID postId, String authorId, String body, LocalDateTime createdAt, LocalDateTime updatedAt,
			boolean isDeleted) {
		this.id = id == null ? UUID.randomUUID() : id;
		this.postId = Objects.requireNonNull(postId, "postId");
		this.authorId = Objects.requireNonNull(authorId, "authorId");
		this.body = body;
		this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
		this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;
		this.isDeleted = isDeleted;
	}

	/** Convenience factory */
	public static Reply create(UUID postId, String authorId, String body) {
		return new Reply(null, postId, authorId, body, null, null, false);
	}

	// --- Getters/Setters ---
	public UUID getId() {
		return id;
	}

	public UUID getPostId() {
		return postId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getBody() {
		return body;
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

	public void setBody(String body) {
		this.body = body;
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
		return "Reply{" + "id=" + id + ", postId=" + postId + ", authorId='" + authorId + '\'' + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", isDeleted=" + isDeleted + '}';
	}
}