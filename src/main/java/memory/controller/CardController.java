package memory.controller;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import memory.model.Card;
import memory.model.Game;

import java.util.function.Consumer;

public class CardController implements Consumer<Card> {
    @FXML
    private ImageView display;

    private Card card;
    private Game game;
    private Image image;
    private GameController gameController;

    public CardController(Card card, Game game, GameController gameController) {
        this.card = card;
        this.game = game;
        this.gameController = gameController;

        this.card.observe(this);
        this.image = new Image(this.card.getCover());
    }

    @FXML
    protected void initialize() {
        if (card.getId() != -1) {
            this.display.setImage(image);
            this.display.setOnMouseClicked(e -> select());
        } else {
            this.display.setDisable(true);
        }
    }

    /**
     * Selects the card if the grid isn't disabled.
     */
    private void select() {
        if (!this.gameController.gridIsDisabled()) this.show();
    }

    /**
     * Shows the image.
     */
    private void show() {
        this.gameController.disableGrid();
        this.display.setDisable(true);
        this.card.show(true);

        var firstHalf = new RotateTransition(Duration.millis(200), this.display);
        var secondHalf = new RotateTransition(Duration.millis(300), this.display);

        firstHalf.setAxis(Rotate.Y_AXIS);
        firstHalf.setFromAngle(360);
        firstHalf.setToAngle(270);
        firstHalf.setOnFinished(event -> {
            secondHalf.play();
            this.display.setImage(new Image(this.card.getUrl()));
        });

        secondHalf.setAxis(Rotate.Y_AXIS);
        secondHalf.setFromAngle(270);
        secondHalf.setToAngle(360);
        secondHalf.setOnFinished(event -> {
            this.game.addSelectedCard(this.card);
            this.gameController.enableGrid();
        });

        firstHalf.play();
    }

    /**
     * Hides the image.
     */
    private void hide() {
        var firstHalf = new RotateTransition(Duration.millis(200), this.display);
        var secondHalf = new RotateTransition(Duration.millis(300), this.display);

        firstHalf.setDelay(Duration.seconds(1));
        firstHalf.setAxis(Rotate.Y_AXIS);
        firstHalf.setFromAngle(360);
        firstHalf.setToAngle(270);
        firstHalf.setOnFinished(event -> {
            secondHalf.play();
            this.display.setImage(new Image(this.card.getCover()));
            this.display.setDisable(false);
        });

        secondHalf.setAxis(Rotate.Y_AXIS);
        secondHalf.setFromAngle(270);
        secondHalf.setToAngle(360);

        firstHalf.play();
    }

    /**
     * Removes the image from the grid.
     */
    private void vanish() {
        this.display.setDisable(true);

        var transition = new FadeTransition(Duration.millis(300), this.display);
        transition.setDelay(Duration.seconds(1));
        transition.setFromValue(1.0);
        transition.setToValue(0.0);
        transition.play();
    }

    @Override
    public void accept(Card card) {
        if (card == null) return;

        if (this.card.getId() == -1) this.vanish();

        if (this.card.isSelected()) this.hide();

        this.card = card;
    }
}
