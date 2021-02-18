package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
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


    public interface ActionInController{
        void selectFile(List<File> files);
    }

    private static final List<ActionInController> listSubscribers = new ArrayList<>();

    public static void subscribe(ActionInController subscriber){
        listSubscribers.add(subscriber);
    }

    private void notifySubscriber(List<File> files){
        for (ActionInController subscriber : listSubscribers) {
            subscriber.selectFile(files);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final FileChooser fileChooser = new FileChooser();
        btnSelectFile.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                areaLog.clear();
                txtFldShowPathFile.clear();
                Node node = (Node) event.getSource();
                List<File> files = fileChooser.showOpenMultipleDialog(node.getScene().getWindow());
                notifySubscriber(files);
                txtFldShowPathFile.appendText(files.get(0).getAbsolutePath() + " " + files.get(1).getAbsolutePath());
            }
        });
    }
}
