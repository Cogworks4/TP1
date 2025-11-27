package guiAdminTickets;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Simple read-only dialog for viewing the full contents of an admin ticket.
 *
 * If {@code canResolve} is true, a "Mark Resolved" button is shown and, when
 * clicked, the provided {@code onResolve} callback is executed.
 */
public class ViewTicketDetails {

    private ViewTicketDetails() {
        // utility class; no instances
    }

    /**
     * Open the ticket details dialog.
     *
     * @param owner      parent stage (Admin Tickets stage)
     * @param title      ticket title
     * @param body       ticket body/description
     * @param canResolve whether to show a "Mark Resolved" button
     * @param onResolve  callback to run after the ticket is resolved;
     *                   may be null if no callback is needed
     */
    public static void open(Stage owner,
                            String title,
                            String body,
                            boolean canResolve,
                            Runnable onResolve) {

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Ticket Details");

        // UI elements
        Label titleLabel = new Label("Title:");
        Label titleValue = new Label(title);

        Label bodyLabel = new Label("Description:");
        TextArea bodyArea = new TextArea(body == null ? "" : body);
        bodyArea.setEditable(false);
        bodyArea.setWrapText(true);
        bodyArea.setPrefRowCount(8);

        Button btnClose = new Button("Close");
        Button btnResolve = new Button("Mark Resolved");

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(titleLabel, 0, 0);
        grid.add(titleValue, 1, 0);
        grid.add(bodyLabel, 0, 1);
        grid.add(bodyArea, 0, 2, 2, 1);

        if (canResolve) {
            grid.add(btnResolve, 0, 3);
            grid.add(btnClose, 1, 3);
            GridPane.setMargin(btnResolve, new Insets(10, 0, 0, 0));
            GridPane.setMargin(btnClose, new Insets(10, 0, 0, 0));
        } else {
            grid.add(btnClose, 0, 3);
            GridPane.setColumnSpan(btnClose, 2);
            GridPane.setHalignment(btnClose, javafx.geometry.HPos.CENTER);
            GridPane.setMargin(btnClose, new Insets(10, 0, 0, 0));
        }

        grid.setAlignment(Pos.CENTER);

        // Behavior
        btnClose.setOnAction(e -> dialog.close());

        btnResolve.setOnAction(e -> {
            if (onResolve != null) {
                onResolve.run();
            }
            dialog.close();
        });

        dialog.setScene(new Scene(grid, 450, 300));
        dialog.showAndWait();
    }
}
