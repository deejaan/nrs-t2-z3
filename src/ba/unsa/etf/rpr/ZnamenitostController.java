package ba.unsa.etf.rpr;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class ZnamenitostController {
    private Znamenitost znamenitost;
    private Grad grad;
    private String putanjaSlike;
    public TextField fieldNazivZnamenitost;
    public ImageView imageViewZnamenitost;


    public ZnamenitostController(Grad grad) {
        this.grad = grad;
        this.putanjaSlike = "";
    }

    public Znamenitost getZnamenitost() {
        return znamenitost;
    }

    public void spasiIzmjene() {
        if (znamenitost == null) znamenitost = new Znamenitost();
        znamenitost.setGrad(grad);
        znamenitost.setPutanjaSlike(putanjaSlike);
        znamenitost.setNaziv(fieldNazivZnamenitost.getText());
        Stage stage = (Stage) fieldNazivZnamenitost.getScene().getWindow();
        stage.close();
    }

    public void odaberiSliku() throws IOException {
        Stage stage = new Stage();
        Parent root;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pretraga.fxml"));
        PretragaController pretragaController = new PretragaController();
        loader.setController(pretragaController);
        root = loader.load();
        stage.setTitle("Pretraga datoteka");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.setResizable(true);
        stage.show();
        stage.setOnHiding(event -> {
            putanjaSlike = pretragaController.getPutanjaSlike();
            try {
                imageViewZnamenitost.setImage(new Image(new FileInputStream(putanjaSlike)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });


    }
}