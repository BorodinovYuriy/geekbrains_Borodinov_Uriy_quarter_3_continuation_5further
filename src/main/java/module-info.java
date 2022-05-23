module com.example.csbd {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.apache.commons.io;

    opens com.example.csbd to javafx.fxml;
    exports com.example.csbd;
}