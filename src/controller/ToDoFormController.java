package controller;

import db.DBConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class ToDoFormController {
    public Label lblTitle;
    public Label lblUserId;
    public AnchorPane root;
    public AnchorPane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;

    public void initialize() throws SQLException, ClassNotFoundException {
        lblTitle.setText("Hi " + LoginFormController.loginUserName + " Welcome to To Do List");
        lblUserId.setText(LoginFormController.loginUserID);

        subRoot.setVisible(false);

        loadList();
    }

    public void btnLogOutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to logout...?", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.centerOnScreen();
        }
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        subRoot.setVisible(true);
        txtDescription.requestFocus();
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String id = autoGenerateID();
        String description = txtDescription.getText();
        String userID = lblUserId.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        //sql injection support prepareStatement.It doesn't support createStatement
        //insert value to database from intellij
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO todo VALUES(?,?,?)");
        preparedStatement.setObject(1,id);
        preparedStatement.setObject(2,description);
        preparedStatement.setObject(3,userID);

        int i = preparedStatement.executeUpdate();


        txtDescription.clear();

        subRoot.setVisible(false);

        loadList();
    }

    public String autoGenerateID() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();

        String id = null;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT id FROM todo ORDER BY id DESC limit 1");
        boolean isExist = resultSet.next();

        if(isExist){
            String todoID =  resultSet.getString(1);
            todoID = todoID.substring(1,todoID.length());
            int intId = Integer.parseInt(todoID);
            intId++;
            if(intId < 10){
                id = "T00" + intId;
            }else if(intId < 100){
                id = "T0" + intId;
            }else{
                id = "T" + intId;
            }
        }else{
            id = "T001";
        }
        return id;
    }
    public void loadList() throws SQLException, ClassNotFoundException {

        ObservableList<ToDoTM> items = lstToDo.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM todo WHERE user_id = ?");
        preparedStatement.setObject(1,lblUserId.getText());

        ResultSet resultSet = preparedStatement.executeQuery();

        //Add list to observable list
        while(resultSet.next()){
            String id = resultSet.getString(1);
            String description = resultSet.getString(2);
            String user_id = resultSet.getString(3);

            items.add(new ToDoTM(id, description, user_id));

        }
        lstToDo.refresh();

    }

}
