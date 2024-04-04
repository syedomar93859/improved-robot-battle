package rw.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import rw.battle.Battle;
import rw.util.Reader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/** Syed Omar
 *  March 20, 2024
 *  T12
 */
public class MainController {

    //Store the data of editor
    private Battle battle;
@FXML
    private TextField column;
@FXML
    private TextField row;

    @FXML
    private TextField predaConSymbol;

    @FXML
    private TextField predaConName;

    @FXML
    private TextField predaConHealth;

    @FXML
    private TextField maximalSymbol;

    @FXML
    private TextField maximalName;

    @FXML
    private TextField maximalHealth;

    @FXML
    private TextField maximalWeaponStrength;

    @FXML
    private TextField maximalArmourStrength;

    @FXML
    private ToggleButton detailsButton;

    private String symbol;

    @FXML
    private ToggleButton battleMap;

    @FXML
    private MenuItem saveButton;

    @FXML
    private TextArea details;

    @FXML
    private Text maximal_health;


    @FXML
    private RadioButton maximalButton;


    @FXML
    private RadioButton predaConButton;

    @FXML
    private TextField inputSymbol;


    @FXML
    private GridPane gridPane;

    @FXML
    private Label leftStatus;

    @FXML
    private Label rightStatus;

    @FXML
    private Text maximal_name;

    @FXML
    private Text maximal_symbol;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

    private ToggleGroup group = new ToggleGroup();

    @FXML
    private ChoiceBox<String> Weapon_input;

    /**
     * Set up the window state
     * gets all the input and texts, adds some options as well
     */
    @FXML
    public void initialize() {
        // Initialize your components or variables if necessary.
        row.setOnKeyTyped(event -> clearGrid());
        column.setOnKeyTyped(event -> clearGrid());

        predaConButton.setToggleGroup(group);
        maximalButton.setToggleGroup(group);
        detailsButton.setOnAction(this::onViewDetailsPressed);

        // adds the claws, laser and teeth to the weapon options
        Weapon_input.getItems().addAll("Claw(2)", "Laser(3)", "Teeth(4)");

        // sets claw as the first val
        Weapon_input.setValue("Claw(2)");

        // gets the first value for symbol
        symbol = inputSymbol.getText();

        // sees which changes the toggle group does
        group.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selectedRadioButton = (RadioButton) newToggle;
                symbol = selectedRadioButton.getText();
                // Update currentSymbol based on the selected RadioButton
                if (predaConButton.isSelected()) {
                    symbol = inputSymbol.getText();
                    leftStatus.setText("Currently adding PredaCons");
                    rightStatus.setText("Add / create to make a battle");
                } else if (maximalButton.isSelected()) {
                    symbol = maximalSymbol.getText();
                    leftStatus.setText("Currently adding Maximal");
                    rightStatus.setText("Add / create to make a battle");
                }
            }
        });
    }

    /**
     * clears the grid
     * shows a txt message on the left status
     */
    private void clearGrid() {
        gridPane.getChildren().clear();
        leftStatus.setText("Cleared the grid");
    }

    /**
     * clears the grid of row input
     * @param event
     */
    @FXML
    void RowInput(ActionEvent event) {
        gridCreation();
    }

    /**
     * clears the column input of the grid
     * @param event
     */
    @FXML
    void ColumnInput(ActionEvent event) {
        gridCreation();
    }

    @FXML
    void saveAction(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
                int rows = gridPane.getColumnConstraints().size();
                int columns = gridPane.getRowConstraints().size();

                bw.write(rows + "\n");
                bw.write(columns + "\n");

                // Iterate through GridPane children
                for (Node child : gridPane.getChildren()) {
                    // Get row and column indices
                    Integer rowIndex = GridPane.getRowIndex(child);
                    Integer columnIndex = GridPane.getColumnIndex(child);

                    if (rowIndex == null) rowIndex = 0;
                    if (columnIndex == null) columnIndex = 0;
                    // Write the cell's details to the file
                    bw.write(rowIndex + "," + columnIndex + "\n");
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    @FXML
    void loadAction(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Loading");
        fc.setInitialDirectory(new File("."));
        fc.setInitialFileName("battle.txt");
        File file = fc.showOpenDialog(new Stage());
        loadAction(file);
    }


    @FXML
    void loadAction(File file){
        Reader.loadBattle(file);
    }

    /**
     * creates a grid for the view in the gui
     * gets the row and column and adds walls
     * also clears the constraints of row and column
     * add right and left status to let the user know of the status
     */
    @FXML
    void gridCreation() {
        try {
            // adds the walls in our grid
            int rows = Integer.parseInt(row.getText().trim()) + 2;
            int columns = Integer.parseInt(column.getText().trim()) + 2;

            // clears all the grid of the previous attempt
            gridPane.getChildren().clear();
            // clears all the grid row of the previous attempt
            gridPane.getRowConstraints().clear();
            // clears all the grid column of the previous attempt
            gridPane.getColumnConstraints().clear();

            // adds a new column constraint with an equal length
            for (int col = 0; col < columns; col++) {
                ColumnConstraints columnConstraints = new ColumnConstraints();
                // sets the size
                columnConstraints.setPercentWidth(100.0 / columns);
                gridPane.getColumnConstraints().add(columnConstraints);
            }

            // adds a new column constraint with an equal length
            for (int row = 0; row < rows; row++) {
                RowConstraints rowConstraints = new RowConstraints();
                // sets the size
                rowConstraints.setPercentHeight(100.0 / rows);
                gridPane.getRowConstraints().add(rowConstraints);
            }

            // this is creating buttons for our grid
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    Button cellButton = new Button();
                    cellButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                    // Set wall cells
                    if (isWallCell(row, col, rows, columns)) {
                        cellButton.setText("#");
                    } else {
                        cellButton.setText(".");
                    }
                    gridPane.add(cellButton, col, row);
                }
            }
            // sets the status of our grid in green or red depending on if it is an error or not
            leftStatus.setTextFill(Color.GREEN);
            leftStatus.setText("Map created with " + (rows - 2) + "x" + (columns - 2) + " cells (plus walls).");
        } catch (NumberFormatException e) {
            leftStatus.setTextFill(Color.RED);
            leftStatus.setText("Please enter positive integer values for rows and columns.");
        }
    }

    @FXML
    void BattleMapButton(ActionEvent event) {
        try {
            int rows = Integer.parseInt(row.getText().trim()) + 2; // Including walls
            int columns = Integer.parseInt(column.getText().trim()) + 2; // Including walls

            if (rows <= 2 || columns <= 2) { // Basic validation for positive input
                throw new NumberFormatException("Rows and columns must be positive.");
            }
            // Clear any existing grid
            gridPane.getChildren().clear();

            // Set up row and column constraints for equal sizing
            setUpRowConstraints(rows);
            setUpColumnConstraints(columns);

            // Populate grid with buttons
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    Button cellButton = new Button();
                    cellButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Allow button to fill cell

                    // Set wall cells or default cell character
                    if (isWallCell(row, col, rows, columns)) {
                        cellButton.setText("#");
                    } else {
                        cellButton.setText(".");
                    }

                    // Configure button click behavior
                    cellButton.setOnMouseClicked(e -> {
                        // Ensure changes are made only to non-wall cells
                        if (!cellButton.getText().equals("#")) {
                            if (e.getButton() == MouseButton.PRIMARY) {
                                // Apply symbol based on selected radio button, default to "."
                                String symbol = getSelectedSymbol();
                                if (symbol.isEmpty()) {
                                    // If the symbol is empty, set the button's text to "."
                                    cellButton.setText(".");
                                } else {
                                    // If the symbol is not empty, set the button's text to the symbol's value
                                    cellButton.setText(symbol);
                                }
                            } else if (e.getButton() == MouseButton.SECONDARY) {
                                // On right-click, reset to "."
                                cellButton.setText(".");
                            }
                        }
                    });

                    gridPane.add(cellButton, col, row);
                }
            }
            rightStatus.setTextFill(Color.GREEN);
            rightStatus.setText("Battle Drawn!");
            leftStatus.setTextFill(Color.GREEN);
            leftStatus.setText("Map created with " + (rows - 2) + "x" + (columns - 2) + " cells (plus walls).");
        } catch (NumberFormatException e) {
            leftStatus.setTextFill(Color.RED);
            leftStatus.setText("Please enter positive integer values for rows and columns.");
        }
    }

    /**
     * gets the specific coordinates that is a wall cell
     * ///    a helper function    ///
     */
    private boolean isWallCell(int row, int col, int totalRows, int totalColumns) {
        return row == 0 || col == 0 || row == totalRows - 1 || col == totalColumns - 1;
    }

    /**
     * gets the type of symbol that is selected by the radio buttons
     * we have 2 types of radio buttons, predaCon and maximal
     */
    private String getSelectedSymbol() {
        if (predaConButton.isSelected()) {
            return inputSymbol.getText();
        } else if (maximalButton.isSelected()) {
            return maximalSymbol.getText();
        }
        return "";
    }

    /**
     * Sets up row  constraints on the grid
     * makes sure they are equal sizes
     * @param rows , rowns in the grid
     */
    private void setUpRowConstraints(int rows) {
        gridPane.getRowConstraints().clear();
        for (int i = 0; i < rows; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(100.0 / rows);
            gridPane.getRowConstraints().add(rc);
        }
    }

    /**
     * Sets up column constraints on the grid
     * @param columns, column in the map
     */
    private void setUpColumnConstraints(int columns) {
        gridPane.getColumnConstraints().clear();
        for (int i = 0; i < columns; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / columns);
            gridPane.getColumnConstraints().add(cc);
        }
    }

    /**
     * This method give details about either the  predaCon or Maximal
     * gives general information of the input the user entered
     * @param event
     */
    @FXML
    private void onViewDetailsPressed(ActionEvent event) {
        try {
            if (predaConButton.isSelected()) {
                // if Predacon button is selected, get the health
                int health = Integer.parseInt(predaConHealth.getText());

                // BOTH SHOULD BE in letters and not numeric
                if (isNumeric(inputSymbol.getText()) || isNumeric(predaConName.getText())) {
                    throw new NumberFormatException("Symbol and Name should not be numeric.");
                }

                details.setText("Symbol: " + inputSymbol.getText() + "\n" +
                        "Name: " + predaConName.getText() + "\n" +
                        "Health: " + health + "\n" +
                        "Weapon: " + Weapon_input.getValue());
            } else if (maximalButton.isSelected()) {
                // checks if all 3 are valid integers
                int weaponStrength = Integer.parseInt(maximalWeaponStrength.getText());
                int health = Integer.parseInt(maximalHealth.getText());
                int armorStrength = Integer.parseInt(maximalArmourStrength.getText());

                // checks if Symbol and Name are not integers
                if (isNumeric(maximalSymbol.getText()) || isNumeric(maximalName.getText())) {
                    throw new NumberFormatException("Symbol and name should not be numeric.");
                }

                details.setText("Symbol: " + maximalSymbol.getText() + "\n" +
                        "Name: " + maximalName.getText() + "\n" +
                        "Health: " + health + "\n" +
                        "Weapon Strength: " + weaponStrength + "\n" +
                        "Armor: " + armorStrength);
            } else {
                details.setText("Select a character type to view details.");
            }
        } catch (NumberFormatException e) {
            details.setText("Error: " + e.getMessage());
        }
    }

    /**
     *Checks to see if the value in a string is a number
     * @param str
     * @return
     */
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Considers the alert of the menu help option
     * @param event
     */
    @FXML
    void Help(ActionEvent event){
        // Alert will INFORMATION, because the info is general
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // sets title and header of the opened window
        alert.setTitle("About");
        alert.setHeaderText("Message");
        alert.setContentText(
                // information
                "Author: Saif ullah Anwar" +
                        "\nEmail: saif.anwar1@ucalgary.ca" +
                        "\nVersion: v1.0" +
                        "\nThis is a Battle editor for PredaCons versus Maximals"
        );
        alert.show();
    }

    /**
     * This method will exit the GUI
     * @param event
     */
    @FXML
    void quit(ActionEvent event){
        // if the user pressed quit option in the menu option, closes the gui and window
        System.exit(1);
    }
}