package guiStudentReplies;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.function.Consumer;

public final class ViewAddReply {

    /** Simple DTO for the result */
    public record ReplyInput(String body) {}

    /**
     * Opens a small window to collect Title + Body and returns via onSubmit.
     * The main window stays open.
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