package guiAdminTickets;

import java.util.function.Consumer;

import guiStudentPosts.ViewAddPost;
import guiStudentPosts.ViewAddPost.PostInput;
import javafx.stage.Stage;

/**
 * <p><b>ViewAddTicket</b> — wrapper dialog for creating admin tickets.</p>
 *
 * <p>This class lives in the guiAdminTickets package but internally
 * delegates to the existing {@link guiStudentPosts.ViewAddPost} dialog.
 * That means all the same input validation and GUI behavior used for
 * normal posts are reused for admin tickets.</p>
 */
public final class ViewAddTicket {

    /** Simple record representing the ticket input (title + description). */
    public record TicketInput(String title, String description) {}

    /**
     * Open the Add Ticket dialog.
     *
     * This simply calls {@code ViewAddPost.open(...)} and adapts its
     * {@code PostInput} into a {@code TicketInput}.
     *
     * @param owner        the parent stage
     * @param initialTitle initial title text
     * @param initialDesc  initial description text
     * @param onSubmit     callback invoked when user presses "Create"
     * @param blockOwner   true to open as WINDOW_MODAL, false otherwise
     */
    public static void open(Stage owner,
                            String initialTitle,
                            String initialDesc,
                            Consumer<TicketInput> onSubmit,
                            boolean blockOwner) {

        ViewAddPost.open(
                owner,
                initialTitle,
                initialDesc,
                (PostInput pi) -> {
                    // Map the existing post fields to our ticket fields
                    onSubmit.accept(new TicketInput(
                            pi.title(),
                            pi.body()
                    ));
                },
                blockOwner
        );
    }
}
