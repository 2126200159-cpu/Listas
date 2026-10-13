package edu.utj.evnd.poo.bibliotecaproyec.controller;

import edu.utj.evnd.poo.bibliotecaproyec.model.Persona;
import edu.utj.evnd.poo.bibliotecaproyec.repository.PersonaRepository;
import edu.utj.evnd.poo.bibliotecaproyec.service.ImagenService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.time.Period;

/**
 * Controlador base del CRUD de cualquier {@link Persona}. Contiene la lógica
 * común (modos Nuevo/Editar, validación, tabla, búsqueda e imagen); cada
 * subclase solo aporta lo propio de su entidad. Las ventanas no contienen
 * lógica de negocio: las reglas viven en el modelo y el almacenamiento en el
 * repositorio.
 *
 * @param <T> tipo de persona que gestiona
 * @author Joshua Odin Montoya Flores
 */
public abstract class PersonaCrudController<T extends Persona> implements Initializable {

    private enum Modo { NINGUNO, NUEVO, EDITAR }

    @FXML protected TextField txtId;
    @FXML protected TextField txtCurp;
    @FXML protected TextField txtRfc;
    @FXML protected TextField txtNombre;
    @FXML protected TextField txtApellido;
    @FXML protected DatePicker dpNacimiento;
    @FXML protected TextField txtCorreo;
    @FXML protected TextField txtTelefono;
    @FXML protected TextField txtBuscar;
    @FXML protected ImageView imgPreview;

    @FXML protected Button btnImagen;
    @FXML protected Button btnNuevo;
    @FXML protected Button btnGuardar;
    @FXML protected Button btnEditar;
    @FXML protected Button btnEliminar;
    @FXML protected Button btnLimpiar;
    @FXML protected Button btnBuscar;
    @FXML protected Button btnTodos;

    @FXML protected TableView<T> tabla;
    @FXML protected TableColumn<T, Integer> colId;
    @FXML protected TableColumn<T, String> colCurp;
    @FXML protected TableColumn<T, String> colNombre;
    @FXML protected TableColumn<T, String> colApellido;
    @FXML protected TableColumn<T, String> colCorreo;

    private final ImagenService imagenService = new ImagenService();
    private final ObservableList<T> datos = FXCollections.observableArrayList();
    private Modo modo = Modo.NINGUNO;
    private T seleccionado;
    private File imagenNueva;
    private boolean busquedaRealizada;

    // ===== Puntos de extensión que implementa cada entidad =====

    /** @return repositorio donde se guardan los registros */
    protected abstract PersonaRepository<T> repositorio();

    /** @return nombre de la entidad para los mensajes (p. ej. "Administrador") */
    protected abstract String nombreEntidad();

    /**
     * Construye la entidad con los datos del formulario.
     *
     * @param id identificador ya leído del formulario
     * @return la entidad
     * @throws IllegalArgumentException si algún dato no es válido
     */
    protected abstract T crearEntidad(int id);
    
    /**
     * Valida los campos propios de la entidad (vacios, numericos, etc.).
     * Debe usar [@link #invalido(Control, String)] para avisar del error
     * 
     * @return [@code true] si todos los campos propios son correctos
     */
    protected abstract boolean validarCamposEspecificos();

    /** Enlaza las columnas propias de la entidad. */
    protected abstract void configurarColumnasEspecificas();

    /** Prepara los controles propios (p. ej. cargar un ComboBox). */
    protected abstract void prepararCamposEspecificos();

    /** @param entidad registro cuyos datos propios se muestran en el formulario */
    protected abstract void cargarCamposEspecificos(T entidad);

    /** Deja los controles propios en su valor inicial. */
    protected abstract void limpiarCamposEspecificos();

    /** @param habilitar {@code true} para permitir editar los controles propios */
    protected abstract void habilitarCamposEspecificos(boolean habilitar);

    // ===== Inicialización =====

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tabla.setEditable(false);
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCurp.setCellValueFactory(new PropertyValueFactory<>("curp"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        configurarColumnasEspecificas();
        prepararCamposEspecificos();

        SortedList<T> ordenados = new SortedList<>(datos);
        ordenados.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(ordenados);

        txtBuscar.setOnAction(e -> onBuscar());

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            if (nuevo != null) {
                seleccionado = nuevo;
                modo = Modo.NINGUNO;
                imagenNueva = null;
                mostrar(nuevo);
                habilitar(false, false);
                actualizarBotones();
            }
        });

        cancelar();
    }

    // ===== Acciones de los botones =====

    @FXML
    protected void onNuevo() {
        cancelar();
        modo = Modo.NUEVO;
        habilitar(true, true);
        actualizarBotones();
        txtId.requestFocus();
    }
    
    @FXML
    protected void onBuscar(){
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()){
            alerta(Alert.AlertType.WARNING, "Busqueda", "Escribe un ID, CURP, nombre o correo para buscar.");
            txtBuscar.requestFocus();
            return;
        }
        List<T> resultado = repositorio().buscar(texto);
        if (resultado.isEmpty()){
            alerta(Alert.AlertType.ERROR, "No encontrado", "No existe ningun " + nombreEntidad().toLowerCase() + " que coincida con la busqueda. ");
            txtBuscar.requestFocus();
            return;
        }
        reiniciarFormulario();
        datos.setAll(resultado);
        busquedaRealizada = true;
        tabla.getSelectionModel().selectFirst();
        actualizarBotones();
    }
    
    @FXML
    protected void onMostrarTodos(){
        cancelar();
    }

    @FXML
    protected void onEditar() {
        if (!busquedaRealizada || seleccionado == null) {
            alerta(Alert.AlertType.WARNING, "Editar", "Selecciona un registro de la tabla.");
            return;
        }
        modo = Modo.EDITAR;
        habilitar(true, false); // el id no se puede cambiar al editar
        actualizarBotones();
    }

    @FXML
    protected void onGuardar() {
        if (modo == Modo.NINGUNO) {
            return;
        }
        boolean esNuevo = modo == Modo.NUEVO;
        if (!validar(esNuevo)){
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            T entidad = crearEntidad(id);
            aplicarDatosComunes(entidad);

            String rutaAnterior = seleccionado != null ? seleccionado.getUrlImagen() : "";
            String ruta = rutaAnterior;
            if (imagenNueva != null) {
                ruta = imagenService.guardar(imagenNueva, id);
            }
            entidad.setUrlImagen(ruta);

            boolean ok = esNuevo ? repositorio().agregar(entidad) : repositorio().actualizar(entidad);
            if (!ok) {
                if (imagenNueva != null) {
                    imagenService.eliminar(ruta);
                }
                alerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo guardar: el ID o la CURP ya pertenecen a otro registro.");
                return;
            }
            if (imagenNueva != null && !esNuevo) {
                imagenService.eliminar(rutaAnterior); // reemplazo: borra la imagen vieja
            }
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Éxito", nombreEntidad()
                    + (esNuevo ? " guardado correctamente." : " actualizado correctamente."));
        } catch (IllegalArgumentException ex) {
            alerta(Alert.AlertType.WARNING, "Datos inválidos", ex.getMessage());
        } catch (IOException ex) {
            alerta(Alert.AlertType.ERROR, "Error con la imagen", ex.getMessage());
        }
    }

    @FXML
    protected void onEliminar() {
        if (seleccionado == null) {
            alerta(Alert.AlertType.WARNING, "Eliminar", "Selecciona un registro de la tabla.");
            return;
        }
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar a " + seleccionado.getNombreCompleto() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirmar.setTitle("Confirmar eliminación");
        confirmar.setHeaderText(null);
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }
        if (repositorio().eliminar(seleccionado.getId())) {
            imagenService.eliminar(seleccionado.getUrlImagen());
            cancelar();
            alerta(Alert.AlertType.INFORMATION, "Eliminado", nombreEntidad() + " eliminado correctamente.");
        } else {
            alerta(Alert.AlertType.ERROR, "Error", "No se encontró el registro para eliminar.");
        }
    }

    @FXML
    protected void onLimpiar() {
        cancelar();
    }

    @FXML
    protected void onSeleccionarImagen() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar imagen");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File archivo = selector.showOpenDialog(imgPreview.getScene().getWindow());
        if (archivo != null) {
            imagenNueva = archivo;
            imgPreview.setImage(cargarImagen(archivo));
        }
    }
    
    /**
     * 
     * @param esNuevo
     * @return 
     */
    private boolean validar(boolean esNuevo){
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()){
            return invalido(txtId, "El ID es obligatorio");
        } 
        int id;
        try{
            id = Integer.parseInt(idTexto);
        } catch (NumberFormatException ex){
            return invalido(txtId, "El ID debe ser un numero entero.");
        }
        if (id <= 0) {
            return invalido(txtId, "El ID debe ser un entero positivo.");
        }
        if (txtCurp.getText().trim().isEmpty()){
            return invalido(txtCurp, "La CURP es obligatoria.");
        }
        if (txtNombre.getText().trim().isEmpty()){
            return invalido(txtNombre, "El nombre es obligatorio.");
        }
        if (txtApellido.getText().trim().isEmpty()){
            return invalido(txtApellido, "El apellido es obligatorio.");
        }
        String correo = txtCorreo.getText().trim();
        if (!correo.isEmpty() && !correo.matches("^[^@\\s]+@[^@\\s]+$")){
            return invalido(txtCorreo, "El correo no tiene un formato valido.");
        }
        LocalDate nacimiento = dpNacimiento.getValue();
        if (nacimiento == null){
            return invalido(dpNacimiento, "La fecha de nacimiento es obligatorio.");
        }
        if ( ! nacimiento.isBefore(LocalDate.now())){
            return invalido(dpNacimiento, "La fecha de nacimiento debe ser anterior a hoy.");        
        }
        if (Period.between(nacimiento, LocalDate.now()).getYears() < edadMinima()){
            return invalido(dpNacimiento, "Debe tener al menos " + edadMinima() + "años. ");
        }
        if (esNuevo && repositorio().buscarPorId(id) != null){
            return invalido(txtId, "Ya existe un registro con ese ID.");
        }
        if (repositorio().existeCurp(txtCurp.getText().trim(), esNuevo ? null : id)){
            return invalido(txtCurp, "La CURP ya pertenece a otro registro.");
        }
        String telefono = txtTelefono.getText().trim();
        if (!telefono.isEmpty() && !telefono.matches("\\d{10}")){
            return invalido(txtTelefono, "El telefono debe tener exactamente 10 digitos. ");
        }
        return validarCamposEspecificos();
    }
    
    /**
     * 
     * @param campo
     * @param mensaje
     * @return 
     */
    protected boolean invalido(Control campo, String mensaje){
        alerta(Alert.AlertType.WARNING, "Datos invalidos", mensaje);
        campo.requestFocus();
        return false;
    }

    // ===== Apoyo interno =====

    /** Vuelve al estado inicial: sin selección, formulario vacío y bloqueado. */
    private void cancelar() {
        reiniciarFormulario();
        txtBuscar.clear();
        busquedaRealizada = false;
        datos.setAll(repositorio().obtenerTodos());
        actualizarBotones();
    }
    
    private void reiniciarFormulario(){
        modo = Modo.NINGUNO;
        seleccionado = null;
        imagenNueva = null;
        tabla.getSelectionModel().clearSelection();
        limpiarFormulario();
        habilitar(false, false);
        actualizarBotones();
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtCurp.clear();
        txtRfc.clear();
        txtNombre.clear();
        txtApellido.clear();
        dpNacimiento.setValue(null);
        txtCorreo.clear();
        txtTelefono.clear();
        imgPreview.setImage(null);
        limpiarCamposEspecificos();
    }

    private void mostrar(T e) {
        txtId.setText(String.valueOf(e.getId()));
        txtCurp.setText(e.getCurp());
        txtRfc.setText(e.getRfc());
        txtNombre.setText(e.getNombre());
        txtApellido.setText(e.getApellido());
        LocalDate fecha = e.getNacimiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        dpNacimiento.setValue(fecha);
        txtCorreo.setText(e.getCorreo());
        txtTelefono.setText(e.getTelefono());
        File imagen = imagenService.resolver(e.getUrlImagen());
        imgPreview.setImage(imagen != null ? cargarImagen(imagen) : null);
        cargarCamposEspecificos(e);
    }

    private Image cargarImagen(File archivo) {
        return new Image(archivo.toURI().toString(), 140, 140, true, true);
    }

    private void habilitar(boolean editable, boolean idEditable) {
        txtId.setDisable(!(editable && idEditable));
        Control[] campos = {txtCurp, txtRfc, txtNombre, txtApellido, dpNacimiento,
                txtCorreo, txtTelefono, btnImagen};
        for (Control c : campos) {
            c.setDisable(!editable);
        }
        habilitarCamposEspecificos(editable);
    }

    private void actualizarBotones() {
        btnGuardar.setText(modo == Modo.EDITAR ? "Actualizar" : "Registrar");
        btnGuardar.setDisable(modo == Modo.NINGUNO);
        boolean puedeActuar = busquedaRealizada && seleccionado != null;
        btnEditar.setDisable(seleccionado == null || modo != Modo.NINGUNO);
        btnEliminar.setDisable(seleccionado == null);
    }


    private void aplicarDatosComunes(T e) {
        if (dpNacimiento.getValue() != null) {
            e.setNacimiento(Date.from(dpNacimiento.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        e.setRfc(txtRfc.getText());
        e.setCorreo(txtCorreo.getText());
        e.setTelefono(txtTelefono.getText());
    }
    
    /**
     * 
     * @return 
     */
    protected int edadMinima(){
        return 0;
    }

    /**
     * Muestra una ventana de mensaje al usuario.
     *
     * @param tipo    tipo de alerta
     * @param titulo  título de la ventana
     * @param mensaje texto a mostrar
     */
    protected void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
