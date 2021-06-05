package memory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        var view = getClass().getResource("/views/game.fxml");
        Parent root = FXMLLoader.load(view);
        primaryStage.setTitle("Memory");
        primaryStage.setScene(new Scene(root, 900, 900));
//        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
