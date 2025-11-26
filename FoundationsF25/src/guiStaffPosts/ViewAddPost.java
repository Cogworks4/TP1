package guiStaffPosts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.function.Consumer;

/**
 * <p><b>ViewAddPost</b> — a small JavaFX dialog window used for creating new discussion posts.</p>
 *
 * <p>This view provides text fields for a post title and body. When both fields
 * contain text, the "Create" button becomes enabled. On submission, a
 * {@link PostInput} record containing the entered title and body is passed to
 * the provided callback.</p>
 *
 * <p>The dialog may be opened as modal (blocking its owner) or non-modal, and
 * does not close the parent window upon opening.</p>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * ViewAddPost.open(stage, "", "", postInput -> {
 *     database.writePost(new Post(postInput.title(), postInput.body()));
 * }, true);
 * }</pre>
 *
 * @author Jacob
 * @version 1.0
 */
public final class ViewAddPost {

    /**
     * Simple record type representing the data entered by the user.
     *
     * @param title the post title entered by the user
     * @param body  the main body text of the post
     */
    public record PostInput(String title, String body) {}

    /**
     * Opens the "Add Post" dialog window.
     *
     * @param owner        the owner {@link Stage} of this dialog
     * @param initialTitle optional initial title text to display
     * @param initialBody  optional initial body text to display
     * @param onSubmit     a {@link Consumer} that receives the resulting {@link PostInput}
     *                     when the user clicks "Create"
     * @param blockOwner   whether the owner window should be blocked while the dialog is open
     */
    public static void open(Stage owner,
                            String initialTitle,
                            String initialBody,
                            Consumer<PostInput> onSubmit,
                            boolean blockOwner) {

        Stage dialog = new Stage();
        dialog.setTitle("New Post");
        dialog.initOwner(owner);
        dialog.initModality(blockOwner ? Modality.WINDOW_MODAL : Modality.NONE);

        TextField titleField = new TextField(initialTitle == null ? "" : initialTitle);
        titleField.setPromptText("Title");

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
                titleField.getText().trim().isEmpty() ||
                bodyArea.getText().trim().isEmpty()
        );
        titleField.textProperty().addListener((o, a, b) -> validate.run());
        bodyArea.textProperty().addListener((o, a, b) -> validate.run());
        validate.run();

        btnCreate.setOnAction(e -> {
            onSubmit.accept(new PostInput(
                    titleField.getText().trim(),
                    bodyArea.getText().trim()
            ));
            dialog.close();
        });
        btnCancel.setOnAction(e -> dialog.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, new Label("Title:"), titleField);
        grid.add(new Label("Body:"), 0, 1);
        grid.add(bodyArea, 0, 2, 2, 1);
        grid.addRow(3, btnCancel, btnCreate);

        dialog.setScene(new Scene(grid, 420, 300));
        dialog.show();          // keeps owner open
        if (blockOwner) dialog.toFront();
    }
}