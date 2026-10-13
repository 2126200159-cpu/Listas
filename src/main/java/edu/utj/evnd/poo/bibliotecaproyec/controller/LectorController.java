package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.model.Lector;
import edu.utj.evnd.poo.bibliotecaproyec.repository.LectorRepository;
import edu.utj.evnd.poo.bibliotecaproyec.repository.PersonaRepository;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista de lectores. Aporta los campos propios
 * (credencial y multa pendiente); el CRUD lo resuelve la clase base.
 *
 * @author Joshua Odin Montoya Flores
 */
public class LectorController extends PersonaCrudController<Lector> {

    @FXML private TextField txtCredencial;
    @FXML private TextField txtMulta;

    @FXML private TableColumn<Lector, String> colCredencial;
    @FXML private TableColumn<Lector, Double> colMulta;

    private final LectorRepository repository = new LectorRepository();

    @Override
    protected PersonaRepository<Lector> repositorio() {
        return repository;
    }

    @Override
    protected String nombreEntidad() {
        return "Lector";
    }

    @Override
    protected Lector crearEntidad(int id) {
        Lector lector = new Lector(id, txtCurp.getText(), txtNombre.getText(),
                txtApellido.getText(), txtCredencial.getText());
        String texto = txtMulta.getText().trim();
        try {
            lector.setMultaPendiente(texto.isEmpty() ? 0.0 : Double.parseDouble(texto));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La multa debe ser un número.");
        }
        return lector;
    }
    
    @Override
    protected boolean validarCamposEspecificos(){
        if (txtCredencial.getText().trim().isEmpty()){
            return invalido(txtCredencial, "El numero de credencial es obligatorio.");
        }
        String multa = txtMulta.getText().trim();
        if (!multa.isEmpty()){
            try {
                if (Double.parseDouble(multa) < 0){
                    return invalido(txtMulta, "La multa no puede ser negativa.");
                }
            } catch (NumberFormatException ex){
                return invalido(txtMulta, "La multa debe ser un numero. ");
            }
        }
        return true;
    }

    @Override
    protected void configurarColumnasEspecificas() {
        colCredencial.setCellValueFactory(new PropertyValueFactory<>("numeroCredencial"));
        colMulta.setCellValueFactory(new PropertyValueFactory<>("multaPendiente"));
    }

    @Override
    protected void prepararCamposEspecificos() {
        // Este módulo no tiene controles que preparar.
    }

    @Override
    protected void cargarCamposEspecificos(Lector l) {
        txtCredencial.setText(l.getNumeroCredencial());
        txtMulta.setText(String.valueOf(l.getMultaPendiente()));
    }

    @Override
    protected void limpiarCamposEspecificos() {
        txtCredencial.clear();
        txtMulta.setText("0");
    }

    @Override
    protected void habilitarCamposEspecificos(boolean habilitar) {
        txtCredencial.setDisable(!habilitar);
        txtMulta.setDisable(!habilitar);
    }
}
