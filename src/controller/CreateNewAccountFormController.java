package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;

    public Label lblPasswordNotMatched1;
    public Label lblPasswordNotMatched2;
    public TextField txtUserName;
    public TextField txtEmail;
    public Button btnRegister;
    public Label lblID;

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

        autoGenerateID();
    }

    //Auto generate ID
    public void autoGenerateID()  {
        Connection connection = DBConnection.getInstance().getConnection();
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");
            boolean isExist = resultSet.next();
            if(isExist){
                String userID =  resultSet.getString(1);
                userID = userID.substring(1, userID.length());
                int intId = Integer.parseInt(userID);
                intId++;

                if(intId < 10){
                    lblID.setText("U00" + intId);
                }else if(intId < 100){
                    lblID.setText("U0" + intId);
                }else{
                    lblID.setText("U" + intId);
                }
            }else{
                lblID.setText("U001");
            }
        }catch(SQLException throwables){
            throwables.printStackTrace();
        }
    }
}
