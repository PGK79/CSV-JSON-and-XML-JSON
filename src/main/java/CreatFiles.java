import com.opencsv.CSVWriter;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CreatFiles {
    public static void creatCsv(String fileName, List<String[]> allEmployee) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName, true))) {
            for (String[] emp : allEmployee) {
                writer.writeNext(emp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void creatXml(String fileName, List<String[]> allEmployee) throws ParserConfigurationException,
            TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element staff = document.createElement("staff");
        document.appendChild(staff);
        for (String[] s : allEmployee) {
            Element employee = document.createElement("employee");
            staff.appendChild(employee);

            Element id = document.createElement("id");
            employee.appendChild(id);
            id.setAttribute("id", s[0]);

            Element firstname = document.createElement("firstname");
            employee.appendChild(firstname);
            firstname.setAttribute("firstname", s[1]);

            Element lastname = document.createElement("lastname");
            employee.appendChild(lastname);
            lastname.setAttribute("lastname", s[2]);

            Element country = document.createElement("country");
            employee.appendChild(country);
            country.setAttribute("country", s[3]);

            Element age = document.createElement("age");
            employee.appendChild(age);
            age.setAttribute("age", s[4]);
        }

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);
    }
}