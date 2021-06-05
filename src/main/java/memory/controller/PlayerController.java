package memory.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import memory.model.Badge;
import memory.model.Game;
import memory.model.Player;

import java.io.IOException;
import java.util.function.Consumer;

public class PlayerController implements Consumer<Player> {
    @FXML
    private HBox container;

    @FXML
    private TextField name;

    private Player player;
    private Game game;

    public PlayerController(Player player, Game game) {
        this.player = player;
        this.game = game;

        this.player.observe(this);
    }

    @FXML
    protected void initialize() {
        this.name.textProperty().bindBidirectional(this.player.getName());
        this.highlight();
        this.renderBadges();
    }

    /**
     * Adds a badge to the list when a new one gets added.
     */
    private void addBadge() {
        if (this.container == null) return;
        var badges = this.player.getBadges();
        var children = this.container.getChildren();

        if (children.size() < badges.size() + 1) this.renderBadge(badges.get(badges.size() - 1));
    }

    /**
     * Renders all badges that a player has.
     */
    private void renderBadges() {
        var badges = this.player.getBadges();

        badges.forEach(this::renderBadge);
    }

    /**
     * Renders a badge for the player.
     *
     * @param badge The badge to render.
     */
    private void renderBadge(Badge badge) {
        var view = this.getClass().getResource("/views/components/badge.fxml");
        var loader = new FXMLLoader(view);

        loader.setController(new BadgeController(badge));

        try {
            this.container.getChildren().add(loader.load());
        } catch (IOException ignored) {
        }
    }

    /**
     * Highlights or un-highlights the player field depending on the currentPlayer.
     */
    public void highlight() {
        final Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                Thread.sleep(1000);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            if (this.player == this.game.getCurrentPlayer()) {
                this.name.requestFocus();
                if (!this.name.getStyleClass().contains("current")) this.name.getStyleClass().add("current");
                this.name.setFocusTraversable(true);
            } else {
                this.name.getStyleClass().remove("current");
                this.name.setFocusTraversable(false);
            }
        });

        new Thread(task).start();
    }

    @Override
    public void accept(Player player) {
        if (player == null) return;

        this.player = player;

        this.addBadge();
    }
}
