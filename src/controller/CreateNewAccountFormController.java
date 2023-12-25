package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.SQLException;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;

    public Label lblPasswordNotMatched1;
    public Label lblPasswordNotMatched2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;

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
    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if(newPassword.equals(confirmPassword)){
            setBorderColor("transparent");

            setVisibility(false);

        }else{

            setBorderColor("red");

            setVisibility(true);

            txtConfirmPassword.clear();
            txtNewPassword.clear();

            txtNewPassword.requestFocus();
        }
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

        DBConnection object = DBConnection.getInstance();
        Connection connection = object.getConnection();

        System.out.println(connection);
    }
}
