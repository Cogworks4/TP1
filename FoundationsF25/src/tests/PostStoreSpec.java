package tests;

import store.PostStore;

import java.util.*;

import entityClasses.Post;

/**
 * Specification tests for the {@link PostStore} in-memory CRUD manager.
 *
 * <p>This test class exercises the complete behavior of {@code PostStore}
 * and its backing {@link Post} entities through a series of self-validating
 * assertions implemented with {@code TestHelper}. The tests run sequentially
 * in {@link #main(String[])} and validate correctness, error handling, and
 * data integrity for all major operations.</p>
 *
 * <h2>Overview</h2>
 * <p>
 * The {@code PostStoreSpec} class functions as a lightweight integration test
 * suite. It validates core CRUD operations, field validation, soft deletion,
 * keyword search, subset management, and large dataset behavior.
 * Optional tests cover per-user read/unread state management.
 * </p>
 *
 * <h3>Coverage Summary</h3>
 * <ul>
 *   <li><b>Create</b> — Validates creation rules, default thread assignment, and title/body constraints.</li>
 *   <li><b>Read</b> — Verifies retrieval via {@code read()} and full listings with {@code listAll()}.</li>
 *   <li><b>Update</b> — Confirms ownership enforcement, content updates, and field-level validation.</li>
 *   <li><b>Delete</b> — Tests soft-deletion and user confirmation logic.</li>
 *   <li><b>Search</b> — Exercises keyword queries, subset synchronization, and empty-result behavior.</li>
 *   <li><b>Read/Unread</b> — (Optional) Ensures user-specific read tracking is correctly recorded and isolated.</li>
 *   <li><b>Error Messaging</b> — Asserts presence of informative messages in thrown validation exceptions.</li>
 *   <li><b>Performance</b> — Performs a large-scale insertion and search to validate scalability assumptions.</li>
 * </ul>
 *
 * <h3>Execution</h3>
 * <p>
 * The test suite can be executed directly via its {@code main()} method.
 * Each assertion emits a human-readable result through {@code TestHelper}.
 * A summary line is printed at the end of execution indicating total
 * passed and failed tests.
 * </p>
 *
 * <h3>Implementation Notes</h3>
 * <ul>
 *   <li>Each test operates on distinct posts and does not rely on prior test outcomes, minimizing cross-test interference.</li>
 *   <li>Assertions are performed using {@code TestHelper.assertTrue}
 *       and {@code TestHelper.expectException}, providing descriptions
 *       for every expected behavior.</li>
 *   <li>Timing and concurrency are not validated; these tests assume
 *       single-threaded in-memory operation.</li>
 * </ul>
 *
 * <p>All tests must pass before promoting {@code PostStore} to the
 * integration phase of the project.</p>
 *
 * @see PostStore
 * @see Post
 * @see TestHelper
 */

public class PostStoreSpec { 

    private static final String USER_A = "userA";
    private static final String USER_B = "userB";
    
    
    /**
     * Executes the full {@code PostStoreSpec} test suite.
     *
     * <p>This method sequentially exercises all {@link PostStore} CRUD operations,
     * validation rules, search functionality, and optional read/unread behavior.
     * Results are reported to the console via {@link TestHelper}, including
     * pass/fail indicators and a final summary line.</p>
     *
     * <p>To run the tests, execute this class directly from the command line or an IDE.
     * No command-line arguments are required.</p>
     *
     * @param args optional command-line arguments (not used)
     */
    
    public static void main(String[] args) {
        PostStore posts = new PostStore(new HashSet<>(Arrays.asList("General", "Homework", "Projects")));

        // Create with default thread
        Post p1 = posts.create(USER_A, "HW2 help", "How do I model subsets?", null, Arrays.asList("hw2","subset"));
        TestHelper.assertTrue("General".equals(p1.getThread()) && p1.getId()!=null && !p1.isDeleted(), "Create defaults to General");

        // Reject empty title
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, "   ", "content", "General", null),
                "Reject empty title");

        // Reject long title
        String longTitle = "x".repeat(121);
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, longTitle, "content", "General", null),
                "Reject long title");

        // Reject empty body
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, "Valid", "   ", "General", null),
                "Reject empty body");

        // Unknown thread
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, "Valid", "body", "UnknownThread", null),
                "Reject unknown thread");

        // Read/list my posts
        Post p2 = posts.create(USER_A, "A", "x", "General", null);
        Post p3 = posts.create(USER_A, "B", "y", "Homework", null);
        List<Post> all = posts.listAll();
        TestHelper.assertTrue(all.contains(p2) && all.contains(p3), "List contains my posts");

        // Update my post
        Post p4 = posts.create(USER_A, "Old", "Old body", "General", null);
        Post updated = posts.update(p4.getId(), USER_A, "New", "New body", "Homework", Arrays.asList("updated"));
        TestHelper.assertTrue(
                "New".equals(updated.getTitle()) &&
                "New body".equals(updated.getBody()) &&
                "Homework".equals(updated.getThread()) &&
                updated.getTags().size()==1,
                "Update my post");

        // Reject empty body on update
        Post p5 = posts.create(USER_A, "Title", "Body", "General", null);
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.update(p5.getId(), USER_A, null, "   ", null, null),
                "Reject empty body on update");

        // Cannot update others’ post
        Post p6 = posts.create(USER_A, "Title", "Body", "General", null);
        TestHelper.expectException(PostStore.PermissionException.class,
                () -> posts.update(p6.getId(), USER_B, "Hacked", null, null, null),
                "Cannot update others' post");

        // Delete with confirmation
        Post p7 = posts.create(USER_A, "Title", "Body", "General", null);
        posts.delete(p7.getId(), USER_A, true);
        TestHelper.assertTrue(posts.read(p7.getId()).get().isDeleted(), "Soft delete with confirmation");

        // Cancel deletion
        Post p8 = posts.create(USER_A, "Title", "Body", "General", null);
        posts.delete(p8.getId(), USER_A, false);
        TestHelper.assertTrue(!posts.read(p8.getId()).get().isDeleted(), "Cancel deletion keeps post");

        // Keyword search (subset)
        posts.create(USER_A, "Heap question", "about binary heap", "General", Arrays.asList("heap"));
        posts.create(USER_A, "Graphs", "Dijkstra", "Homework", Arrays.asList("graph"));
        List<Post> results = posts.search("heap", Optional.empty());
        TestHelper.assertTrue(results.size()==1 && "Heap question".equals(results.get(0).getTitle()), "TC-S1: Search 'heap' finds 1");
        TestHelper.assertTrue(results.equals(posts.getSubset()), "Subset reflects last search");

        // Zero-result search
        List<Post> r0 = posts.search("nonexistentphrase", Optional.empty());
        TestHelper.assertTrue(r0.isEmpty(), "Zero-result search");

        // Clear subset
        posts.search("A", Optional.empty());
        TestHelper.assertTrue(!posts.getSubset().isEmpty(), "Subset non-empty before clear");
        posts.clearSubset();
        TestHelper.assertTrue(posts.getSubset().isEmpty(), "Subset cleared");

        // Large dataset (lenient timing check omitted in plain runner)
        int created = 0;
        for (int i = 0; i < 1500; i++) {
            String title2 = (i % 10 == 0) ? "test " + i : "other " + i;
            posts.create(USER_A, title2, "Body " + i, "General", null);
            created++;
        }
        List<Post> rs = posts.search("test", Optional.empty());
        TestHelper.assertTrue(rs.size() >= 120 && rs.size() <= 200, "Large dataset match count plausible ("+rs.size()+")");

        // Error message quality (spot checks)
        try {
            posts.create(USER_A, "", "b", "General", null);
            TestHelper.assertTrue(false, "TC-E1: Title error message thrown");
        } catch (PostStore.ValidationException ex) {
            TestHelper.assertTrue(ex.getMessage().contains("Title"), "Title message mentions 'Title'");
        }

        Post p9 = posts.create(USER_A, "T", "b", "General", null);
        try {
            posts.update(p9.getId(), USER_A, null, "", null, null);
            TestHelper.assertTrue(false, "Body error message thrown");
        } catch (PostStore.ValidationException ex) {
            TestHelper.assertTrue(ex.getMessage().contains("Body"), "Body message mentions 'Body'");
        }
        
        // --- READ/UNREAD TESTS ---
        UUID userAId = UUID.randomUUID();
        UUID userBId = UUID.randomUUID();

        Post p10 = posts.create(USER_A, "Read/unread test", "Body", "General", null);

        // Initially unread
        TestHelper.assertTrue(!p10.isReadBy(userAId), "New post starts unread for userA");

        // Mark as read
        posts.markPostRead(userAId, p10.getId());
        TestHelper.assertTrue(p10.isReadBy(userAId), "Post marked read for userA");

        // Mark as unread
        posts.markPostUnread(userAId, p10.getId());
        TestHelper.assertTrue(!p10.isReadBy(userAId), "Post marked unread for userA");

        // Independent per-user states
        posts.markPostRead(userAId, p10.getId());
        TestHelper.assertTrue(p10.isReadBy(userAId) && !p10.isReadBy(userBId),
            "Read/unread tracked per user");

        TestHelper.summary();
    }
}
