package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.enums.EstadoAdministrador;
import edu.utj.evnd.poo.bibliotecaproyec.model.Administrador;
import edu.utj.evnd.poo.bibliotecaproyec.repository.AdministradorRepository;
import edu.utj.evnd.poo.bibliotecaproyec.repository.PersonaRepository;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista de administradores. Aporta los campos propios
 * (usuario, contraseña, estado y rol); el CRUD lo resuelve la clase base.
 *
 * @author Joshua Odin Montoya Flores
 */
public class AdministradorController extends PersonaCrudController<Administrador> {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<EstadoAdministrador> cbEstado;
    @FXML private TextField txtRol;

    @FXML private TableColumn<Administrador, String> colUsuario;
    @FXML private TableColumn<Administrador, EstadoAdministrador> colEstado;
    @FXML private TableColumn<Administrador, String> colRol;

    private final AdministradorRepository repository = new AdministradorRepository();

    @Override
    protected PersonaRepository<Administrador> repositorio() {
        return repository;
    }

    @Override
    protected String nombreEntidad() {
        return "Administrador";
    }

    @Override
    protected Administrador crearEntidad(int id) {
        Administrador admin = new Administrador(id, txtCurp.getText(), txtNombre.getText(),
                txtApellido.getText(), txtUsuario.getText(), txtContrasena.getText());
        admin.setEstado(cbEstado.getValue());
        admin.setRol(txtRol.getText());
        return admin;
    }
    
    @Override
    protected boolean validarCamposEspecificos(){
        if (txtUsuario.getText().trim().isEmpty()){
            return invalido(txtUsuario, "El usuario es obligatorio.");
        } 
        if (txtContrasena.getText().length() < 4){
            return invalido(txtContrasena, "La contraseña debe tener al menos 4 caracteres.");
        }
        if (cbEstado.getValue() == null){
            return invalido(cbEstado, "Selecciona un estado.");
        }
        return true;
    }
    
    @Override
    protected int edadMinima(){
        return 18;
    }

    @Override
    protected void configurarColumnasEspecificas() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
    }

    @Override
    protected void prepararCamposEspecificos() {
        // Solo se pueden elegir los valores definidos en el enum del modelo.
        cbEstado.setItems(FXCollections.observableArrayList(EstadoAdministrador.values()));
    }

    @Override
    protected void cargarCamposEspecificos(Administrador a) {
        txtUsuario.setText(a.getUsuario());
        txtContrasena.setText(a.getContrasena());
        cbEstado.setValue(a.getEstado());
        txtRol.setText(a.getRol());
    }

    @Override
    protected void limpiarCamposEspecificos() {
        txtUsuario.clear();
        txtContrasena.clear();
        cbEstado.setValue(EstadoAdministrador.ACTIVO);
        txtRol.clear();
    }

    @Override
    protected void habilitarCamposEspecificos(boolean habilitar) {
        txtUsuario.setDisable(!habilitar);
        txtContrasena.setDisable(!habilitar);
        cbEstado.setDisable(!habilitar);
        txtRol.setDisable(!habilitar);
    }
}
