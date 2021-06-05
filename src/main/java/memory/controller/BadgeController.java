package memory.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import memory.model.Badge;

public class BadgeController {
    @FXML
    private ImageView display;

    private Image image;

    public BadgeController(Badge badge) {
        this.image = new Image(badge.getUrl());
    }

    @FXML
    protected void initialize() {
        this.display.setOpacity(0);
        this.display.setImage(this.image);

        var transition = new FadeTransition(Duration.millis(400), this.display);
        transition.setDelay(Duration.seconds(1));
        transition.setFromValue(0.0);
        transition.setToValue(1.0);
        transition.play();
    }
}
