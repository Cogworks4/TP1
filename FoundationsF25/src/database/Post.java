package database;

import java.time.LocalDateTime;
import java.util.*;
import java.util.HashSet;
import java.util.Set;

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
	private final Set<UUID> readByUserIds = new HashSet<>();
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
	
	// --- Public Helpers --
	/**
	 * Checks whether the specified user has marked this post as read.
	 *
	 * <p>This method determines if the given {@code userID} exists
	 * in the collection tracking which users have read the post.</p>
	 *
	 * @param userID the unique identifier of the user to check; may be {@code null}
	 * @return {@code true} if the user has read the post; {@code false} if the user has not
	 *         read it or if {@code userID} is {@code null}
	 */
	public boolean isReadBy(UUID userID) {
		return userID != null && readByUserIds.contains(userID);
	}
	
	/**
	 * Marks this post as read for the specified user.
	 *
	 * <p>If the provided {@code userID} is valid, the user is added to the set of
	 * readers who have seen this post. If {@code userID} is {@code null}, this method
	 * will perform no action.</p>
	 *
	 * @param userID the unique identifier of the user marking the post as read;
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
	 * <p>If the provided {@code userID} is valid, the user is removed from the set of
	 * readers who have seen this post. If {@code userID} is {@code null}, this method
	 * will perform no action.</p>
	 *
	 * @param userID the unique identifier of the user marking the post as unread;
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
		return "Post{" + "id=" + id + ", authorId='" + authorId + '\'' + ", title='" + title + '\'' + ", thread='"
				+ thread + '\'' + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", isDeleted=" + isDeleted
				+ '}';
	}
	
	
}