package tests;

import store.PostStore;
import store.ReplyStore;

import java.util.*;

import entityClasses.Post;
import entityClasses.Reply;

public class ReplyStoreSpec {

    private static final String USER_A = "userA";
    private static final String USER_B = "userB";

    public static void main(String[] args) throws Exception {
        PostStore posts = new PostStore(new HashSet<>(Arrays.asList("General", "Homework", "Projects")));
        Post parentPost1 = posts.create(USER_A, "Title", "Body", "General", null);
        ReplyStore replies = new ReplyStore(posts.listAll());

        // TC-R1: Create a reply
        Reply r1 = replies.create(parentPost1.getId(), USER_A, "Thanks!");
        boolean r1ok = r1.getId() != null && parentPost1.getId().equals(r1.getPostId());
        TestHelper.assertTrue(r1ok, "TC-R1: Reply created and linked");
        replies.delete(r1.getId(), USER_A, true);

        // TC-R2: Reject empty reply
        TestHelper.expectException(ReplyStore.ValidationException.class,
                () -> replies.create(parentPost1.getId(), USER_A, "   "),
                "TC-R2: Reject empty reply");

        // TC-R3: No reply to deleted/non-existent post
        posts.delete(parentPost1.getId(), USER_A, true);
        replies.refreshPosts(posts.listAll());
        TestHelper.expectException(ReplyStore.ValidationException.class,
                () -> replies.create(parentPost1.getId(), USER_A, "new reply"),
                "TC-R3: Cannot reply to deleted post");

        // Create a new parent post for the next tests (separate variable, not re-used)
        Post parentPost2 = posts.create(USER_A, "New Title", "New Body", "General", null);
        replies.refreshPosts(posts.listAll());

        // TC-R4: Chronological order
        Reply a = replies.create(parentPost2.getId(), USER_A, "first");
        Thread.sleep(2);
        Reply b = replies.create(parentPost2.getId(), USER_A, "second");
        List<Reply> list = replies.search("", Optional.of(parentPost2.getId()));
        boolean chrono = list.size() >= 2
                && a.getId().equals(list.get(0).getId())
                && b.getId().equals(list.get(1).getId());
        TestHelper.assertTrue(chrono, "TC-R4: Replies in chronological order");

        // TC-R5: Update my reply
        Reply c = replies.create(parentPost2.getId(), USER_A, "original");
        Reply updated = replies.update(c.getId(), USER_A, "edited");
        TestHelper.assertTrue("edited".equals(updated.getBody()), "TC-R5: Update my reply");

        // TC-R6: Cannot edit others' replies
        Reply d = replies.create(parentPost2.getId(), USER_A, "original");
        TestHelper.expectException(ReplyStore.PermissionException.class,
                () -> replies.update(d.getId(), USER_B, "hacked"),
                "TC-R6: Cannot edit others' replies");

        // TC-R7: Delete reply with confirmation
        Reply e = replies.create(parentPost2.getId(), USER_A, "bye");
        replies.delete(e.getId(), USER_A, true);
        TestHelper.assertTrue(replies.read(e.getId()).get().isDeleted(), "TC-R7: Soft delete reply");

        // TC-S2: Keyword search (replies) + subset
        
        replies.create(parentPost2.getId(), USER_A, "Thanks this helps");
        replies.create(parentPost2.getId(), USER_A, "Great explanation");
        List<Reply> sr = replies.search("thanks", Optional.empty());
        TestHelper.assertTrue(sr.size() == 1, "TC-S2: Search 'thanks' finds 1");
        TestHelper.assertTrue(sr.equals(replies.getSubset()), "TC-S2: Subset reflects last search");

        // TC-S3: Zero-result search
        List<Reply> zr = replies.search("nonexistentphrase", Optional.empty());
        TestHelper.assertTrue(zr.isEmpty(), "TC-S3 (replies): Zero-result search");

        // TC-S4: Clear subset
        List<Reply> gr = replies.search("Thanks", Optional.empty());
        TestHelper.assertTrue(!replies.getSubset().isEmpty(), "TC-S4: Subset non-empty before clear");
        replies.clearSubset();
        TestHelper.assertTrue(replies.getSubset().isEmpty(), "TC-S4: Subset cleared");

        TestHelper.summary();
    }
}
