package guiAdminTickets;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import database.Database;
import entityClasses.Post;
import guiAdminTickets.ViewAddTicket.TicketInput;

/**
 * Controller for the Admin Tickets screen.
 *
 * Handles repainting, navigation, and creating new admin tickets.
 */
public class ControllerAdminTickets {

    /**
     * Repaint the window with all content for Admin Tickets.
     */
    protected static void repaintTheWindow() {
        ViewAdminTickets.theRootPane.getChildren().setAll(
                ViewAdminTickets.label_PageTitle,
                ViewAdminTickets.label_UserDetails,
                ViewAdminTickets.label_OpenHeader,
                ViewAdminTickets.label_ClosedHeader,
                ViewAdminTickets.line_Separator1,
                ViewAdminTickets.line_Separator2,
                ViewAdminTickets.list_OpenTickets,
                ViewAdminTickets.list_ClosedTickets,
                ViewAdminTickets.button_Return,
                ViewAdminTickets.button_NewTicket,
                ViewAdminTickets.button_Refresh
        );

        ViewAdminTickets.theStage.setTitle("CSE 360 Foundation Code: Admin Tickets Page");
        ViewAdminTickets.theStage.setScene(ViewAdminTickets.theAdminTicketScene);
        ViewAdminTickets.theStage.show();
    }

    /**
     * Return to the appropriate home page (Admin or Staff).
     */
    protected static void performReturn() {
        if (ViewAdminTickets.theUser.getAdminRole()) {
            guiAdminHome.ViewAdminHome.displayAdminHome(
                    ViewAdminTickets.theStage,
                    ViewAdminTickets.theUser);
        } else if (ViewAdminTickets.theUser.getNewStaff()) {
            guiStaff.ViewStaffHome.displayStaffHome(
                    ViewAdminTickets.theStage,
                    ViewAdminTickets.theUser);
        } else {
            guiStudent.ViewStudentHome.displayStudentHome(
                    ViewAdminTickets.theStage,
                    ViewAdminTickets.theUser);
        }
    }

    /**
     * Open the "New Ticket" dialog (wrapper around the normal post dialog).
     * On submit, create a Post in the "Admin Tickets - Open" thread.
     */
    protected static void performNewTicket() {
        ViewAddTicket.open(
                ViewAdminTickets.theStage,
                "",
                "",
                (TicketInput input) -> {
                    try {
                        Database db = ViewAdminTickets.theDatabase;

                        // Use your existing Post.create(...) factory:
                        //   authorId, title, body, thread, tags
                        Post p = Post.create(
                                ViewAdminTickets.theUser.getUserName(),       // authorId
                                input.title(),                                // title
                                input.description(),                          // body
                                ViewAdminTickets.ADMIN_OPEN_THREAD,           // thread
                                Arrays.asList("admin", "ticket")              // tags
                        );

                        db.writePost(p);
                        ViewAdminTickets.refreshTicketLists();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                },
                true
        );
    }
    
    /**
     * Open a read-only ticket details dialog for an item selected
     * from either the OPEN or CLOSED list.
     *
     * @param ticketListEntry the list entry string (e.g. "author - title")
     * @param fromOpenList    true if this came from the OPEN list (ticket can be resolved)
     */
    protected static void openTicketDetails(String ticketListEntry, boolean fromOpenList) {
        if (ticketListEntry == null || ticketListEntry.isBlank()) {
            return;
        }

        // listPosts() returns "author - title"; we only need the title
        String title = extractTitleFromListEntry(ticketListEntry);

        try {
            Database db = ViewAdminTickets.theDatabase;

            // Reuse existing helper: this also logs that the user has read the post
            UUID postId = db.grabPostId(title, ViewAdminTickets.theUser.getUserName());

            if (postId == null) {
                // Nothing to show
                return;
            }

            String body = db.grabPostBody(postId);
            if (body == null) {
                body = "(No body found for this ticket.)";
            }

            // Open dialog; only allow "Resolve" for OPEN tickets
            ViewTicketDetails.open(
                    ViewAdminTickets.theStage,
                    title,
                    body,
                    fromOpenList,
                    () -> {
                        // This callback runs only if the user clicks "Mark Resolved"
                        try {
                            db.updatePostThread(postId, ViewAdminTickets.ADMIN_CLOSED_THREAD);
                            ViewAdminTickets.refreshTicketLists();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Parse "author - title" into just the "title" portion.
     */
    private static String extractTitleFromListEntry(String entry) {
        int dashIndex = entry.indexOf("- ");
        return (dashIndex != -1) ? entry.substring(dashIndex + 2) : entry;
    }



    /**
     * Reload the lists from the database.
     */
    protected static void performRefresh() {
        ViewAdminTickets.refreshTicketLists();
    }
}
