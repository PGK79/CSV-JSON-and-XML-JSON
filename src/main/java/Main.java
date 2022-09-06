import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String[] employee = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Ivan,Petrov,RU,23".split(",");

        List<String[]> allEmployee = new ArrayList<>();
        allEmployee.add(employee);
        allEmployee.add(employee2);

        String fileNameCsv = "data.csv";
        String fileNameJson = "data.json";
        String fileNameJson2 = "data2.json";
        String fileNameXml = "data.xml";

        CreatFiles.creatCsv(fileNameCsv, allEmployee);

        List<Employee> list = parseCSV(columnMapping, fileNameCsv);
        String s = listToJson(list);
        writeString(s, fileNameJson);

        CreatFiles.creatXml(fileNameXml, allEmployee);

        List<Employee> list2 = parseXML(fileNameXml);
        String s2 = listToJson(list2);
        writeString(s2, fileNameJson2);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Employee> parseXML(String fileNameXml) throws
            ParserConfigurationException, IOException, SAXException {

        List<Employee> employees = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileNameXml));
        Node root = doc.getDocumentElement();

        Queue<String> result = read(root, queue);

        while (!result.isEmpty()) {
            long id = Long.parseLong(result.poll());
            String firstName = result.poll();
            String lastName = result.poll();
            String country = result.poll();
            int age = Integer.parseInt(result.poll());

            employees.add(new Employee(id, firstName, lastName, country, age));
        }
        return employees;
    }

    public static Queue<String> read(Node node, Queue<String> queue) {
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);

            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Element element = (Element) node_;
                NamedNodeMap map = element.getAttributes();

                for (int a = 0; a < map.getLength(); a++) {
                    String attrValue = map.item(a).getNodeValue();
                    queue.add(attrValue);
                }
            }
            read(node_, queue);
        }
        return queue;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }
                .getType();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String s, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(s);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}