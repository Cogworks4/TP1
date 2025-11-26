package guiAdminTickets;

import java.sql.SQLException;
import java.util.Arrays;

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
     * Reload the lists from the database.
     */
    protected static void performRefresh() {
        ViewAdminTickets.refreshTicketLists();
    }
}
