package serializers;

import medicaments.Medicament;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Map;
import org.w3c.dom.*;

public class XmlSerializer implements Serializer {
    private static final String FILENAME = "medicaments.xml";

    @Override
    public void sauvegarder(Map<String, Medicament> medicaments) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("medicaments");
            doc.appendChild(rootElement);

            for (Medicament medicament : medicaments.values()) {
                rootElement.appendChild(medicament.toXML(doc));
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILENAME));

            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Erreur lors de la sauvegarde des médicaments en XML: " + e.getMessage());
        }
    }

    @Override
    public void charger(Map<String, Medicament> medicaments) {
        try {
            File file = new File(FILENAME);
            if (!file.exists()) {
                return;
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);

            NodeList nodeList = doc.getElementsByTagName("medicament");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Medicament medicament = Medicament.fromXML(element);
                    medicaments.put(medicament.getId(), medicament);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des médicaments en XML: " + e.getMessage());
        }
    }
}
