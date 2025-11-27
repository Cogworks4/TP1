package tests;

import entityClasses.Reply;
import entityClasses.Post;
import store.PostStore;
import store.ReplyStore;
import database.Database;
import java.util.*;
import java.sql.Connection;


/**
 * HW3 – Task 2.3
 * Security-Oriented Tests for ReplyStore
 * 
 * Focused on CWE-287 (Improper Authentication) and CWE-863 (Incorrect Authorization).
 * 
 * These tests validate that ReplyStore requires valid authenticated user IDs
 * and correctly enforces ownership and parent-post integrity.
 * 
 * Author: [Isaac Marx]
 */



public class ReplyStoreSecuritySpec {

    private static final String USER_A = "userA";
    private static final String USER_B = "userB";
    private static Database theDatabase = applicationMain.FoundationsMain.database;		

    public static void main(String[] args) throws Exception {

        // === Setup Fixtures ===
        PostStore posts = new PostStore(new HashSet<>(Arrays.asList("General", "Homework", "Projects")));
        Post parent = posts.create(USER_A, "Security Test Post", "Body", "General", null);
        ReplyStore replies = new ReplyStore(posts.listAll(), theDatabase.getConnection());

        // ---------------------------------------------------------------------
        // CWE-287: Improper Authentication
        // ---------------------------------------------------------------------

        // Reject null authorId on create
        TestHelper.expectException(NullPointerException.class,
            () -> replies.create(parent.getId(), null, "Invalid"),
            "(CWE-287): create() rejects null authorId");

        // Safe behavior for null userId on markReplyRead
        Reply rA = replies.create(parent.getId(), USER_A, "Body for read test");
        replies.markReplyRead(null, rA.getId());
        TestHelper.assertTrue(!rA.isReadBy(null),
            "(CWE-287): null userId does not mark reply as read");

        // Safe behavior for null userId on markReplyUnread
        replies.markReplyUnread(null, rA.getId());
        TestHelper.assertTrue(!rA.isReadBy(null),
            "(CWE-287): null userId does not unmark reply as read");
        
        // Reject empty or whitespace reply body
        TestHelper.expectException(ReplyStore.ValidationException.class,
            () -> replies.create(parent.getId(), USER_A, "   "),
            "(CWE-287): Reject empty or whitespace reply body");
        
        TestHelper.expectException(NoSuchElementException.class,
        	    () -> replies.update(UUID.randomUUID(), USER_A, "should fail"),
        	    "(CWE-287): Cannot update nonexistent reply ID");

        
        

        // ---------------------------------------------------------------------
        // CWE-863: Incorrect Authorization
        // ---------------------------------------------------------------------

        // Only author can update reply
        Reply rB = replies.create(parent.getId(), USER_A, "Owned reply");
        TestHelper.expectException(ReplyStore.PermissionException.class,
            () -> replies.update(rB.getId(), USER_B, "unauthorized edit"),
            "(CWE-863): Only author can update reply");

        // Only author can delete reply
        Reply rC = replies.create(parent.getId(), USER_A, "Owned reply for delete test");
        TestHelper.expectException(ReplyStore.PermissionException.class,
            () -> replies.delete(rC.getId(), USER_B, true),
            "(CWE-863): Only author can delete reply");

        // Author successfully deletes own reply (soft delete)
        Reply rD = replies.create(parent.getId(), USER_A, "Legit delete");
        replies.delete(rD.getId(), USER_A, true);
        TestHelper.assertTrue(replies.read(rD.getId()).get().isDeleted(),
            "(CWE-863): Author can delete own reply");
        
        // Owner successfully updates their reply
        Reply r1 = replies.create(parent.getId(), USER_A, "original text");
        Reply updated = replies.update(r1.getId(), USER_A, "edited text");
        TestHelper.assertTrue("edited text".equals(updated.getBody()),
            "(CWE-863): Author can update their own reply");
        
        
       

        // ---------------------------------------------------------------------
        // Summary
        // ---------------------------------------------------------------------
        TestHelper.summary();
    }
}
