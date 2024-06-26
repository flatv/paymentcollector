package org.astondevs.paymentcollector.service.impl;

import org.astondevs.paymentcollector.filemerger.FileMerger;
import org.astondevs.paymentcollector.service.MergeService;
import org.astondevs.paymentcollector.xmlvalidator.XmlValidator;

import java.io.File;
import java.util.List;

public class MergeServiceImpl implements MergeService {

    private final FileMerger fileMerger;

    private final XmlValidator xmlValidator;

    public MergeServiceImpl(FileMerger fileMerger, XmlValidator xmlValidator) {
        this.fileMerger = fileMerger;
        this.xmlValidator = xmlValidator;
    }

    @Override
    public void merge(List<File> xmlFiles, String outputFilePath) {
        for (File xmlFile : xmlFiles) {
            if (!xmlValidator.validate(xmlFile)) {
                log.error("При обработке файлов произошла ошибка");
                throw new RuntimeException("Invalid XML file: " + xmlFile.getName());
            }
        }
        fileMerger.merge(xmlFiles, outputFilePath);
    }
}
