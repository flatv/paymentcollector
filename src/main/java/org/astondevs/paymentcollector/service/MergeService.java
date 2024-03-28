package org.astondevs.paymentcollector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public interface MergeService {

    Logger log = LoggerFactory.getLogger(MergeService.class);

    void merge(List<File> xmlFiles, String outputFilePath);
}
