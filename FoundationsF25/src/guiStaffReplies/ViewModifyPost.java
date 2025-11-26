package guiStaffReplies;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * <p><b>ViewModifyPost</b> — a small JavaFX dialog window used for
 * modifying an existing discussion Post.</p>
 *
 * <p>This view provides text fields for a new Post title and new tags.
 * When both fields contain text, the "Confirm" button becomes enabled.
 * On submission, a {@link PostResult} containing the new title/tags
 * and a delete flag is passed to the provided callback.</p>
 *
 * <p>The dialog may be opened as modal (blocking its owner) or non-modal,
 * and does not close the parent window upon opening.</p>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * ViewModifyPost.open(stage,
 *                       currentTitle,
 *                       currentTags,
 *                       result -> {
 *                           if (result.delete()) {
 *                               database.deletePost(currentTitle);
 *                           } else {
 *                               database.updatePost(currentTitle,
 *                                                     result.newTitle(),
 *                                                     result.newTags());
 *                           }
 *                       },
 *                       true);
 * }</pre>
 *
 * @author Jacob
 * @version 1.0
 */
public final class ViewModifyPost {

    /**
     * Result of this dialog.
     *
     * @param delete   true if the user chose to delete the Post
     * @param newTitle the new title (only meaningful when delete is false)
     * @param newTags  the new tags  (only meaningful when delete is false)
     */
    public record PostResult(boolean delete, String newTitle, String newTags) {}

    private ViewModifyPost() {
        // utility class; no instances
    }

    /**
     * Opens the "Modify Post" dialog window.
     *
     * @param owner        the owner {@link Stage} of this dialog
     * @param initialTitle initial title text to display (current Post title)
     * @param initialTags  initial tags text to display (current Post tags)
     * @param onResult     a {@link Consumer} that receives the resulting
     *                     {@link PostResult} when the user clicks
     *                     "Confirm" or "Delete". No callback is made on cancel.
     * @param blockOwner   whether the owner window should be blocked while
     *                     the dialog is open
     */
    public static void open(Stage owner,
                            String initialTitle,
                            String initialTags,
                            Consumer<PostResult> onResult,
                            boolean blockOwner) {

        Stage dialog = new Stage();
        dialog.setTitle("Modify Post");
        dialog.initOwner(owner);
        dialog.initModality(blockOwner ? Modality.WINDOW_MODAL : Modality.NONE);

        TextField titleField = new TextField(initialTitle == null ? "" : initialTitle);
        titleField.setPromptText("New Title");

        TextArea tagsArea = new TextArea(initialTags == null ? "" : initialTags);
        tagsArea.setPromptText("New Body");
        tagsArea.setWrapText(true);
        tagsArea.setPrefRowCount(8);

        Button btnConfirm = new Button("Confirm");
        Button btnCancel  = new Button("Cancel");
        Button btnDelete  = new Button("Delete");

        btnConfirm.setDefaultButton(true);
        btnCancel.setCancelButton(true);
        btnConfirm.setDisable(true); // enable after validation

        // Enable Confirm only when both fields have non-blank text
        Runnable validate = () -> btnConfirm.setDisable(
                titleField.getText().trim().isEmpty() ||
                tagsArea.getText().trim().isEmpty()
        );
        titleField.textProperty().addListener((o, a, b) -> validate.run());
        tagsArea.textProperty().addListener((o, a, b) -> validate.run());
        validate.run();

        // Confirm: return new title + tags, delete=false
        btnConfirm.setOnAction(e -> {
            PostResult result = new PostResult(
                    false,
                    titleField.getText().trim(),
                    tagsArea.getText().trim()
            );
            onResult.accept(result);
            dialog.close();
        });

        // Delete: return delete=true; title/tags can be ignored by caller
        btnDelete.setOnAction(e -> {
            PostResult result = new PostResult(true, null, null);
            onResult.accept(result);
            dialog.close();
        });

        // Cancel: no callback
        btnCancel.setOnAction(e -> dialog.close());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.addRow(0, new Label("Title:"), titleField);
        grid.add(new Label("Body:"), 0, 1);
        grid.add(tagsArea, 0, 2, 3, 1);
        grid.addRow(3, btnCancel, btnConfirm, btnDelete);

        dialog.setScene(new Scene(grid, 420, 300));
        dialog.show();
        if (blockOwner) dialog.toFront();
    }
}
