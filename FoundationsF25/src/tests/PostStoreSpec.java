package tests;

import store.PostStore;
import database.Post;

import java.util.*;

public class PostStoreSpec { 

    private static final String USER_A = "userA";
    private static final String USER_B = "userB";

    public static void main(String[] args) {
        PostStore posts = new PostStore(new HashSet<>(Arrays.asList("General", "Homework", "Projects")));

        // TC-P1: Create with default thread
        Post p1 = posts.create(USER_A, "HW2 help", "How do I model subsets?", null, Arrays.asList("hw2","subset"));
        TestHelper.assertTrue("General".equals(p1.getThread()) && p1.getId()!=null && !p1.isDeleted(), "TC-P1: Create defaults to General");

        // TC-P2: Reject empty title
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, "   ", "content", "General", null),
                "TC-P2: Reject empty title");

        // TC-P3: Reject long title
        String longTitle = "x".repeat(121);
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, longTitle, "content", "General", null),
                "TC-P3: Reject long title");

        // TC-P4: Reject empty body
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, "Valid", "   ", "General", null),
                "TC-P4: Reject empty body");

        // TC-P5: Unknown thread
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.create(USER_A, "Valid", "body", "UnknownThread", null),
                "TC-P5: Reject unknown thread");

        // TC-P6: Read/list my posts
        Post p2 = posts.create(USER_A, "A", "x", "General", null);
        Post p3 = posts.create(USER_A, "B", "y", "Homework", null);
        List<Post> all = posts.listAll();
        TestHelper.assertTrue(all.contains(p2) && all.contains(p3), "TC-P6: List contains my posts");

        // TC-P7: Update my post
        Post p4 = posts.create(USER_A, "Old", "Old body", "General", null);
        Post updated = posts.update(p4.getId(), USER_A, "New", "New body", "Homework", Arrays.asList("updated"));
        TestHelper.assertTrue(
                "New".equals(updated.getTitle()) &&
                "New body".equals(updated.getBody()) &&
                "Homework".equals(updated.getThread()) &&
                updated.getTags().size()==1,
                "TC-P7: Update my post");

        // TC-P8: Reject empty body on update
        Post p5 = posts.create(USER_A, "Title", "Body", "General", null);
        TestHelper.expectException(PostStore.ValidationException.class,
                () -> posts.update(p5.getId(), USER_A, null, "   ", null, null),
                "TC-P8: Reject empty body on update");

        // TC-P9: Cannot update others’ post
        Post p6 = posts.create(USER_A, "Title", "Body", "General", null);
        TestHelper.expectException(PostStore.PermissionException.class,
                () -> posts.update(p6.getId(), USER_B, "Hacked", null, null, null),
                "TC-P9: Cannot update others' post");

        // TC-P10: Delete with confirmation
        Post p7 = posts.create(USER_A, "Title", "Body", "General", null);
        posts.delete(p7.getId(), USER_A, true);
        TestHelper.assertTrue(posts.read(p7.getId()).get().isDeleted(), "TC-P10: Soft delete with confirmation");

        // TC-P11: Cancel deletion
        Post p8 = posts.create(USER_A, "Title", "Body", "General", null);
        posts.delete(p8.getId(), USER_A, false);
        TestHelper.assertTrue(!posts.read(p8.getId()).get().isDeleted(), "TC-P11: Cancel deletion keeps post");

        // TC-S1: Keyword search (subset)
        posts.create(USER_A, "Heap question", "about binary heap", "General", Arrays.asList("heap"));
        posts.create(USER_A, "Graphs", "Dijkstra", "Homework", Arrays.asList("graph"));
        List<Post> results = posts.search("heap", Optional.empty());
        TestHelper.assertTrue(results.size()==1 && "Heap question".equals(results.get(0).getTitle()), "TC-S1: Search 'heap' finds 1");
        TestHelper.assertTrue(results.equals(posts.getSubset()), "TC-S1: Subset reflects last search");

        // TC-S3: Zero-result search
        List<Post> r0 = posts.search("nonexistentphrase", Optional.empty());
        TestHelper.assertTrue(r0.isEmpty(), "TC-S3: Zero-result search");

        // TC-S4: Clear subset
        posts.search("A", Optional.empty());
        TestHelper.assertTrue(!posts.getSubset().isEmpty(), "TC-S4: Subset non-empty before clear");
        posts.clearSubset();
        TestHelper.assertTrue(posts.getSubset().isEmpty(), "TC-S4: Subset cleared");

        // TC-S5: Large dataset (lenient timing check omitted in plain runner)
        int created = 0;
        for (int i = 0; i < 1500; i++) {
            String title2 = (i % 10 == 0) ? "test " + i : "other " + i;
            posts.create(USER_A, title2, "Body " + i, "General", null);
            created++;
        }
        List<Post> rs = posts.search("test", Optional.empty());
        TestHelper.assertTrue(rs.size() >= 120 && rs.size() <= 200, "TC-S5: Large dataset match count plausible ("+rs.size()+")");

        // TC-E1: Error message quality (spot checks)
        try {
            posts.create(USER_A, "", "b", "General", null);
            TestHelper.assertTrue(false, "TC-E1: Title error message thrown");
        } catch (PostStore.ValidationException ex) {
            TestHelper.assertTrue(ex.getMessage().contains("Title"), "TC-E1: Title message mentions 'Title'");
        }

        Post p9 = posts.create(USER_A, "T", "b", "General", null);
        try {
            posts.update(p9.getId(), USER_A, null, "", null, null);
            TestHelper.assertTrue(false, "TC-E1: Body error message thrown");
        } catch (PostStore.ValidationException ex) {
            TestHelper.assertTrue(ex.getMessage().contains("Body"), "TC-E1: Body message mentions 'Body'");
        }

        TestHelper.summary();
    }
}
