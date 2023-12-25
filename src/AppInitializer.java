import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("view/LoginForm.fxml"));

        Scene scene=new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.centerOnScreen();

        //To change the logo in every form header
        Image img = new Image("images/icon/icon1.png");    //ctrl + space - twice press
        primaryStage.getIcons().add(img);

        primaryStage.show();
    }
}
