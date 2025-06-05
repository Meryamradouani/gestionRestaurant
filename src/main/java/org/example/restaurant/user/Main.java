package org.example.restaurant.user;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/org/example/restaurant/user/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1040, 605);
        stage.setTitle("Welcome To DARNA !");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}