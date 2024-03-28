package org.astondevs.paymentcollector.xmlvalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public interface XmlValidator {

    Logger log = LoggerFactory.getLogger(XmlValidator.class);

    boolean validate(File file);
}
