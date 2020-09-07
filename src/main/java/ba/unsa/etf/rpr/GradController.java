package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class GradController {
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public ChoiceBox<Drzava> choiceDrzava;
    public ObservableList<Drzava> listDrzave;
    private Grad grad;
    public TextField fieldPostanskiBroj;
    public ObservableList<Znamenitost> znamenitosti;
    public ListView<Znamenitost> listViewZnamenitosti;
    private GeografijaDAO geografijaDao;

    public GradController(Grad grad, ArrayList<Drzava> drzave) {
        this.grad = grad;
        listDrzave = FXCollections.observableArrayList(drzave);
        if (grad != null) znamenitosti = FXCollections.observableArrayList(grad.getZnamenitosti());
        geografijaDao = GeografijaDAO.getInstance();
    }

    @FXML
    public void initialize() {
        choiceDrzava.setItems(listDrzave);
        if (grad != null) {
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(grad.getBrojStanovnika()));
            fieldPostanskiBroj.setText(Integer.toString(grad.getPostanskiBroj()));
            // choiceDrzava.getSelectionModel().select(grad.getDrzava());
            // ovo ne radi jer grad.getDrzava() nije identički jednak objekat kao član listDrzave
            for (Drzava drzava : listDrzave)
                if (drzava.getId() == grad.getDrzava().getId())
                    choiceDrzava.getSelectionModel().select(drzava);
        } else {
            choiceDrzava.getSelectionModel().selectFirst();
        }
        listViewZnamenitosti.setItems(znamenitosti);
    }

    public Grad getGrad() {
        return grad;
    }

    public void clickCancel(ActionEvent actionEvent) {
        grad = null;
        Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        stage.close();
    }

    public void clickOk(ActionEvent actionEvent) {
        boolean sveOk = true;

        if (fieldNaziv.getText().trim().isEmpty()) {
            fieldNaziv.getStyleClass().removeAll("poljeIspravno");
            fieldNaziv.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
            fieldNaziv.getStyleClass().add("poljeIspravno");
        }


        int brojStanovnika = 0;
        try {
            brojStanovnika = Integer.parseInt(fieldBrojStanovnika.getText());
        } catch (NumberFormatException e) {
            // ...
        }
        if (brojStanovnika <= 0) {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        } else {
            fieldBrojStanovnika.getStyleClass().removeAll("poljeNijeIspravno");
            fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
        }

        if (fieldPostanskiBroj.getText().isEmpty()) {
            fieldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
            fieldPostanskiBroj.getStyleClass().add("poljeNijeIspravno");
            sveOk = false;
        }

        if (!sveOk) return;

        //Validacija postanskog broja putem web servisa
        Thread thread = new Thread(() -> {
            int postanskiBr = 0;
            postanskiBr = Integer.parseInt(fieldPostanskiBroj.getText());
            URL servis = null;
            try {
                servis = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + postanskiBr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(servis.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(inputStreamReader);
            String validacija = scanner.nextLine();
            if (!validacija.equals("OK")) {
                fieldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
                fieldPostanskiBroj.getStyleClass().add("poljeNijeIspravno");
            } else {
                fieldPostanskiBroj.getStyleClass().removeAll("poljeNijeIspravno");
                fieldPostanskiBroj.getStyleClass().add("poljeIspravno");
                if (grad == null) grad = new Grad();
                grad.setNaziv(fieldNaziv.getText());
                grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
                grad.setDrzava(choiceDrzava.getValue());
                grad.setPostanskiBroj(Integer.parseInt(fieldPostanskiBroj.getText()));
                Platform.runLater(() -> {
                    Stage stage = (Stage) fieldNaziv.getScene().getWindow();
                    stage.close();
                });
            }
        });
        thread.start();

        //if (grad == null) grad = new Grad();
        //grad.setNaziv(fieldNaziv.getText());
        //grad.setBrojStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
        //grad.setDrzava(choiceDrzava.getValue());
        //Stage stage = (Stage) fieldNaziv.getScene().getWindow();
        //stage.close();
    }

    public void dodajZnamenitost() throws IOException {
        if (grad == null) return;
        Stage stage = new Stage();
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/znamenitosti.fxml"));
        ZnamenitostController znamenitostController = new ZnamenitostController(grad);
        loader.setController(znamenitostController);
        root = loader.load();
        stage.setTitle("Znamenitost");
        stage.setScene(new Scene(root));
        stage.setResizable(true);
        stage.show();
        stage.setOnHiding(event -> {
            Znamenitost znamenitost = znamenitostController.getZnamenitost();
            if (znamenitost != null) {
                try {
                    geografijaDao.dodajZnamenitost(znamenitost);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                grad.getZnamenitosti().add(znamenitost);
                znamenitosti.setAll(grad.getZnamenitosti());
                listViewZnamenitosti.refresh();
            }

        });
    }
}
