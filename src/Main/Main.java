package Main;

import Tyontekijanakyma.TyontekijanakymanKasittelija;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import luokat.Leiri;
import static javafx.application.Application.launch;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javax.swing.JOptionPane;

public class Main extends Application implements Initializable {

    static Leiri leiri;
    static ArrayList<Integer> lisaaTyontekjoita;
    static ArrayList<Slider> sliderit;
    static ArrayList<TextField> kentat;
    static ArrayList<Integer> maarat;
    static ArrayList<Label> tekstit;
    static boolean voidaanSulkea;
    static int kaytettavienTyopaikkojenMaara;
    static int tyopaikkojenMaara;
    static int maxTickShow;
    static Parent root;

    public static void main(String[] args) {
        //Pääikkunan valmistelu ja avaus
        tyopaikkojenMaara = 3;
        voidaanSulkea = true;
        kaytettavienTyopaikkojenMaara = 2;
        maxTickShow = 20;
        lisaaTyontekjoita = new ArrayList<>(Collections.nCopies(kaytettavienTyopaikkojenMaara, 0));

        //Leirin valmistelu
        leiri = new Leiri();

        launch(args);

    }

    //Javafx suorittaa tämän metodien, kun main-metodin launch(args) suoritetaan
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Main.fxml tiedoston lataus samasta kansiosta
        root = FXMLLoader.load(getClass().getResource("Main.fxml"));

        sliderit = new ArrayList<>();
        kentat = new ArrayList<>();
        maarat = new ArrayList<>(Collections.nCopies(kaytettavienTyopaikkojenMaara, 0));
        tekstit = new ArrayList<>();

        //Slidereiden, kenttien ja tekstien valmistelu
        for (int i = 0; i < tyopaikkojenMaara; i++) {
            if (i >= kaytettavienTyopaikkojenMaara) {
                root.lookup("#editVBox" + i).setDisable(true);
                root.lookup("#tyontekijatTeksti" + i).setDisable(true);
            } else {
                //Haetaan Slider-elementti
                Slider slider = (Slider) root.lookup("#slider" + i);
                sliderit.add(slider);
                ////Lisätään kuuntelija, joka muuttaa lisaaTyontekijoita-listan arvoa, kun Slideria liikutetaan
                slider.valueProperty().addListener((ObservableValue<? extends Number> arvo, Number vanha, Number uusi) -> {
                    if (vanha.intValue() != uusi.intValue()) {
                        lisaaTyontekjoita.set(sliderit.indexOf(slider), (int) arvo.getValue().intValue());
                        paivitaKentat();
                    }
                });

                //Valmistellaan tekstikenttä-elementti
                TextField kentta = (TextField) root.lookup("#lisaaKentta" + i);
                kentat.add(kentta);
                ////Lisätään kuuntelija, joka tarkistaa, onko syöte numero
                kentta.focusedProperty().addListener((ObservableValue<? extends Boolean> arvo, Boolean vanha, Boolean uusi) -> {
                    if (vanha && !kentta.getText().isEmpty()) {
                        tarkistaKentat();
                    }
                });
                tekstit.add((Label) root.lookup("#tyontekijatTeksti" + i));
            }

        }
        //Nappien toimintojen määritteleminen
        Button suoritaNappi = (Button) root.lookup("#suorita");
        suoritaNappi.setOnAction(e -> {
            if (voidaanSulkea) {
                //Suljetaan ikkuna
                primaryStage.close();

                //Palkataan työntekijät käyttäjän syöttöjen mukaan
                for (int i = 0; i < lisaaTyontekjoita.size(); i++) {

                    for (int k = 0; k < lisaaTyontekjoita.get(i); k++) {
                        leiri.palkkaaTyontekija(i + 1);
                    }
                }

                leiri.kasittele();
                try {
                    start(new Stage());
                } catch (Exception error) {
                    primaryStage.close();
                }
            }

        });

        //Ikkunan valmistelut ja näyttö
        paivita();
        primaryStage.setTitle("Resurssienkeruusimulaattori");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    //Pääikkunassa napit, joissa lukee "Tutki työntekijöitä", kutsuvat tätä metodia.
    //Metodiin haettu apua täältä: http://stackoverflow.com/questions/25409044/javafx-multiple-buttons-to-same-handler
    @FXML
    public void tutkiTyolaisia(ActionEvent event) throws Exception {
        Button kutsuja = (Button) event.getSource();
        int numero;
        String ikkunanOtsikko;
        //Selvitetään, mitä bt-nappia painettiin
        if (kutsuja.getId().equals("bt1")) {
            numero = 1;
            ikkunanOtsikko = "Metsätyöläiset";
        } else if (kutsuja.getId().equals("bt2")) {
            numero = 2;
            ikkunanOtsikko = "Metsästäjät";
        } else if (kutsuja.getId().equals("bt3")) {
            numero = 3;
            ikkunanOtsikko = "Kaivostyöläiset";
        } else {
            return;
        }
        ArrayList<Integer> poistettavat = new TyontekijanakymanKasittelija().nayta(leiri.palautaTyontekijatTyopaikkaindksilla(numero), ikkunanOtsikko);

        leiri.poistaTyontekijat(poistettavat);
    }

    private void paivita() {
        paivitaTyontekijoidenMaarat();

        for (int i = 0; i < tekstit.size(); i++) {
            if (!tekstit.get(i).isDisabled()) {
                tekstit.get(i).setText("Työntekijät: " + maarat.get(i));
            }
        }

        paivitaKentat();
        paivitaLisaaelementit();

    }

    private void paivitaKentat() {
        for (int i = 0; i < kentat.size(); i++) {
            if (!kentat.get(i).getText().isEmpty()) {
                kentat.get(i).setText("" + lisaaTyontekjoita.get(i));
            }

            kentat.get(i).setPromptText("Palkkaa tyontekijoita. Liukusäädin:" + lisaaTyontekjoita.get(i));
        }
    }

    private void paivitaTyontekijoidenMaarat() {
        for (int i = 0; i < maarat.size(); i++) {
            maarat.set(i, leiri.palautaTyontekijoidenMaaraTyopaikkaindeksilla(i + 1));

        }
    }

    private void paivitaLisaaelementit() {

        for (int i = 0; i < maarat.size(); i++) {
            int maara = maarat.get(i);
            Slider slider = sliderit.get(i);
            slider.setMin(0);
            slider.setBlockIncrement(1);
            slider.setSnapToTicks(true);
            if (maara > maxTickShow) {
                slider.setShowTickMarks(false);
                slider.setShowTickLabels(false);
            }
            if (maara < 1) {
                sliderit.get(i).setMax(3);
            } else {
                sliderit.get(i).setMax(maara * 3);
            }
        }
    }

    private void tarkistaKentat() {
        for (TextField kentta : kentat) {
            try {
                lisaaTyontekjoita.set(kentat.indexOf(kentta), Integer.parseInt(kentta.getText()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Syötteen täytyy olla numero tai tyhjä");
                kentta.requestFocus();
                voidaanSulkea = false;
                return;
            }
        }
        voidaanSulkea = true;
    }

}
