package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PretragaController implements Initializable {
    private String putanjaSlike;
    public TextField fieldUzorak;
    public ListView<String> listViewPretraga;
    ObservableList<String> putanjeObs;
    ArrayList<String> putanje;
    private Thread thread;


    public String getPutanjaSlike() {
        return putanjaSlike;
    }

    public void btnTrazi() {
        putanje.clear();
        putanjeObs.setAll(putanje);
        thread = new Thread(() -> pretraziUzorak(fieldUzorak.getText(), new File(System.getProperty("user.home"))));
        thread.start();
    }

    private void pretraziUzorak(String uzorak, File file) {
        File[] fajlovi = file.listFiles();
        if (fajlovi != null) {
            for (File f : fajlovi) {
                if (f.isDirectory()) {
                    pretraziUzorak(uzorak, f);
                }
                else if (f.getAbsolutePath().toLowerCase().contains(uzorak.toLowerCase())) {
                    Platform.runLater(() -> putanjeObs.add(f.getAbsolutePath()));
                }
            }
        }
    }

    public void btnOk() {
        thread.interrupt();
        putanjaSlike=listViewPretraga.getSelectionModel().getSelectedItem();
        Stage stage=(Stage) listViewPretraga.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        thread=new Thread();
        putanje=new ArrayList<>();
        putanjeObs= FXCollections.observableArrayList();
        listViewPretraga.setItems(putanjeObs);
    }
}
