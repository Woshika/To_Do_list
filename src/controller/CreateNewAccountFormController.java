package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;

    public Label lblPasswordNotMatched1;
    public Label lblPasswordNotMatched2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblID;
    public AnchorPane root;

    public void initialize(){
        setVisibility(false);

        setDisableCommon(true);
    }

    public void setDisableCommon(boolean isDisable){
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
        btnRegister.setDisable(isDisable);
    }
    public void btnRegisterOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        register();
    }

    public void setBorderColor(String color){
        txtNewPassword.setStyle("-fx-border-color: " + color);
        txtConfirmPassword.setStyle("-fx-border-color: " + color);
    }

    public void setVisibility(boolean isVisible){
        lblPasswordNotMatched1.setVisible(isVisible);
        lblPasswordNotMatched2.setVisible(isVisible);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        setDisableCommon(false);

        txtUserName.requestFocus();

        autoGenerateID();
    }

    //Auto generate ID
   public void autoGenerateID() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT id FROM user ORDER BY id DESC limit 1");
        boolean isExist = resultSet.next();

        if(isExist){
           String userID =  resultSet.getString(1);
           userID = userID.substring(1,userID.length());
           int intId = Integer.parseInt(userID);
           intId++;
           if(intId < 10){
               lblID.setText("U00"+ intId);
           }else if(intId < 100){
               lblID.setText("U0"+ intId);
           }else{
               lblID.setText("U"+ intId);
           }
        }else{
            lblID.setText("U001");
        }
    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {
        register();
    }

    public void register() throws SQLException, ClassNotFoundException, IOException {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if(newPassword.equals(confirmPassword)){
            setBorderColor("transparent");

            setVisibility(false);

            String id = lblID.getText();
            String userName = txtUserName.getText();
            String email = txtEmail.getText();


            Connection connection = DBConnection.getInstance().getConnection();

            //sql injection support prepareStatement.It doesn't support createStatement
            //insert value to database from intellij
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user VALUES(?,?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,userName);
            preparedStatement.setObject(3,email);
            preparedStatement.setObject(4,confirmPassword);

            int i = preparedStatement.executeUpdate();

            if(i > 0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Success....!");
                alert.showAndWait();

                //when click alert message ok then log login form
                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                Scene scene = new Scene(parent);

                Stage stage = (Stage) root.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Login");
                stage.centerOnScreen();
            }
        }else{

            setBorderColor("red");

            setVisibility(true);

            txtConfirmPassword.clear();
            txtNewPassword.clear();

            txtNewPassword.requestFocus();
        }
    }
}
