package store;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import database.Post;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * In-memory PostStore with support for: 
 *  - CRUD 
 *  - validation
 *  - subset view 
 * Thread governance (allowedThreads) is injected to mirror staff-only thread creation.
 */
/**
 * CRUD manager for Post with validation and subset search. - Enforces ownership
 * and thread rules (students cannot invent new threads). - Maintains allPosts
 * and subsetPosts to back UI views.
 * 
 */

public class PostStore {

	/**
	 * Represents a lightweight view model for a {@link Post} when displayed in a
	 * list.
	 *
	 * <p>
	 * This record is designed for UI or controller usage where both the post
	 * metadata and the user’s read state are relevant. It also includes reply
	 * statistics so that interfaces can display engagement indicators such as total
	 * replies and unread replies per user.
	 * </p>
	 *
	 * @param post             the {@link Post} being presented in the list; never
	 *                         {@code null}
	 * @param read             {@code true} if the current viewing user has marked
	 *                         the post as read; {@code false} otherwise
	 * @param replyCount       total number of replies associated with this post;
	 *                         must be {@code >= 0}
	 * @param unreadReplyCount number of replies the viewing user has not yet marked
	 *                         as read; must be {@code >= 0} and
	 *                         {@code <= replyCount}
	 */
	public record PostListItem(Post post, boolean read, // for the viewing user
			int replyCount, int unreadReplyCount) {
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

	/**
	 * Fields — in-memory collections simulating persistence for HW2. all* holds
	 * full dataset; subset* reflects last filter/search operation. Subset can be
	 * empty or arbitrarily large.
	 */

	private final Set<String> allowedThreads;
	private final Map<UUID, Post> byId = new LinkedHashMap<>();
	private ReplyStore replyStore; // inject this
	private List<Post> subset = new ArrayList<>();

	/**
	 * Wires the {@link store.ReplyStore} used to compute reply counts and unread
	 * reply counts.
	 *
	 * @param replyStore the reply store dependency
	 */
	public void setReplyStore(ReplyStore replyStore) {
		this.replyStore = replyStore;
	}

	/**
	 * PostStore — See implementation notes. non-empty body, default 'General',
	 * ownership checks.
	 * 
	 * @return As declared
	 */
	public PostStore(Set<String> allowedThreads) {
		this.allowedThreads = new HashSet<>(allowedThreads);
		this.allowedThreads.add("General"); // always ensure default exists
	}

	// --- Validation helpers ---
	/**
	 * validateForCreateOrUpdate — See implementation notes. Follows validation
	 * rules: title 1–120, non-empty body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	private void validateForCreateOrUpdate(Post p, boolean isCreate) {
		String title = p.getTitle() == null ? "" : p.getTitle().trim();
		String body = p.getBody() == null ? "" : p.getBody().trim();
		String thread = p.getThread() == null ? "General" : p.getThread().trim();

		if (title.length() < 1 || title.length() > 120) {
			throw new ValidationException("Title must be between 1 and 120 characters.");
		}
		if (body.isEmpty()) {
			throw new ValidationException("Body cannot be empty.");
		}
		if (!allowedThreads.contains(thread)) {
			throw new ValidationException("Thread \"" + thread + "\" doesn't exist. Staff create threads.");
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
	public Post create(String authorId, String title, String body, String thread, List<String> tags) {
		Post p = Post.create(authorId, title, body, thread == null ? "General" : thread.trim(), tags);
		validateForCreateOrUpdate(p, true);
		byId.put(p.getId(), p);
		return p;
	}

	/**
	 * Reads an entity by id.
	 * 
	 * @param id Identifier.
	 * @return Entity or Optional<T>.
	 */
	public Optional<Post> read(UUID id) {
		return Optional.ofNullable(byId.get(id));
	}

	/**
	 * listAll — See implementation notes. Follows validation rules: title 1–120,
	 * non-empty body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	public List<Post> listAll() {
		return new ArrayList<>(byId.values());
	}

	/**
	 * Updates an entity after validation and ownership checks. Updates updatedAt on
	 * success.
	 * 
	 * @param See signature.
	 * @return Updated entity.
	 * @throws IllegalArgumentException on validation failures.
	 * @throws SecurityException        if not owner.
	 */
	public Post update(UUID id, String actingUserId, String newTitle, String newBody, String newThread,
			List<String> newTags) {
		Post existing = requireExisting(id);
		ensureOwner(existing, actingUserId);
		if (newTitle != null)
			existing.setTitle(newTitle);
		if (newBody != null)
			existing.setBody(newBody);
		if (newThread != null)
			existing.setThread(newThread);
		if (newTags != null)
			existing.setTags(newTags);
		validateForCreateOrUpdate(existing, false);
		existing.setUpdatedAt(LocalDateTime.now());
		return existing;
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
		Post existing = requireExisting(id);
		ensureOwner(existing, actingUserId);
		if (!confirm)
			return;
		existing.setDeleted(true);
		existing.setUpdatedAt(LocalDateTime.now());
	}

	// --- Read/unread for posts ---
	/**
	 * Marks the specified post as read for the given user.
	 *
	 * <p>
	 * If the {@code postId} resolves to a valid stored post, this method updates
	 * the post’s internal read-tracking state to indicate that the given user has
	 * viewed it. If the post does not exist or {@code userId} is {@code null}, no
	 * action is taken.
	 * </p>
	 *
	 * @param userId the unique identifier of the user marking the post as read;
	 *               ignored if {@code null}
	 * @param postId the unique identifier of the post to mark as read; may be
	 *               {@code null}, in which case this method performs no action
	 */
	public void markPostRead(UUID userId, UUID postId) {
		Post p = byId.get(postId);
		if (p != null)
			p.markRead(userId);
	}

	/**
	 * Marks the specified post as unread for the given user.
	 *
	 * <p>
	 * If the {@code postId} resolves to a valid stored post, this method updates
	 * the post’s internal read-tracking state to indicate that the given user has
	 * not viewed it. If the post does not exist or {@code userId} is {@code null},
	 * no action is taken.
	 * </p>
	 *
	 * @param userId the unique identifier of the user marking the post as unread;
	 *               ignored if {@code null}
	 * @param postId the unique identifier of the post to mark as unread; may be
	 *               {@code null}, in which case this method performs no action
	 */
	public void markPostUnread(UUID userId, UUID postId) {
		Post p = byId.get(postId);
		if (p != null)
			p.markUnread(userId);
	}

	// --- Listing helpers (with flags & counts) ---
	public List<PostListItem> listAllWithReadFlag(UUID userId) {
		List<PostListItem> out = new ArrayList<>(byId.size());
		for (Post p : byId.values()) {
			int replyCount = replyStore != null ? replyStore.countReplies(p.getId()) : 0;
			int unread = replyStore != null ? replyStore.countUnreadReplies(userId, p.getId()) : 0;
			out.add(new PostListItem(p, p.isReadBy(userId), replyCount, unread));
		}
		// newest first
		out.sort(Comparator.comparing((PostListItem i) -> i.post().getCreatedAt()).reversed());
		return out;
	}

	// "My posts" with unread reply count (for dashboard)
	public List<PostListItem> listMineWithUnreadReplyCount(UUID userId) {
		List<PostListItem> out = new ArrayList<>();
		for (Post p : byId.values()) {
			if (Objects.equals(userId, p.getAuthorId())) {
				int replyCount = replyStore != null ? replyStore.countReplies(p.getId()) : 0;
				int unread = replyStore != null ? replyStore.countUnreadReplies(userId, p.getId()) : 0;
				out.add(new PostListItem(p, true /* authors can be treated as read */, replyCount, unread));
			}
		}
		out.sort(Comparator.comparing((PostListItem i) -> i.post().getCreatedAt()).reversed());
		return out;
	}

	// Single-post unread reply counter (useful for detail pages)
	public int countUnreadReplies(UUID userId, UUID postId) {
		return replyStore != null ? replyStore.countUnreadReplies(userId, postId) : 0;
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
	public List<Post> search(String keywords, Optional<String> threadFilterOpt) {
		String kw = keywords == null ? "" : keywords.toLowerCase(Locale.ROOT).trim();
		Predicate<Post> pred = p -> !p.isDeleted();
		if (!kw.isEmpty()) {
			pred = pred.and(p -> {
				String hay = (p.getTitle() + " " + p.getBody() + " " + String.join(" ", p.getTags()))
						.toLowerCase(Locale.ROOT);
				return hay.contains(kw);
			});
		}
		if (threadFilterOpt.isPresent()) {
			String tf = threadFilterOpt.get();
			pred = pred.and(p -> tf.equals(p.getThread()));
		}
		subset = byId.values().stream().filter(pred).collect(Collectors.toList());
		return new ArrayList<>(subset);
	}

	public List<Post> getSubset() {
		return new ArrayList<>(subset);
	}

	public void clearSubset() {
		subset = new ArrayList<>();
	}

	// --- Search with user flag & optional thread filter ---
	/**
	 * Searches posts with optional thread filtering and a case-insensitive keyword
	 * match, then projects each result into a {@link PostListItem} enriched with
	 * read state and reply counts for the given user.
	 *
	 * <p>
	 * The search:
	 * <ul>
	 * <li>Excludes posts that are soft-deleted ({@code p.isDeleted()}).</li>
	 * <li>Filters by thread if {@code threadOpt} is present (case-insensitive
	 * equality).</li>
	 * <li>Performs a case-insensitive substring match of {@code keyword} against
	 * post title and body. A {@code null} or blank keyword matches all posts.</li>
	 * <li>Maps each post to a {@link PostListItem}:
	 * <ul>
	 * <li>{@code read} flag via {@code p.isReadBy(userId)}</li>
	 * <li>{@code replyCount} via {@code replyStore.countReplies(postId)} if
	 * {@code replyStore} is non-null, else 0</li>
	 * <li>{@code unreadReplyCount} via
	 * {@code replyStore.countUnreadReplies(userId, postId)} if {@code replyStore}
	 * is non-null, else 0</li>
	 * </ul>
	 * </li>
	 * <li>Sorts results by {@code post.getCreatedAt()} in descending order (newest
	 * first).</li>
	 * </ul>
	 *
	 * @param userId    the user whose read/unread state should be reflected in the
	 *                  results; may be {@code null}, in which case read/unread
	 *                  computations that depend on {@code userId} may treat the
	 *                  user as unread
	 * @param keyword   search term matched against title and body; {@code null} or
	 *                  blank means no keyword filtering
	 * @param threadOpt optional thread name; if present, only posts whose
	 *                  {@code getThread()} equals this value (case-insensitive) are
	 *                  included
	 * @return a list of {@link PostListItem} instances, never {@code null};
	 *         possibly empty
	 */
	public List<PostListItem> search(UUID userId, String keyword, Optional<String> threadOpt) {
		final String kw = keyword == null ? "" : keyword.trim().toLowerCase();
		return byId.values().stream().filter(p -> !p.isDeleted()) // if you support soft-delete
				.filter(p -> threadOpt.isEmpty() || threadOpt.get().equalsIgnoreCase(p.getThread()))
				.filter(p -> kw.isEmpty() || (p.getTitle() != null && p.getTitle().toLowerCase().contains(kw))
						|| (p.getBody() != null && p.getBody().toLowerCase().contains(kw)))
				.map(p -> {
					int replyCount = replyStore != null ? replyStore.countReplies(p.getId()) : 0;
					int unread = replyStore != null ? replyStore.countUnreadReplies(userId, p.getId()) : 0;
					return new PostListItem(p, p.isReadBy(userId), replyCount, unread);
				}).sorted(Comparator.comparing((PostListItem i) -> i.post().getCreatedAt()).reversed()).toList();
	}

	// --- Helpers ---
	/**
	 * requireExisting — See implementation notes. Follows validation rules: title
	 * 1–120, non-empty body, default 'General', ownership checks.
	 * 
	 * @return As declared
	 */
	private Post requireExisting(UUID id) {
		Post p = byId.get(id);
		if (p == null)
			throw new NoSuchElementException("Post not found: " + id);
		return p;
	}

	/**
	 * Ensures requester owns the entity.
	 * 
	 * @param entity      Post/Reply.
	 * @param requesterId User id.
	 * @return void
	 * @throws SecurityException if not owner.
	 */
	private void ensureOwner(Post p, String actingUserId) {
		if (!Objects.equals(p.getAuthorId(), actingUserId)) {
			throw new PermissionException("You can only modify your own posts.");
		}
	}
}