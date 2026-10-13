module edu.utj.evnd.poo.bibliotecaproyec {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens edu.utj.evnd.poo.bibliotecaproyec to javafx.fxml;
    exports edu.utj.evnd.poo.bibliotecaproyec;
}
