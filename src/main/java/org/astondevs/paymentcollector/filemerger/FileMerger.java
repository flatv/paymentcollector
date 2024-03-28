package org.astondevs.paymentcollector.filemerger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public interface FileMerger {

    Logger log = LoggerFactory.getLogger(FileMerger.class);

    void merge(List<File> validFiles, String outputFilePath);
}
