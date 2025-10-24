package store;

import database.Post;
import database.Reply;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * In-memory ReplyStore with support for:
 *  - CRUD (soft delete)
 *  - validation (cannot reply to deleted/non-existent post)
 *  - subset view (last query results)
 * Requires a view of PostStore for validation.
 */
/**
 * CRUD manager for Reply with validation and subset search.
 * - Prevents replies to deleted/non-existent posts.
 * - Maintains allReplies and subsetReplies; optional per-post filter.
 * 
 */

public class ReplyStore {

    public static class ValidationException extends RuntimeException {
        public ValidationException(String msg) { super(msg); }
    }
    public static class PermissionException extends RuntimeException {
        public PermissionException(String msg) { super(msg); }
    }

    private final Map<UUID, Reply> byId = new LinkedHashMap<>();
    private List<Reply> subset = new ArrayList<>();
/**
 * Fields — in-memory collections simulating persistence for HW2.
 * all* holds full dataset; subset* reflects last filter/search operation.
 * Subset can be empty or arbitrarily large.
 */

    private final Map<UUID, Post> postIndex; // reference snapshot of posts for validation

/**
 * ReplyStore — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
 * @return As declared
 */
    public ReplyStore(Collection<Post> posts) {
        this.postIndex = new HashMap<>();
        for (Post p : posts) {
            postIndex.put(p.getId(), p);
        }
    }

/**
 * refreshPosts — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
 * @return As declared
 */
    public void refreshPosts(Collection<Post> posts) {
        postIndex.clear();
        for (Post p : posts) postIndex.put(p.getId(), p);
    }

    // --- Validation helpers ---
/**
 * validateForCreateOrUpdate — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
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
 * Creates a new entity after validation.
 * Thread defaults to 'General' when omitted (posts).
 * Reject unknown thread names.
 * @param See signature.
 * @return Newly created entity.
 * @throws IllegalArgumentException on validation failures.
 * @throws SecurityException on ownership issues.
 */
    public Reply create(UUID postId, String authorId, String body) {
        Reply r = Reply.create(postId, authorId, body);
        validateForCreateOrUpdate(r, true);
        byId.put(r.getId(), r);
        return r;
    }

    public Optional<Reply> read(UUID id) { return Optional.ofNullable(byId.get(id)); }
    public List<Reply> listAll() { return new ArrayList<>(byId.values()); }

/**
 * Updates an entity after validation and ownership checks.
 * Updates updatedAt on success.
 * @param ... See signature.
 * @return Updated entity.
 * @throws IllegalArgumentException on validation failures.
 * @throws SecurityException if not owner.
 */
    public Reply update(UUID id, String actingUserId, String newBody) {
        Reply r = requireExisting(id);
        ensureOwner(r, actingUserId);
        if (newBody != null) r.setBody(newBody);
        validateForCreateOrUpdate(r, false);
        r.setUpdatedAt(LocalDateTime.now());
        return r;
    }

/**
 * Soft-deletes an entity.
 * UI should confirm before calling.
 * @param id Entity id.
 * @param requesterId Caller id.
 * @return void
 * @throws SecurityException if not owner.
 */
    public void delete(UUID id, String actingUserId, boolean confirm) {
        Reply r = requireExisting(id);
        ensureOwner(r, actingUserId);
        if (!confirm) return;
        r.setDeleted(true);
        r.setUpdatedAt(LocalDateTime.now());
    }

    // --- Search / Subset ---
/**
 * Case-insensitive keyword search with optional filter.
 * Zero-result searches are valid.
 * @param keywords Search string.
 * @param filter Thread or post filter.
 * @return List<T> results.
 */
    public List<Reply> search(String keywords, Optional<UUID> postFilterOpt) {
        String kw = keywords == null ? "" : keywords.toLowerCase(Locale.ROOT).trim();
        Predicate<Reply> pred = r -> !r.isDeleted();
        if (!kw.isEmpty()) {
            pred = pred.and(r -> r.getBody().toLowerCase(Locale.ROOT).contains(kw));
        }
        if (postFilterOpt.isPresent()) {
            UUID pid = postFilterOpt.get();
            pred = pred.and(r -> pid.equals(r.getPostId()));
        }
        subset = byId.values().stream().filter(pred).sorted(Comparator.comparing(Reply::getCreatedAt)).collect(Collectors.toList());
        return new ArrayList<>(subset);
    }

    public List<Reply> getSubset() { return new ArrayList<>(subset); }
    public void clearSubset() { subset = new ArrayList<>(); }

    // --- Helpers ---
/**
 * requireExisting — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
 * @return As declared
 */
    private Reply requireExisting(UUID id) {
        Reply r = byId.get(id);
        if (r == null) throw new NoSuchElementException("Reply not found: " + id);
        return r;
    }
/**
 * Ensures requester owns the entity.
 * @param entity Post/Reply.
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