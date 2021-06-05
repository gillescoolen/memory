package memory.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import memory.model.Game;
import memory.model.Player;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

public class GameController implements Consumer<Game> {
    @FXML
    private VBox wrapper;
    @FXML
    private GridPane grid;

    private Game game;
    private PlayerController playerOneController;
    private PlayerController playerTwoController;
    private BooleanProperty disabled = new SimpleBooleanProperty(false);

    public GameController() {
        this.game = new Game();

        this.game.observe(this);
    }

    @FXML
    protected void initialize() {
        this.grid.disableProperty().bindBidirectional(this.disabled);

        this.renderPlayers();
        this.renderCards(this.game.generateCards());
    }

    /**
     * Render the grid based off of .mem-like data.
     */
    private void renderCards(List<Integer> data) {
        var row = 1;
        var column = 1;

        this.game.loadCards(data);

        this.grid.getChildren().clear();

        for (int i = 0; i < 36; i++) {
            try {
                var view = this.getClass().getResource("/views/components/card.fxml");
                var loader = new FXMLLoader(view);
                loader.setController(new CardController(this.game.getCards().get(i), game, this));

                this.grid.add(loader.load(), column, row);

                if ((i + 1) % 6 == 0) {
                    row++;
                    column = 1;
                } else {
                    column++;
                }

            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Renders the players.
     */
    private void renderPlayers() {
        if (this.wrapper.getChildren().size() == 4) {
            this.wrapper.getChildren().remove(2, 4);
        }

        this.renderPlayer(this.game.getplayerOne());
        this.renderPlayer(this.game.getplayerTwo());

        this.playerOneController.highlight();
        this.playerTwoController.highlight();
    }

    /**
     * Renders a player.
     * @param player The player to render.
     */
    private void renderPlayer(Player player) {
        var view = this.getClass().getResource("/views/components/player.fxml");
        var loader = new FXMLLoader(view);
        PlayerController controller = new PlayerController(player, this.game);

        if (this.playerOneController == null) this.playerOneController = controller;
        else this.playerTwoController = controller;

        loader.setController(controller);

        try {
            this.wrapper.getChildren().add(loader.load());
        } catch (IOException ignored) { }
    }


    /**
     * Checks if the selected cards form a pair.
     */
    private void checkForPair() {
        this.disableGrid();

        this.game.calculatePair();

        this.playerOneController.highlight();
        this.playerTwoController.highlight();

        this.enableGrid();

        this.checkForGameEnd();
    }

    /**
     * Checks for the game end.
     * If so, shows the ending dialog.
     */
    private void checkForGameEnd() {
        if (!this.game.checkForGameEnd()) return;

        var playerOneBadges = this.game.getplayerOne().getBadges();
        var playerTwoBadges = this.game.getplayerTwo().getBadges();

        var winner = this.game.checkForWinner();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game has ended.");
        alert.setHeaderText(String.format("%s has won the game!", winner));
        alert.setContentText(String.format("Final score: %s-%s, want to play another game?", playerOneBadges.size(), playerTwoBadges.size()));

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yes, no);

        alert.setOnCloseRequest(e -> {
            ButtonType result = alert.getResult();

            if (result.getButtonData() == ButtonBar.ButtonData.YES) this.restart();
            else this.exit();
        });

        alert.show();
    }

    /**
     * Load a .mem file.
     */
    public void load() {
        var fileChooser = new FileChooser();
        var stage = this.grid.getScene().getWindow();

        fileChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/src/main/resources/saves"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Memory Files", "*.mem"));

        var save = fileChooser.showOpenDialog(stage);

        if (save == null) return;

        try {
            var data = this.game.readSaveFileGrid(save);
            this.game.setplayerOne(this.game.readSaveFilePlayer(save, 1));
            this.game.setplayerTwo(this.game.readSaveFilePlayer(save, 2));
            this.game.readSaveFileCurrentPlayer(save);

            if (data.size() != 36) return;

            this.renderCards(data);
            this.renderPlayers();

        } catch (Exception e) {
            this.game.generatePlayers();
            this.renderPlayers();

            var cards = this.game.generateCards();
            this.renderCards(cards);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Save File");
            alert.setHeaderText("Invalid Save File");
            alert.setContentText("Your save might have been corrupted, please use another save file.");
            alert.showAndWait();
        }

    }

    /**
     * Saves the current game.
     */
    public void save() {
        var fileChooser = new FileChooser();
        var stage = this.grid.getScene().getWindow();

        fileChooser.setInitialDirectory(new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/src/main/resources/saves"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Memory Files", "*.mem"));

        var file = fileChooser.showSaveDialog(stage);

        if (file == null) return;

        this.game.writeSaveFile(file);
    }

    /**
     * Closes the window.
     */
    public void exit() {
        var stage = this.grid.getScene().getWindow();
        stage.hide();
    }

    /**
     * Restarts the game.
     */
    public void restart() {
        this.game.generatePlayers();
        this.renderPlayers();

        var cards = this.game.generateCards();
        this.renderCards(cards);
    }

    /**
     * Disables clicking on the grid.
     */
    public void disableGrid() {
        this.disabled.set(true);
    }

    /**
     * Enables clicking on the grid.
     */
    public void enableGrid() {
        this.disabled.set(false);
    }

    public boolean gridIsDisabled() {
        return this.disabled.getValue();
    }

    @Override
    public void accept(Game game) {
        if (game == null) return;

        this.game = game;

        if (this.game.getSelectedCards().size() == 2) this.checkForPair();
    }
}