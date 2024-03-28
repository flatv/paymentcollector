package org.astondevs.paymentcollector.filemerger.impl;

import org.astondevs.paymentcollector.filemerger.FileMerger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class XmlFileMerger implements FileMerger {

    private final Transformer transformer;

    public XmlFileMerger() {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (TransformerConfigurationException e) {
            log.error("Error initializing XmlFileMerger: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void merge(List<File> validFiles, String outputFilePath) {
        if (validFiles == null || validFiles.isEmpty()) {
            log.error("No valid XML files found");
            return;
        }
        if (outputFilePath == null || outputFilePath.isEmpty()) {
            log.error("Invalid output file path: {}", outputFilePath);
            return;
        }
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document mergeDoc = builder.newDocument();
            Element element = mergeDoc.createElement("documents");
            mergeDoc.appendChild(element);
            for (File file : validFiles) {
                Document doc = builder.parse(file);

                Node root = doc.getDocumentElement();

                Element docElement = mergeDoc.createElement("document");
                Node importedRoot = mergeDoc.importNode(root, true);

                docElement.appendChild(importedRoot);
                element.appendChild(docElement);
            }
            DOMSource source = new DOMSource(mergeDoc);
            StreamResult result = new StreamResult(new File(outputFilePath));
            transformer.transform(source, result);
            log.info("XML files merged to {}", outputFilePath);
        } catch (TransformerException | ParserConfigurationException | IOException | SAXException e) {
            log.error("Error merging XML files: {}", e.getMessage());
        }
    }
}
