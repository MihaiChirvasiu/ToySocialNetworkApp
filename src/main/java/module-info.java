module com.example.toysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.toysocialnetwork to javafx.fxml;
    exports com.example.toysocialnetwork;

    opens com.example.toysocialnetwork.Domain to javafx.base;
    exports com.example.toysocialnetwork.Service;

    requires java.sql;
}