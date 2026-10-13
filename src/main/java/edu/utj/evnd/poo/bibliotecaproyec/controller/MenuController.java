package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.App;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * Controlador del menú principal: permite navegar entre los módulos. Las vistas
 * se cargan una sola vez y se reutilizan, así los registros capturados
 * (en memoria) se conservan al cambiar de módulo.
 *
 * @author Joshua Odin Montoya Flores
 */
public class MenuController implements Initializable {

    @FXML private StackPane contenedor;
    @FXML private Button btnAdministradores;
    @FXML private Button btnLectores;

    private Parent vistaAdministradores;
    private Parent vistaLectores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            vistaAdministradores = App.loadFXML("administrador");
            vistaLectores = App.loadFXML("lector");
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        onAdministradores();
    }

    @FXML
    private void onAdministradores() {
        mostrar(vistaAdministradores, btnAdministradores);
    }

    @FXML
    private void onLectores() {
        mostrar(vistaLectores, btnLectores);
    }

    @FXML
    private void onSalir() {
        Platform.exit();
    }

    private void mostrar(Parent vista, Button activo) {
        contenedor.getChildren().setAll(vista);
        btnAdministradores.getStyleClass().remove("activo");
        btnLectores.getStyleClass().remove("activo");
        activo.getStyleClass().add("activo");
    }
}
