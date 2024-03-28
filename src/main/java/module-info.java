module org.astondevs.paymentcollector {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires org.slf4j;
    requires java.xml;
    requires java.logging;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;
    requires java.naming;

    opens org.astondevs.paymentcollector to javafx.fxml;
    exports org.astondevs.paymentcollector.logging;
    exports org.astondevs.paymentcollector;
}