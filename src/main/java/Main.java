import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        //Задача 1: CSV - JSON парсер
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listTask1 = parseCSV(columnMapping, fileName);
        String csvJson = listToJson(listTask1);
        String fileNameTask1 = "data.json";
        writeString(csvJson, fileNameTask1);

        //Задача 2: XML - JSON парсер
        String xmlFile = "data.xml";
        List<Employee> listTask2 = parseXML(xmlFile);
        String xmlJson = listToJson(listTask2);
        String fileNameTask2 = "data2.json";
        writeString(xmlJson, fileNameTask2);

        //Задача 3: JSON парсер (со звездочкой *)
        String jsonTask3 = readString("new_data.json");
        //System.out.println(jsonTask3);
        List<Employee> listTask3 = jsonToList(jsonTask3);
        listTask3.forEach(System.out::println);

    }

    private static List<Employee> jsonToList(String json) {

        List<Employee> staff = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try {
            Object obj = new JSONParser().parse(json);
            JSONArray array = (JSONArray) obj;
            for (Object emp : array) {
                Employee employee = gson.fromJson(emp.toString(), Employee.class);
                staff.add(employee);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return staff;

    }


    private static String readString(String fileName) {
        StringBuilder str = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");

        try {
            BufferedReader readJson = new BufferedReader(new FileReader(fileName));
            while ((line = readJson.readLine()) != null) {
                str.append(line);
                str.append(ls);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();
    }


    private static List<Employee> parseXML(String xmlFile) throws ParserConfigurationException,
            IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFile));
        Node root = doc.getDocumentElement();
        List<Employee> list = new ArrayList<>();
        read(root, list);

        return list;
    }


    private static void read(Node node, List<Employee> list) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {

                if (node_.getNodeName().equals("employee")) {
                    Element element = (Element) node_;
                    list.add(new Employee(
                            Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                    ));
                }

            }

        }
    }


    private static void writeString(String json, String fileName) {

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);

        return json;

    }


    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
