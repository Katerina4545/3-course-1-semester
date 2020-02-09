package com.company;

import com.company.View.MainGui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("View/PageGUI/main.fxml"));
        stage.setTitle("Time to live");
        Scene scene = new Scene(root, 1275, 775);

        MainGui mainGui = new MainGui();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
