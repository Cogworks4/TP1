package entityClasses;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

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
	private final Set<UUID> readByUserIds = new HashSet<>();
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

	// --- Public Helpers ---
	/**
	 * Checks whether the specified user has marked this post as read.
	 *
	 * <p>
	 * This method determines if the given {@code userID} exists in the collection
	 * tracking which users have read the reply.
	 * </p>
	 *
	 * @param userID the unique identifier of the user to check; may be {@code null}
	 * @return {@code true} if the user has read the reply; {@code false} if the
	 *         user has not read it or if {@code userID} is {@code null}
	 */
	public boolean isReadBy(UUID userID) {
		return userID != null && readByUserIds.contains(userID);
	}

	/**
	 * Marks this post as read for the specified user.
	 *
	 * <p>
	 * If the provided {@code userID} is valid, the user is added to the set of
	 * readers who have seen this reply. If {@code userID} is {@code null}, this
	 * method will perform no action.
	 * </p>
	 *
	 * @param userID the unique identifier of the user marking the reply as read;
	 *               ignored if {@code null}
	 */
	public void markRead(UUID userID) {
		if (userID != null) {
			readByUserIds.add(userID);
		}
	}

	/**
	 * Marks this post as unread for the specified user.
	 *
	 * <p>
	 * If the provided {@code userID} is valid, the user is removed from the set of
	 * readers who have seen this reply. If {@code userID} is {@code null}, this
	 * method will perform no action.
	 * </p>
	 *
	 * @param userID the unique identifier of the user marking the reply as unread;
	 *               ignored if {@code null}
	 */
	public void markUnread(UUID userID) {
		if (userID != null) {
			readByUserIds.remove(userID);
		}
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

	// use this if needed for view Posts
	public Set<UUID> getReadByUserIds() {
		return new HashSet<>(readByUserIds);
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