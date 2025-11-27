package entityClasses;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import store.PostStore;

/**
 * <p>Title: StaffPostModerationService</p>
 *
 * <p>Description:
 * Service class that implements the TP3 aspect
 * <b>Staff Delete/Modify Post Functionality</b>. This class
 * builds on the existing {@link PostStore} behavior by
 * providing staff-only operations that allow staff to delete
 * or modify posts created by any student while preserving
 * the existing validation and soft-delete semantics.
 * </p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Enforce that only staff users can invoke these methods.</li>
 *   <li>Perform soft-delete of posts on behalf of staff.</li>
 *   <li>Modify post title/body/thread while preserving author id.</li>
 *   <li>Reuse {@link PostStore#update(UUID, String, String, String, String, java.util.List)}
 *       and {@link PostStore#delete(UUID, String, boolean)} so that all
 *       existing validation and invariants are honored.</li>
 * </ul>
 *
 * <p>Notes:</p>
 * <ul>
 *   <li>This is a TP3 risk-reduction prototype; logging/audit
 *       can be added later.</li>
 *   <li>The staff role check is based on {@link User#getNewStaff()},
 *       which returns the value of the Staff attribute.</li>
 * </ul>
 *
 * @author TP3
 */
public class StaffPostModerationService {

    /** The underlying PostStore used for all CRUD and validation. */
    private final PostStore postStore;

    /**
     * Constructs a new StaffPostModerationService.
     *
     * @param postStore the {@link PostStore} instance to delegate to;
     *                  must not be {@code null}
     * @throws IllegalArgumentException if {@code postStore} is {@code null}
     */
    public StaffPostModerationService(PostStore postStore) {
        if (postStore == null) {
            throw new IllegalArgumentException("postStore must not be null");
        }
        this.postStore = postStore;
    }

    /**
     * Deletes a post on behalf of a staff user using the existing soft-delete
     * semantics from {@link PostStore#delete(UUID, String, boolean)}.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Requires that {@code actingStaff} is a staff user
     *       ({@link User#getNewStaff()} is true).</li>
     *   <li>Looks up the post in {@link PostStore#read(UUID)}.</li>
     *   <li>If the post does not exist, throws {@link NoSuchElementException}.</li>
     *   <li>If {@code confirm} is false, performs no deletion.</li>
     *   <li>If {@code confirm} is true, calls {@code PostStore.delete} using
     *       the <i>original author's id</i> as the acting user so that the
     *       underlying permission and validation logic is reused.</li>
     * </ul>
     *
     * <p>This method does not permanently remove the post; it sets the
     * post's soft-delete flag and updates the {@code updatedAt} timestamp.
     * Replies remain visible and can display "original post deleted"
     * based on the {@code isDeleted} flag.</p>
     *
     * @param postId      the id of the post to delete; must not be {@code null}
     * @param actingStaff the staff user performing the action; must not be
     *                    {@code null} and must have Staff role
     * @param confirm     {@code true} if the caller has confirmed deletion;
     *                    {@code false} to cancel
     *
     * @throws IllegalArgumentException if {@code actingStaff} is not a staff user
     * @throws NoSuchElementException   if the post does not exist
     */
    public void deletePostAsStaff(UUID postId, User actingStaff, boolean confirm) {
        requireStaff(actingStaff);
        if (!confirm) {
            // UI already asked "Are you sure?" and user said no; nothing to do.
            return;
        }

        Post target = postStore.read(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));

        // Reuse existing ownership-based delete by passing the author's id
        // as the acting user. preserves validation and soft-delete logic.
        postStore.delete(postId, target.getAuthorId(), true);

        // add audit logging for full TP3 implementation
    }

    /**
     * Modifies a post's title/body/thread on behalf of a staff user.
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Requires that {@code actingStaff} is a staff user.</li>
     *   <li>Looks up the post; throws {@link NoSuchElementException} if missing.</li>
     *   <li>Delegates to {@link PostStore#update(UUID, String, String, String, String, java.util.List)}
     *       using the original author's id as the acting user.</li>
     *   <li>All existing validation rules apply:
     *     <ul>
     *       <li>Title length 1..120.</li>
     *       <li>Body non-empty (if provided).</li>
     *       <li>Thread must be in the allowedThread set.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>Staff cannot change the {@code authorId}; the original author
     * remains associated with the post.</p>
     *
     * @param postId      the id of the post to modify; must not be {@code null}
     * @param actingStaff the staff user performing the action; must not be
     *                    {@code null} and must have Staff role
     * @param newTitle    the new title, or {@code null} to leave unchanged
     * @param newBody     the new body, or {@code null} to leave unchanged
     * @param newThread   the new thread name, or {@code null} to leave unchanged
     * @param newTags     the new set of tags, or {@code null} to leave unchanged
     *
     * @return the updated {@link Post} instance
     *
     * @throws IllegalArgumentException      if {@code actingStaff} is not staff
     * @throws NoSuchElementException        if the post does not exist
     * @throws PostStore.ValidationException if validation fails (e.g., title too long)
     */
    public Post modifyPostAsStaff(
            UUID postId,
            User actingStaff,
            String newTitle,
            String newBody,
            String newThread,
            java.util.List<String> newTags) {

        requireStaff(actingStaff);

        Post target = postStore.read(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));

        // Delegate to existing update logic to reuse all validation and constants.
        return postStore.update(postId, target.getAuthorId(), newTitle, newBody, newThread, newTags);
    }

    /**
     * Internal helper: ensures the user is non-null and has the Staff attribute.
     *
     * @param actingStaff the user to check
     * @throws IllegalArgumentException if {@code actingStaff} is null or not staff
     */
    private void requireStaff(User actingStaff) {
        if (actingStaff == null || !actingStaff.getNewStaff()) {
            throw new IllegalArgumentException("actingStaff must be a staff user");
        }
    }
}
