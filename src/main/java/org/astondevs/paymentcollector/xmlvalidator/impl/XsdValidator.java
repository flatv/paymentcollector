package org.astondevs.paymentcollector.xmlvalidator.impl;

import org.astondevs.paymentcollector.xmlvalidator.XmlValidator;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class XsdValidator implements XmlValidator {

    private final Schema schema;

    public XsdValidator(String schemaPath) {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            this.schema = factory.newSchema(new StreamSource(schemaPath));
        } catch (SAXException e) {
            log.error("Error initializing XsdValidator: {}", e.getMessage());
            throw new RuntimeException("Error initializing XsdValidator: " + e.getMessage());
        }
    }

    @Override
    public boolean validate(File file) {
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(file));
            log.info("XML {} is valid", file.getName());
            return true;
        } catch (SAXException | IOException e) {
            log.error("Failed to validate XML: {}", file.getName());
            return false;
        }
    }
}
