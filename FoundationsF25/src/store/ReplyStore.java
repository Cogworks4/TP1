package store;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import entityClasses.Post;
import entityClasses.Reply;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * In-memory ReplyStore with support for:
 *  - CRUD (soft delete)
 *  - validation (cannot reply to deleted/non-existent post)
 *  - subset view (last query results)
 * Requires a view of PostStore for validation.
 */
/**
 * CRUD manager for Reply with validation and subset search. - Prevents replies
 * to deleted/non-existent posts. - Maintains allReplies and subsetReplies;
 * optional per-post filter.
 * 
 */

public class ReplyStore {

	/**
	 * Represents a lightweight view model for a {@link Reply} when displayed in a
	 * list.
	 *
	 * <p>
	 * This record allows the UI or controller logic to easily determine whether the
	 * current user has read the reply, without needing to expose or compute
	 * internal read-tracking details.
	 * </p>
	 *
	 * @param reply the {@link Reply} object being presented in the list; never
	 *              {@code null}
	 * @param read  {@code true} if the current viewing user has marked the reply as
	 *              read; {@code false} otherwise
	 */
	public record ReplyListItem(Reply reply, boolean read // for the viewing user
	) {
	}

	public static class ValidationException extends RuntimeException {
		public ValidationException(String msg) {
			super(msg);
		}
	}

	public static class PermissionException extends RuntimeException {
		public PermissionException(String msg) {
			super(msg);
		}
	}

	private final Map<UUID, Reply> byId = new LinkedHashMap<>(); // id -> reply
	private final Map<UUID, List<UUID>> replyIdsByPostId = new LinkedHashMap<>(); // postId -> replyIds
	private List<Reply> subset = new ArrayList<>();
	/**
	 * Fields — in-memory collections. all* holds full dataset; subset* reflects
	 * last filter/search operation. Subset can be empty or arbitrarily large.
	 */

	private final Map<UUID, Post> postIndex; // reference snapshot of posts for validation

	/**
	 * ReplyStore — See implementation notes. Follows validation rules: non-empty
	 * body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	public ReplyStore(Collection<Post> posts) {
		this.postIndex = new HashMap<>();
		for (Post p : posts) {
			postIndex.put(p.getId(), p);
		}
	}

	/**
	 * refreshPosts — See implementation notes. Follows validation rules: non-empty
	 * body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	public void refreshPosts(Collection<Post> posts) {
		postIndex.clear();
		for (Post p : posts)
			postIndex.put(p.getId(), p);
	}

	// --- Validation helpers ---
	/**
	 * validateForCreateOrUpdate — See implementation notes. Follows validation
	 * rules: non-empty body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	private void validateForCreateOrUpdate(Reply r, boolean isCreate) {
		String body = r.getBody() == null ? "" : r.getBody().trim();
		if (body.isEmpty()) {
			throw new ValidationException("Reply cannot be empty.");
		}
		Post parent = postIndex.get(r.getPostId());
		if (parent == null || parent.isDeleted()) {
			throw new ValidationException("You cannot reply to a deleted or non-existent post.");
		}
	}

	// --- CRUD ---
	/**
	 * Creates a new entity after validation. Thread defaults to 'General' when
	 * omitted (posts). Reject unknown thread names.
	 * 
	 * @param See signature.
	 * @return Newly created entity.
	 * @throws IllegalArgumentException on validation failures.
	 * @throws SecurityException        on ownership issues.
	 */
	public Reply create(UUID postId, String authorId, String body) {
		Reply r = Reply.create(postId, authorId, body);
		validateForCreateOrUpdate(r, true);
		byId.put(r.getId(), r);
		return r;
	}

	public Optional<Reply> read(UUID id) {
		return Optional.ofNullable(byId.get(id));
	}

	public List<Reply> listAll() {
		return new ArrayList<>(byId.values());
	}

	/**
	 * Updates an entity after validation and ownership checks. Updates updatedAt on
	 * success.
	 * 
	 * @param ... See signature.
	 * @return Updated entity.
	 * @throws IllegalArgumentException on validation failures.
	 * @throws SecurityException        if not owner.
	 */
	public Reply update(UUID id, String actingUserId, String newBody) {
		Reply r = requireExisting(id);
		ensureOwner(r, actingUserId);
		if (newBody != null)
			r.setBody(newBody);
		validateForCreateOrUpdate(r, false);
		r.setUpdatedAt(LocalDateTime.now());
		return r;
	}

	/**
	 * Soft-deletes an entity. UI should confirm before calling.
	 * 
	 * @param id          Entity id.
	 * @param requesterId Caller id.
	 * @return void
	 * @throws SecurityException if not owner.
	 */
	public void delete(UUID id, String actingUserId, boolean confirm) {
		Reply r = requireExisting(id);
		ensureOwner(r, actingUserId);
		if (!confirm)
			return;
		r.setDeleted(true);
		r.setUpdatedAt(LocalDateTime.now());
	}

	// --- Read/Unread
	/**
	 * Marks the specified reply as read by the given user.
	 *
	 * <p>
	 * This method locates the {@link Reply} associated with the provided
	 * {@code replyId} and records that the specified {@code userId} has read it. If
	 * either identifier is not found in the reply collection, no action is taken.
	 * </p>
	 *
	 * @param userId  the unique identifier of the user marking the reply as read;
	 *                may be {@code null} but will result in no action
	 * @param replyId the unique identifier of the reply to update; if not found,
	 *                this operation is ignored
	 */
	public void markReplyRead(UUID userId, UUID replyId) {
		Reply r = byId.get(replyId);
		if (r != null)
			r.markRead(userId);
	}

	/**
	 * Marks the specified reply as unread by the given user.
	 *
	 * <p>
	 * This method locates the {@link Reply} associated with the provided
	 * {@code replyId} and removes the specified {@code userId} from the read
	 * tracking data. If the reply cannot be found or {@code userId} is
	 * {@code null}, this method performs no action.
	 * </p>
	 *
	 * @param userId  the unique identifier of the user marking the reply as unread;
	 *                ignored if {@code null}
	 * @param replyId the unique identifier of the reply to update; if not found,
	 *                this operation is ignored
	 */
	public void markReplyUnread(UUID userId, UUID replyId) {
		Reply r = byId.get(replyId);
		if (r != null)
			r.markUnread(userId);

	}

	/**
	 * Counts replies belonging to the given post.
	 *
	 * @param postId the parent post id
	 * @return total number of replies for the post
	 */
	public int countReplies(UUID postId) {
		return replyIdsByPostId.getOrDefault(postId, List.of()).size();
	}

	public int countUnreadReplies(UUID userId, UUID postId) {
		List<UUID> ids = replyIdsByPostId.getOrDefault(postId, List.of());
		int unread = 0;
		for (UUID id : ids) {
			Reply r = byId.get(id);
			if (r != null && !r.isReadBy(userId))
				unread++;
		}
		return unread;
	}

	// --- Search / Subset ---
	/**
	 * Case-insensitive keyword search with optional filter. Zero-result searches
	 * are valid.
	 * 
	 * @param keywords Search string.
	 * @param filter   Thread or post filter.
	 * @return List<T> results.
	 */
	public List<Reply> search(String keywords, Optional<UUID> postFilterOpt) {
	    // Normalize keyword string
	    final String kwRaw = keywords == null ? "" : keywords.trim();
	    final String kw = kwRaw.toLowerCase(Locale.ROOT);

	    Predicate<Reply> pred = r -> !r.isDeleted();

	    // Keyword filter (case-insensitive, null-safe on body)
	    if (!kw.isEmpty()) {
	        pred = pred.and(r -> {
	            final String body = r.getBody();
	            if (body == null || body.isEmpty()) return false;
	            return body.toLowerCase(Locale.ROOT).contains(kw);
	        });
	    }

	    // Sort by createdAt, oldest first (null-safe)
	    Comparator<Reply> byCreated =
	            Comparator.comparing(Reply::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));

	    subset = byId.values().stream()
	            .filter(pred)
	            .sorted(byCreated)
	            .collect(Collectors.toList());

	    return new ArrayList<>(subset);
	}

	// --- Listing helpers (with flags & counts) ---
	/**
	 * Retrieves a list of replies associated with the specified post, each paired
	 * with a flag indicating whether the given user has marked that reply as read.
	 *
	 * <p>
	 * This method constructs {@link ReplyListItem} objects for UI or controller
	 * consumption, allowing display layers to easily differentiate between read and
	 * unread replies without needing access to deeper reply state logic.
	 * </p>
	 *
	 * @param userId the unique identifier of the user whose read state should be
	 *               checked; may be {@code null}, in which case all replies are
	 *               treated as unread
	 * @param postId the unique identifier of the post whose replies are being
	 *               retrieved; if {@code null} or no replies exist for this post,
	 *               an empty list is returned
	 * @return a list of {@link ReplyListItem} objects; never {@code null}, possibly
	 *         empty
	 */
	public List<ReplyListItem> listByPostWithReadFlag(UUID userId, UUID postId) {
		List<UUID> ids = replyIdsByPostId.getOrDefault(postId, List.of());
		List<ReplyListItem> out = new ArrayList<>(ids.size());
		for (UUID id : ids) {
			Reply r = byId.get(id);
			if (r != null) {
				out.add(new ReplyListItem(r, r.isReadBy(userId)));
			}
		}
		return out;
	}

	public List<Reply> getSubset() {
		return new ArrayList<>(subset);
	}

	public void clearSubset() {
		subset = new ArrayList<>();
	}

	// --- Helpers ---
	/**
	 * requireExisting — See implementation notes. Follows validation rules:
	 * non-empty body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	private Reply requireExisting(UUID id) {
		Reply r = byId.get(id);
		if (r == null)
			throw new NoSuchElementException("Reply not found: " + id);
		return r;
	}

	/**
	 * Ensures requester owns the entity.
	 * 
	 * @param entity      Post/Reply.
	 * @param requesterId User id.
	 * @return void
	 * @throws SecurityException if not owner.
	 */
	private void ensureOwner(Reply r, String actingUserId) {
		if (!Objects.equals(r.getAuthorId(), actingUserId)) {
			throw new PermissionException("You can only modify your own posts/replies.");
		}
	}
}