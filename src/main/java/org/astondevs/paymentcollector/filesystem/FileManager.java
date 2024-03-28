package org.astondevs.paymentcollector.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface FileManager {

    Logger log = LoggerFactory.getLogger(FileManager.class);

    List<String> getXmlFiles(String directoryPath);
}
