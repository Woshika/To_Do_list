package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.OptionalDouble;

public class ToDoFormController {
    public Label lblTitle;
    public Label lblUserId;
    public AnchorPane root;
    public AnchorPane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM> lstToDo;
    public TextField txtSelectedToDo;
    public Button btnDelete;
    public Button btnUpdate;

    public String selectedID = null;

    public void initialize() throws SQLException, ClassNotFoundException {
        lblTitle.setText("Hi " + LoginFormController.loginUserName + " Welcome to To Do List");
        lblUserId.setText(LoginFormController.loginUserID);

        subRoot.setVisible(false);

        loadList();

        setDisableCommon(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                setDisableCommon(false);

                subRoot.setVisible(false);

                ToDoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();

                if(selectedItem == null){
                    return;
                }
                txtSelectedToDo.setText(selectedItem.getDescription());
                txtSelectedToDo.requestFocus();

                selectedID = selectedItem.getId();
            }
        });
    }

    public void setDisableCommon(boolean isDisable){
        txtSelectedToDo.setDisable(isDisable);
        btnDelete.setDisable(isDisable);
        btnUpdate.setDisable(isDisable);
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
        lstToDo.getSelectionModel().clearSelection();
        txtDescription.requestFocus();
        subRoot.setVisible(true);
        setDisableCommon(true);
        txtSelectedToDo.clear();

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

    public void btnDeleteOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this todo..?",ButtonType.YES,ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM todo WHERE id = ?");
            preparedStatement.setObject(1,selectedID);

            preparedStatement.executeUpdate();
            loadList();
            setDisableCommon(true);
        }

    }

    public void btnUpdateOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE todo SET description = ? WHERE id = ?");
        preparedStatement.setObject(1,txtSelectedToDo.getText());
        preparedStatement.setObject(2,selectedID);

        preparedStatement.executeUpdate();

        txtSelectedToDo.clear();
        loadList();
        setDisableCommon(true);
    }
}
