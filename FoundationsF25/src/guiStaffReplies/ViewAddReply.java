package guiStaffReplies;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.function.Consumer;

/**
 * <p><b>ViewAddReply</b> — a lightweight JavaFX dialog for submitting replies to existing posts.</p>
 *
 * <p>This view provides a text area for entering reply text.
 * The "Create" button is enabled only when the body contains text.
 * When the user submits, a {@link ReplyInput} record containing the body text
 * is sent to the provided callback handler.</p>
 *
 * <p>If the callback throws an exception (e.g. due to validation or database errors),
 * the dialog remains open and displays an error alert to the user.</p>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * ViewAddReply.open(stage, "", replyInput -> {
 *     database.writeReply(new Reply(postId, currentUser, replyInput.body()));
 * }, false);
 * }</pre>
 *
 * @author Jacob
 * @version 1.0
 */
public final class ViewAddReply {

    /**
     * Simple record type representing reply input.
     *
     * @param body the reply text body entered by the user
     */
    public record ReplyInput(String body) {}

    /**
     * Opens the "Add Reply" dialog window.
     *
     * @param owner       the owner {@link Stage} of this dialog
     * @param initialBody optional initial body text to display
     * @param onSubmit    a {@link Consumer} that receives the resulting {@link ReplyInput}
     *                    when the user clicks "Create"
     * @param blockOwner  whether the owner window should be blocked while this dialog is open
     */
    public static void open(Stage owner,
                            String initialBody,
                            Consumer<ReplyInput> onSubmit,
                            boolean blockOwner) {

        Stage dialog = new Stage();
        dialog.setTitle("New Reply");
        dialog.initOwner(owner);
        dialog.initModality(blockOwner ? Modality.WINDOW_MODAL : Modality.NONE);

        TextArea bodyArea = new TextArea(initialBody == null ? "" : initialBody);
        bodyArea.setPromptText("Body");
        bodyArea.setWrapText(true);
        bodyArea.setPrefRowCount(8);

        Button btnCreate = new Button("Create");
        Button btnCancel = new Button("Cancel");
        btnCreate.setDefaultButton(true);
        btnCancel.setCancelButton(true);
        btnCreate.setDisable(true); // enable after validation

        // Enable create only when both fields have text
        Runnable validate = () -> btnCreate.setDisable(
                bodyArea.getText().trim().isEmpty()
        );
        bodyArea.textProperty().addListener((o, a, b) -> validate.run());
        validate.run();

        btnCreate.setOnAction(e -> {
            try {
                onSubmit.accept(new ReplyInput(bodyArea.getText().trim()));
                dialog.close();                // close only if submit didn't throw
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, 
                          "Couldn’t save reply:\n" + ex.getMessage()).showAndWait();
                ex.printStackTrace();
            }
        });
        btnCancel.setOnAction(e -> dialog.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(new Label("Body:"), 0, 1);
        grid.add(bodyArea, 0, 2, 2, 1);
        grid.addRow(3, btnCancel, btnCreate);

        dialog.setScene(new Scene(grid, 420, 300));
        dialog.show();          // keeps owner open
        if (blockOwner) dialog.toFront();
    }
}