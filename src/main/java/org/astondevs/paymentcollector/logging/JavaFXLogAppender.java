package org.astondevs.paymentcollector.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class JavaFXLogAppender extends AppenderBase<ILoggingEvent> {

    private static TextArea logTextArea;

    public static void setLogTextArea(TextArea logTextArea) {
        JavaFXLogAppender.logTextArea = logTextArea;
    }

    public JavaFXLogAppender() {
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (logTextArea != null) {
            String message = String.format("""
                    [%s] %s: %s
                    """, event.getLevel().levelStr, event.getInstant(), event.getFormattedMessage());
            Platform.runLater(() -> logTextArea.appendText(message + System.lineSeparator()));
        }
    }
}
