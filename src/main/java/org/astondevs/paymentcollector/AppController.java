package org.astondevs.paymentcollector;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import org.astondevs.paymentcollector.filemerger.FileMerger;
import org.astondevs.paymentcollector.filemerger.impl.XmlFileMerger;
import org.astondevs.paymentcollector.filesystem.FileManager;
import org.astondevs.paymentcollector.filesystem.impl.FileManagerImpl;
import org.astondevs.paymentcollector.logging.JavaFXLogAppender;
import org.astondevs.paymentcollector.service.MergeService;
import org.astondevs.paymentcollector.service.impl.MergeServiceImpl;
import org.astondevs.paymentcollector.xmlvalidator.XmlValidator;
import org.astondevs.paymentcollector.xmlvalidator.impl.XsdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class AppController {

    private static final Logger log = LoggerFactory.getLogger(AppController.class);

    @Setter
    @Getter
    private HostServices hostServices;

    @FXML
    private ListView<String> xmlFileList;

    @FXML
    private TextArea logArea;

    @FXML
    private Hyperlink link;

    @FXML
    private TextField directoryPathInput;

    @FXML
    private TextField schemaPathInput;

    @FXML
    private TextField outputFilePathInput;

    private List<String> xmlFiles;

    private final FileManager fileManager = new FileManagerImpl();

    private String directoryPath;
    private String schemaPath;
    private String outputFilePath;

    @FXML
    public void initialize() {
        JavaFXLogAppender.setLogTextArea(logArea);
        String currentDir = System.getProperty("user.dir");
        directoryPath = currentDir;
        xmlFiles = fileManager.getXmlFiles(directoryPath);
        ObservableList<String> list = FXCollections.observableArrayList(xmlFiles);
        xmlFileList.setItems(list);
        link.setVisible(false);
        schemaPath = currentDir + File.separator + "CREATE_PAYDOC_LOAD_2.xsd";
        outputFilePath = currentDir + File.separator + "Total.xml";
        directoryPathInput.setText(directoryPath);
        schemaPathInput.setText(schemaPath);
        outputFilePathInput.setText(outputFilePath);

        log.info("Current directory: {}", currentDir);
    }

    private final FileMerger merger = new XmlFileMerger();

    @FXML
    protected void onMergeButtonClick() {
        XmlValidator validator = new XsdValidator(schemaPath);
        MergeService mergeService = new MergeServiceImpl(merger, validator);

        List<File> fileList = xmlFiles.stream()
                .map(File::new)
                .toList();

        mergeService.merge(fileList, outputFilePath);
        link.setVisible(true);
        link.setText("Link to merged file: " + outputFilePath);
    }

    @FXML
    protected void onLinkClick() {
        String text = link.getText();
        if (text == null || !text.startsWith("Link to merged file:")) {
            log.error("Invalid link: {}", text);
            return;
        }
        String path = text.substring(text.indexOf(":") + 1);
        if (Files.exists(Path.of(path.trim()))) {
            getHostServices().showDocument(path);
        } else {
            log.error("File not found: {}", path);
        }
    }

    @FXML
    protected void onClearButtonClick() {
        logArea.clear();
    }

    @FXML
    protected void onSetButtonClick() {
        directoryPath = directoryPathInput.getText();
        schemaPath = schemaPathInput.getText();
        outputFilePath = outputFilePathInput.getText();
        xmlFiles = new FileManagerImpl().getXmlFiles(directoryPath);
        ObservableList<String> list = FXCollections.observableArrayList(xmlFiles);
        xmlFileList.setItems(list);
    }
}