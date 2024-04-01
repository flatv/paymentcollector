package org.astondevs.paymentcollector.filemerger.impl;

import org.astondevs.paymentcollector.filemerger.FileMerger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class XmlFileMerger implements FileMerger {

    private static final long MAX_FILE_SIZE = 500 * 1024; // 500 KB

    private final Transformer transformer;

    public XmlFileMerger() {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document mergeDoc = builder.newDocument();
            mergeDoc.setXmlStandalone(true);

            Element documentRoot = mergeDoc.createElement("ns2:PayDocRuReq");
            documentRoot.setAttribute("Version", "VTB 17.5.1");
            documentRoot.setAttribute("ID", UUID.randomUUID().toString());
            documentRoot.setAttribute("DateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            documentRoot.setAttribute("xmlns:ns2", "http://www.vtb.ru/DBO20/IntegrationPlatform");

            Element bsMessage = mergeDoc.createElement("BSMessage");
            Element documents = mergeDoc.createElement("Documents");

            documentRoot.appendChild(bsMessage);

            Element bsHead = mergeDoc.createElement("BSHead");
            bsHead.setAttribute("RSys", "DB02");
            bsHead.setAttribute("ASys", "CFT2");
            bsHead.setAttribute("route", "");

            mergeDoc.appendChild(documentRoot);
            for (File file : validFiles) {
                Document doc = builder.parse(file);

                NodeList documentTag = doc.getElementsByTagName("DOCUMENT");
                NodeList bsHeadList = doc.getElementsByTagName("BSHead");

                for (int i = 0; i < documentTag.getLength(); i++) {
                    Node item = documentTag.item(i);
                    Element document = mergeDoc.createElement("Document");
                    for (int j = 0; j < item.getChildNodes().getLength(); j++) {
                        Node childNode = item.getChildNodes().item(j);
                        if (childNode.getNodeType() != Node.TEXT_NODE) {
                            Node importedNode = mergeDoc.importNode(childNode, true);
                            document.appendChild(importedNode);
                        }
                    }
                    documents.appendChild(document);
                }

                for (int i = 0; i < bsHeadList.getLength(); i++) {
                    Node node = bsHeadList.item(i);
                    for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                        Node childNode = node.getChildNodes().item(j);
                        if (childNode.getNodeType() != Node.TEXT_NODE) {
                            Node importedNode = mergeDoc.importNode(childNode, true);
                            bsHead.appendChild(importedNode);
                        }
                    }
                }
                bsMessage.appendChild(bsHead);
                bsMessage.appendChild(documents);

                if (getDocumentSize(mergeDoc) > MAX_FILE_SIZE) {
                    log.error("Document size exceeds the maximum size of {} KB", MAX_FILE_SIZE / 1024);
                    throw new RuntimeException("Document size exceeds the maximum size of " + MAX_FILE_SIZE / 1024 + " KB");
                }
            }
            DOMSource source = new DOMSource(mergeDoc);
            StreamResult result = new StreamResult(new File(outputFilePath));
            transformer.transform(source, result);
            log.info("XML files merged to {}", outputFilePath);
            log.info("Файлы успешно обработаны");
        } catch (TransformerException | ParserConfigurationException | IOException | SAXException e) {
            log.error("Error merging XML files: {}", e.getMessage());
        }
    }

    private long getDocumentSize(Document document) throws TransformerException {
        DOMSource source = new DOMSource(document);
        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));
        return writer.getBuffer().length();
    }
}
