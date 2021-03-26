package controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public class Controller implements Initializable {

    @FXML
    public TextField percentTxtFld;


    @FXML
    private TextArea areaLog;

    @FXML
    private Button btnSelectFile;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField txtFldShowPathFile;

    @FXML
    ChoiceBox<String> choiceBoxMarketPlace;

    @FXML
    ChoiceBox<String> choiceBoxSteps;

    private String marketPlace;
    private String step;

    public interface ActionInController{
        void selectFile(List<File> files, String marketPlace, String step);
    }

    private static final List<ActionInController> listSubscribers = new ArrayList<>();

    public static void subscribe(ActionInController subscriber){
        listSubscribers.add(subscriber);
    }

    private void notifySubscriber(List<File> files, String marketPlace, String step){
        for (ActionInController subscriber : listSubscribers) {
            System.out.println(marketPlace);
            subscriber.selectFile(files, marketPlace, step);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> marketPlaces = FXCollections.observableArrayList("Wildberies", "Ozon");
        choiceBoxMarketPlace.setItems(marketPlaces);
        choiceBoxMarketPlace.setValue("Wildberies");
        marketPlace = "Wildberies";

        ObservableList<String> steps = FXCollections.observableArrayList("₽", "%");
        choiceBoxSteps.setItems(steps);
        choiceBoxMarketPlace.setValue("₽");
        step = "₽";


        choiceBoxMarketPlace.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                marketPlace = choiceBoxMarketPlace.getValue();
                System.out.println("выбран - " + marketPlace);
            }
        });

        choiceBoxSteps.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                step = choiceBoxSteps.getValue();
                System.out.println("выбран - " + step);
            }
        });

        final FileChooser fileChooser = new FileChooser();
        btnSelectFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                areaLog.clear();
                txtFldShowPathFile.clear();
                Node node = (Node) event.getSource();
                List<File> files = fileChooser.showOpenMultipleDialog(node.getScene().getWindow());
                notifySubscriber(files, marketPlace, step);
                if (files.size() == 1){
                    txtFldShowPathFile.appendText(files.get(0).getAbsolutePath());
                } else if (files.size() == 2){
                    txtFldShowPathFile.appendText(files.get(0).getAbsolutePath() + " " + files.get(1).getAbsolutePath());
                }
            }
        });
    }
}
