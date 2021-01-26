package controllers;

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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public class Controller implements Initializable {

    @FXML
    private TextArea areaLog;

    @FXML
    private Button btnSelectFile;

    @FXML
    private ProgressBar processingRequest;

    @FXML
    private TextField txtFldShowPathFile;

    public interface ActionInController{
        void selectFile(File file);
    }

    private static final List<ActionInController> listSubscribers = new ArrayList<>();

    public static void subscribe(ActionInController subscriber){
        listSubscribers.add(subscriber);
    }

    private void notifySubscriber(File file){
        for (ActionInController subscriber : listSubscribers) {
            subscriber.selectFile(file);
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
                File file = fileChooser.showOpenDialog(node.getScene().getWindow());
                notifySubscriber(file);


                txtFldShowPathFile.appendText(file.getAbsolutePath() + "\n");
//                if (file != null) {
//                    openFile(file);
//                    List<File> files = Arrays.asList(file);
//                    printLog(textArea, files);
//                }
            }
        });
    }
}
