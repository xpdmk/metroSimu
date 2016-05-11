/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ilmoittaja;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author royja
 */
public class Ilmoittaja {
    public Ilmoittaja() {
        
    }
    public void nayta(String otsikko, String sanoma) {
        Stage ikkuna = new Stage();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/Ilmoittaja/Ilmoittaja.fxml"));
        } catch (Exception e) {
            return;
        }
        ikkuna.setTitle(otsikko);
        Button okNappi = (Button) root.lookup("#okNappi");
        okNappi.setOnAction(e -> {
            ikkuna.close();
        });
        ikkuna.setScene(new Scene(root));
        ikkuna.show();
        
    }
}
