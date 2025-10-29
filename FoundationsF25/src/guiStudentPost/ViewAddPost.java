package guiStudentPost;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.function.Consumer;

public final class ViewAddPost {

    /** Simple DTO for the result */
    public record PostInput(String title, String body) {}

    /**
     * Opens a small window to collect Title + Body and returns via onSubmit.
     * The main window stays open.
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