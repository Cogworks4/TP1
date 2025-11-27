package guiAdminTickets;

import java.sql.SQLException;
import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX view for listing admin tickets (open and closed) for staff and admins.
 *
 * Tickets are stored as Posts in two special threads:
 *   "Admin Tickets - Open"   (open requests)
 *   "Admin Tickets - Closed" (closed requests)
 *
 * Only Admins and Staff are allowed to view this page.
 */
public class ViewAdminTickets {

    /** Thread names used to store admin tickets as posts. */
    public static final String ADMIN_OPEN_THREAD   = "Admin Tickets - Open";
    public static final String ADMIN_CLOSED_THREAD = "Admin Tickets - Closed";

    // Window size
    private static double width  = FoundationsMain.WINDOW_WIDTH;
    private static double height = FoundationsMain.WINDOW_HEIGHT;

    // Labels
    protected static Label label_PageTitle   = new Label();
    protected static Label label_UserDetails = new Label();
    protected static Label label_OpenHeader  = new Label("Open Admin Requests");
    protected static Label label_ClosedHeader = new Label("Closed Admin Requests");

    // Lines
    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
    protected static Line line_Separator2 = new Line(20, 525, width - 20, 525);

    // Lists
    protected static ListView<String> list_OpenTickets   = new ListView<>();
    protected static ListView<String> list_ClosedTickets = new ListView<>();

    // Bottom buttons
    protected static Button button_Return    = new Button("Return");
    protected static Button button_NewTicket = new Button("New Ticket");
    protected static Button button_Refresh   = new Button("Refresh");

    // References
    protected static Stage    theStage;
    protected static Pane     theRootPane;
    protected static User     theUser;
    protected static Database theDatabase;

    public static Scene theAdminTicketScene = null;

    /**
     * Entry point: show the Admin Tickets page.
     *
     * Only Admins and Staff are allowed to see this page.
     */
    public static void displayAdminTickets(Stage ps, User user) {
        theStage = ps;
        theUser  = user;

        // Only admins and staff can see this page
        if (!theUser.getAdminRole() && !theUser.getNewStaff()) {
            guiStudent.ViewStudentHome.displayStudentHome(theStage, theUser);
            return;
        }

        // Get the shared Database instance
        theDatabase = FoundationsMain.database;

        if (theAdminTicketScene == null) {
            new ViewAdminTickets(); // build UI once
        }

        populateTicketLists();
        ControllerAdminTickets.repaintTheWindow();
    }

    /**
     * Constructor sets up the static UI widgets (labels, lists, buttons).
     */
    public ViewAdminTickets() {
        theRootPane = new Pane();
        theAdminTicketScene = new Scene(theRootPane, width, height);

        // Page title
        label_PageTitle.setText("Admin Tickets");
        setupLabelUI(label_PageTitle, "Arial", 24, width, Pos.CENTER, 0, 10);

        // User details: User: username   Roles: ...
        StringBuilder roleSummary = new StringBuilder();
        if (theUser.getAdminRole())  roleSummary.append("Admin ");
        if (theUser.getNewStaff())   roleSummary.append("Staff ");
        if (theUser.getNewStudent()) roleSummary.append("Student ");

        label_UserDetails.setText(
                "User: " + theUser.getUserName() +
                "   Roles: " + roleSummary.toString().trim()
        );
        setupLabelUI(label_UserDetails, "Arial", 14, width - 40, Pos.BASELINE_LEFT, 20, 55);

        // Column headers
        setupLabelUI(label_OpenHeader, "Arial", 18, (width - 60) / 2, Pos.CENTER, 20, 100);
        setupLabelUI(label_ClosedHeader, "Arial", 18, (width - 60) / 2, Pos.CENTER,
                40 + (width - 60) / 2, 100);

        double columnWidth = (width - 60) / 2;
        double listTop     = line_Separator1.getStartY() + 20;
        double listHeight  = line_Separator2.getStartY() - listTop - 10;

        list_OpenTickets.setLayoutX(20);
        list_OpenTickets.setLayoutY(listTop);
        list_OpenTickets.setPrefWidth(columnWidth);
        list_OpenTickets.setPrefHeight(listHeight);
        list_OpenTickets.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 14;");

        list_ClosedTickets.setLayoutX(40 + columnWidth);
        list_ClosedTickets.setLayoutY(listTop);
        list_ClosedTickets.setPrefWidth(columnWidth);
        list_ClosedTickets.setPrefHeight(listHeight);
        list_ClosedTickets.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 14;");
        
        // Double-click on an OPEN ticket to view details (and optionally resolve)
        list_OpenTickets.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selected = list_OpenTickets.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    ControllerAdminTickets.openTicketDetails(selected, true);
                }
            }
        });

        // Double-click on a CLOSED ticket to view details (read-only)
        list_ClosedTickets.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selected = list_ClosedTickets.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    ControllerAdminTickets.openTicketDetails(selected, false);
                }
            }
        });


        // Buttons at bottom
        setupButtonUI(button_Return, "Dialog", 18, 200, Pos.CENTER, 40, 540);
        button_Return.setOnAction(e -> ControllerAdminTickets.performReturn());

        setupButtonUI(button_NewTicket, "Dialog", 18, 200, Pos.CENTER, 300, 540);
        button_NewTicket.setOnAction(e -> ControllerAdminTickets.performNewTicket());

        setupButtonUI(button_Refresh, "Dialog", 18, 200, Pos.CENTER, 560, 540);
        button_Refresh.setOnAction(e -> ControllerAdminTickets.performRefresh());
    }

    /**
     * Pulls posts from the two special threads and fills the list views.
     */
    protected static void populateTicketLists() {
        if (!theRootPane.getChildren().contains(list_OpenTickets)) {
            theRootPane.getChildren().add(list_OpenTickets);
        }
        if (!theRootPane.getChildren().contains(list_ClosedTickets)) {
            theRootPane.getChildren().add(list_ClosedTickets);
        }

        try {
            List<String> openPosts   = theDatabase.listPosts(ADMIN_OPEN_THREAD);
            List<String> closedPosts = theDatabase.listPosts(ADMIN_CLOSED_THREAD);

            ObservableList<String> openItems =
                    FXCollections.observableArrayList(openPosts);
            ObservableList<String> closedItems =
                    FXCollections.observableArrayList(closedPosts);

            list_OpenTickets.setItems(openItems);
            list_ClosedTickets.setItems(closedItems);
        } catch (SQLException e) {
            e.printStackTrace();
            list_OpenTickets.setItems(FXCollections.observableArrayList(
                    "** ERROR ** loading open tickets"));
            list_ClosedTickets.setItems(FXCollections.observableArrayList(
                    "** ERROR ** loading closed tickets"));
        }
    }

    /**
     * Convenience method for refreshing after changes.
     */
    protected static void refreshTicketLists() {
        populateTicketLists();
    }

    /* ==== small helper methods for labels/buttons ==== */

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
                                     double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
                                      double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
