module com.example.toysocialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.pdfbox;
    requires org.jfree.jfreechart;
    requires java.desktop;

    opens com.example.toysocialnetwork.Domain to javafx.base;
    opens com.example.toysocialnetwork to javafx.fxml;
    exports com.example.toysocialnetwork;
    exports com.example.toysocialnetwork.Service;
}