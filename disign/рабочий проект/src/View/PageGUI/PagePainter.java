package com.company.View.PageGUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;


public class PagePainter
{

    private ObservableList<String> observableList = FXCollections.observableArrayList("Добавить вкладку",
            "Удалить вкладку");

    private ObservableList<String> observableListPage = FXCollections.observableArrayList("Переименовать вкладку",
            "Поменять расположение блока", "Добавить блок", "Удалить блок");

    private ObservableList<String> observableListBlock = FXCollections.observableArrayList("Переименовать блок",
            "Изменить контент блока", "Изменить размер блока");
    @FXML
    private ComboBox<String> optionButton;

    @FXML
    private ComboBox<String> refactorPage;

    @FXML
    private ComboBox<String> refactorBlock;

    public PagePainter()
    {
        optionButton = new ComboBox<>(observableList);
        refactorPage = new ComboBox<>(observableListPage);
        refactorBlock = new ComboBox<>(observableListBlock);
    }


    @FXML
    public void changePage(ActionEvent event)
    {

    }

    @FXML
    public void changeBlock(ActionEvent event)
    {

    }

    @FXML
    public void changeOption(ActionEvent event)
    {
        if("Добавить вкладку".equals(optionButton.getValue()))
        {
            //optionButton.setValue("Опции");
            TextInputDialog dialog = new TextInputDialog();

            dialog.setHeaderText("Введите название вкладки");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(name -> {

            });
        }
    }

    @FXML
    public void initialize()
    {
        optionButton.getItems().addAll(observableList);
        refactorPage.getItems().addAll(observableListPage);
        refactorBlock.getItems().addAll(observableListBlock);

    }

}
