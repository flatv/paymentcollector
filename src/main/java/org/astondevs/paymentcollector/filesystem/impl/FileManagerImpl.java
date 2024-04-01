package org.astondevs.paymentcollector.filesystem.impl;

import org.astondevs.paymentcollector.filesystem.FileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class FileManagerImpl implements FileManager {

    @Override
    public List<String> getXmlFiles(String directoryPath) {
        if (directoryPath == null || directoryPath.isEmpty()) {
            log.error("Invalid directory path: {}", directoryPath);
            return Collections.emptyList();
        }
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            log.error("Directory not found: {}", directoryPath);
            return Collections.emptyList();
        }
        try (Stream<Path> pathStream = Files.list(path)) {
            List<String> list = pathStream
                    .map(Path::toString)
                    .filter(string -> string.endsWith(".xml") && !string.equals("Total.xml"))
                    .limit(10)
                    .toList();
            log.info("Found {} XML files in directory: {}", list.size(), directoryPath);
            return list;
        } catch (IOException e) {
            log.error("Failed to list files in directory: {}", directoryPath, e);
            return Collections.emptyList();
        }
    }
}
