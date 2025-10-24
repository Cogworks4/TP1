package store;

import database.Post;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * In-memory PostStore with support for: 
 *  - CRUD 
 *  - validation
 *  - subset view 
 * Thread governance (allowedThreads) is injected to mirror staff-only thread creation.
 */
/**
 * CRUD manager for Post with validation and subset search.
 * - Enforces ownership and thread rules (students cannot invent new threads).
 * - Maintains allPosts and subsetPosts to back UI views.
 * 
 */

public class PostStore {

    public static class ValidationException extends RuntimeException {
        public ValidationException(String msg) { super(msg); }
    }
    public static class PermissionException extends RuntimeException {
        public PermissionException(String msg) { super(msg); }
    }
/**
 * Fields — in-memory collections simulating persistence for HW2.
 * all* holds full dataset; subset* reflects last filter/search operation.
 * Subset can be empty or arbitrarily large.
 */

    private final Set<String> allowedThreads;
    private final Map<UUID, Post> byId = new LinkedHashMap<>();
    private List<Post> subset = new ArrayList<>();

/**
 * PostStore — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
 * @return As declared
 */
    public PostStore(Set<String> allowedThreads) {
        this.allowedThreads = new HashSet<>(allowedThreads);
        this.allowedThreads.add("General"); // always ensure default exists
    }

    // --- Validation helpers ---
/**
 * validateForCreateOrUpdate — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
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
 * Creates a new entity after validation.
 * Thread defaults to 'General' when omitted (posts).
 * Reject unknown thread names.
 * @param See signature.
 * @return Newly created entity.
 * @throws IllegalArgumentException on validation failures.
 * @throws SecurityException on ownership issues.
 */
    public Post create(String authorId, String title, String body, String thread, List<String> tags) {
        Post p = Post.create(authorId, title, body, thread == null ? "General" : thread.trim(), tags);
        validateForCreateOrUpdate(p, true);
        byId.put(p.getId(), p);
        return p;
    }

/**
 * Reads an entity by id.
 * @param id Identifier.
 * @return Entity or Optional<T>.
 */
    public Optional<Post> read(UUID id) {
        return Optional.ofNullable(byId.get(id));
    }

/**
 * listAll — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
 * @return As declared
 */
    public List<Post> listAll() {
        return new ArrayList<>(byId.values());
    }

/**
 * Updates an entity after validation and ownership checks.
 * Updates updatedAt on success.
 * @param See signature.
 * @return Updated entity.
 * @throws IllegalArgumentException on validation failures.
 * @throws SecurityException if not owner.
 */
    public Post update(UUID id, String actingUserId, String newTitle, String newBody, String newThread, List<String> newTags) {
        Post existing = requireExisting(id);
        ensureOwner(existing, actingUserId);
        if (newTitle != null) existing.setTitle(newTitle);
        if (newBody != null) existing.setBody(newBody);
        if (newThread != null) existing.setThread(newThread);
        if (newTags != null) existing.setTags(newTags);
        validateForCreateOrUpdate(existing, false);
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
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
        Post existing = requireExisting(id);
        ensureOwner(existing, actingUserId);
        if (!confirm) return;
        existing.setDeleted(true);
        existing.setUpdatedAt(LocalDateTime.now());
    }

    // --- Search / Subset ---
/**
 * Case-insensitive keyword search with optional filter.
 * Zero-result searches are valid.
 * @param keywords Search string.
 * @param filter Thread or post filter.
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

    public List<Post> getSubset() { return new ArrayList<>(subset); }
    public void clearSubset() { subset = new ArrayList<>(); }

    // --- Helpers ---
/**
 * requireExisting — See implementation notes.
 * Follows validation rules: title 1–120, non-empty body, default 'General', ownership checks.
 * @return As declared
 */
    private Post requireExisting(UUID id) {
        Post p = byId.get(id);
        if (p == null) throw new NoSuchElementException("Post not found: " + id);
        return p;
    }
/**
 * Ensures requester owns the entity.
 * @param entity Post/Reply.
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