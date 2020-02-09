package com.company.View.PageGUI;

import com.company.BlockModel.AbstractBlock;
import com.company.BlockModel.BlockType;
import com.company.BlockModel.Factory;
import com.company.Service;
import com.company.pageModel.Page;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class PagePainter
{

    private List<String> pageNames;
    private List<Button> pageButtons;
    private List<Page> pages;
    private List<String> blockTypes;
    private Factory factory;
    private Service service;


    public PagePainter()
    {
        optionButton = new ComboBox<>(observableList);
        refactorPage = new ComboBox<>(observableListPage);
        refactorBlock = new ComboBox<>(observableListBlock);
    }


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

    @FXML
    private VBox vbox;


    @FXML
    private GridPane gridPane;


    public void addBlock()
    {
        ChoiceDialog<String> dialogAddBlock = new ChoiceDialog<String>(blockTypes.get(0), blockTypes);

            dialogAddBlock.setHeaderText("Выберите блок:");
            dialogAddBlock.setContentText("Блок:");

            Optional<String> resultType = dialogAddBlock.showAndWait();

            resultType.ifPresent(book ->
            {
                TextInputDialog chosen = new TextInputDialog();

                chosen.setHeaderText("Введите название");

                Optional<String> resultName = chosen.showAndWait();

                resultName.ifPresent(name ->
                {
                    if ("Список".equals(resultType.get()))
                    {
                        TextArea list = new TextArea();
                        Label nameList = new Label(name);

                        GridPane.setMargin(nameList, new Insets(0, 0, 0, 0));
                        GridPane.setMargin(list, new Insets(0, 0, 0, 0));

                        gridPane.add(nameList, 0, 0);
                        gridPane.add(list, 0, 1);
                        AbstractBlock listBlock = factory.getBlock(BlockType.list);
                    }
                    else if("Календарь".equals(resultType.get()))
                    {

                    }
                });
            });
    }

    @FXML
    public void changePage(ActionEvent event)
    {
        if ("Переименовать вкладку".equals(refactorPage.getValue()))
        {
            if (!pageNames.isEmpty())
            {
                ChoiceDialog<String> dialog = new ChoiceDialog<String>(pageNames.get(0), pageNames);

                dialog.setHeaderText("Выберите вкладку для изменения:");
                dialog.setContentText("Вкладка:");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(book ->
                {
                    TextInputDialog chosen = new TextInputDialog();

                    chosen.setHeaderText("Введите новое название");

                    Optional<String> resultName = chosen.showAndWait();

                    resultName.ifPresent(name ->
                    {
                        pageNames.remove(result.get());
                        pageNames.add(resultName.get());
                        System.out.println(pageNames.get(0));

                        for (Button button: pageButtons)
                        {
                            if (result.get().equals(button.getText()))
                            {
                                button.setText(resultName.get());
                            }
                        }
                    });
                });
            }
        }
        if("Добавить блок".equals(refactorPage.getValue()))
        {
            addBlock();
        }
    }

    @FXML
    public void changeBlock(ActionEvent event)
    {

    }

    private Button buttonPage;

    @FXML
    public void changeOption(ActionEvent event)
    {
        if ("Добавить вкладку".equals(optionButton.getValue()))
        {
            TextInputDialog dialog = new TextInputDialog();

            dialog.setHeaderText("Введите название вкладки");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(name ->
            {
                Page page = new Page(name);
                pages.add(page);
                pageNames.add(page.getName());
                System.out.println(name);
                buttonPage = new Button(name);
                pageButtons.add(buttonPage);
                vbox.getChildren().add(buttonPage);
                service.addNewPage(page);
            });
        }
        else
        {
            if (!pageNames.isEmpty())
            {
                ChoiceDialog<String> dialog = new ChoiceDialog<String>(pageNames.get(0), pageNames);

                dialog.setHeaderText("Выберите вкладку для удаления:");
                dialog.setContentText("Вкладка:");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(book ->
                {
                    pageNames.remove(result.get());
                    vbox.getChildren().remove(pageButtons.get(0));
                });
            }
        }
    }

    @FXML
    public void initialize()
    {
        optionButton.getItems().addAll(observableList);
        refactorPage.getItems().addAll(observableListPage);
        refactorBlock.getItems().addAll(observableListBlock);
        pageNames = new LinkedList<>();
        pageButtons = new LinkedList<>();
        pages = new LinkedList<>();
        blockTypes = new LinkedList<>();
        blockTypes.add("Список");
        blockTypes.add("Календарь");
        factory = new Factory();
        try
        {
            service = new Service("C:/Users/Катерина/Desktop/programming/сети/time-to-live/src/example.json");
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
