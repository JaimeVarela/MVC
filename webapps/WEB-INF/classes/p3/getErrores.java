package p3;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import java.io.File;

public class getErrores {

    public Document examenDoc;

    String xmlPath = "http://gssi.det.uvigo.es/users/agil/public_html/SINT/17-18/"; //Se le añadirá al final el archivo xml a analizar
    static String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    static String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    //Lista de errores, de tipo xml, y de tipo fichero (fichero xml no encontrado)
    public ArrayList <String> warning_xml;
    public ArrayList <String> cause_warning_xml;
    public ArrayList <String> file_warning_xml;
    public ArrayList <String> error_xml;
    public ArrayList <String> cause_error_xml;
    public ArrayList <String> file_error_xml;
    public ArrayList <String> fatalError_xml;
    public ArrayList <String> cause_fatalError_xml;
    public ArrayList <String> file_fatalError_xml;

    public ArrayList<Document> docList; //Lista de árboles DOM utilizados en las consultas
    public ArrayList<String> xmlList;

    String jsonData;

    //Flag que nos indica si se detectó un error de tipo xml en un determinado documento xml para descartar este por completo
    public boolean err_xml=false;
    
    public Document doc;

    //NumberErrores
    int number_warning_xml;
    int number_error_xml;
    int number_fatalError_xml;

    public getErrores(ServletConfig config){
	//Este será el schema empleado
	ServletContext servCont = config.getServletContext();
	String MY_SCHEMA = servCont.getRealPath("p3/mml.xsd");
	File xsdFile = new File(MY_SCHEMA);

	warning_xml = new ArrayList <String>();
	cause_warning_xml = new ArrayList <String>();
	file_warning_xml = new ArrayList <String>();
	error_xml = new ArrayList <String>();
	cause_error_xml = new ArrayList <String>();
	file_error_xml = new ArrayList <String>();
	fatalError_xml = new ArrayList <String>();
	cause_fatalError_xml = new ArrayList <String>();
	file_fatalError_xml = new ArrayList <String>();

	xmlList = new ArrayList<String>();
	xmlList.add("mml2001.xml"); //Se empieza por este archivo
	docList = new ArrayList<Document>();
	for(int i = 0; i < xmlList.size(); i++){
		try{
			String fXmlFile = xmlPath + xmlList.get(i);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			dbf.setNamespaceAware(true);
			dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			dbf.setAttribute(JAXP_SCHEMA_SOURCE, xsdFile);
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(new XML_XSD_ErrorHandler(this));
	
			doc = db.parse(fXmlFile);
		//Si el xml no está bien formado, lanzará una SAXEception
		}catch (SAXParseException spe){
			fatalError_xml.add(spe.toString());
			cause_fatalError_xml.add(spe.getMessage());
			file_fatalError_xml.add(spe.getSystemId());
			err_xml=true;
		}catch (Exception e){
			e.printStackTrace();
		}

		if(!err_xml){ //Si no hay errores se utilizarán los datos de este xml
			doc.getDocumentElement().normalize();
			docList.add(doc);
			xmlList = getXml(doc, xmlList);
		}
		err_xml = false;
	}
	NumberErrores();
	JsonFormat();
	Examen();
    }

    //Función para obtener los nombres de los ficheros xml contenidos en los campos MML
    private ArrayList<String> getXml(Document doc, ArrayList<String> xmlList){

	NodeList newXmlList = doc.getElementsByTagName("MML");
	for(int i = 0; i < newXmlList.getLength(); i++){
		Node n = newXmlList.item(i);
		if(n.getNodeType() == Node.ELEMENT_NODE){
			Element xmlE = (Element) n;
			String xml = xmlE.getTextContent();
			for(int j = 0; j < xmlList.size(); j++){ //Nos aseguramos que no guardamos más de un único xml
				if(xmlList.get(j).equals(xml)){
					break;
				}
				if(j == xmlList.size() -1){
					xmlList.add(xml);
				}
			}
		}
	}
	return xmlList;
    }

    public void NumberErrores(){
	int number = 0;
	number_warning_xml = 0;
	if(file_warning_xml.size() != 0){
		String xmlActual = file_warning_xml.get(0);
		number++;
		for(int i = 0; i < file_warning_xml.size(); i++){
			if(!file_warning_xml.get(i).equals(xmlActual)){
				xmlActual = file_warning_xml.get(i);
				number++;
			}
		}
	}
	number_warning_xml = number;
	number = 0;
	number_error_xml = 0;
	if(file_error_xml.size() != 0){
		String xmlActual = file_error_xml.get(0);
		number++;
		for(int i = 0; i < file_error_xml.size(); i++){
			if(!file_error_xml.get(i).equals(xmlActual)){
				xmlActual = file_error_xml.get(i);
				number++;
			}
		}
	}
	number_error_xml = number;
	number = 0;
	number_fatalError_xml = 0;
	if(file_fatalError_xml.size() != 0){
		String xmlActual = file_fatalError_xml.get(0);
		number++;
		for(int i = 0; i < file_fatalError_xml.size(); i++){
			if(!file_fatalError_xml.get(i).equals(xmlActual)){
				xmlActual = file_fatalError_xml.get(i);
				number++;
			}
		}
	}
	number_fatalError_xml = number;
	number = 0;
    }

    public void JsonFormat(){
	jsonData = "{\"errores\" : [";

	jsonData += "{\"warnings\" : [";
	if(warning_xml.size() != 0){
		String xmlActual = file_warning_xml.get(0);
		ArrayList<String> causas = new ArrayList<String>();
		for(int i = 0; i < warning_xml.size(); i++){
			if(!file_warning_xml.get(i).equals(xmlActual)){
				jsonData += "{\"file\" : \"" + xmlActual + "\", ";
				jsonData += "\"causes\" : [";
				for(int j = 0; j < causas.size(); j++){
					jsonData += "{\"cause\" : \"" + causas + "\"}";
					if(j < causas.size() - 1){
						jsonData += ", ";
					}
				}
				jsonData += "]}, ";
				xmlActual = file_warning_xml.get(i);
				causas = new ArrayList<String>();
			}
			else{ //Vamos recopilando las causas sobre el mismo fichero
				causas.add(cause_warning_xml.get(i));
			}
		}
		jsonData += "{\"file\" : \"" + xmlActual + "\", ";
		jsonData += "\"causes\" : [";
		for(int j = 0; j < causas.size(); j++){
			jsonData += "{\"cause\" : \"" + causas + "\"}";
			if(j < causas.size() - 1){
				jsonData += ", ";
			}
		}
		jsonData += "]}";
	}
	jsonData += "]}, ";

	jsonData += "{\"errors\" : [";
	if(error_xml.size() != 0){
		String xmlActual = file_error_xml.get(0);
		ArrayList<String> causas = new ArrayList<String>();
		for(int i = 0; i < error_xml.size(); i++){
			if(!file_error_xml.get(i).equals(xmlActual)){
				jsonData += "{\"file\" : \"" + xmlActual + "\", ";
				jsonData += "\"causes\" : [";
				for(int j = 0; j < causas.size(); j++){
					jsonData += "{\"cause\" : \"" + causas + "\"}";
					if(j < causas.size() - 1){
						jsonData += ", ";
					}
				}
				jsonData += "]}, ";
				xmlActual = file_error_xml.get(i);
				causas = new ArrayList<String>();
			}
			else{ //Vamos recopilando las causas sobre el mismo fichero
				causas.add(cause_error_xml.get(i));
			}
		}
		jsonData += "{\"file\" : \"" + xmlActual + "\", ";
		jsonData += "\"causes\" : [";
		for(int j = 0; j < causas.size(); j++){
			jsonData += "{\"cause\" : \"" + causas + "\"}";
			if(j < causas.size() - 1){
				jsonData += ", ";
			}
		}
		jsonData += "]}";
	}
	jsonData += "]}, ";

	jsonData += "{\"fatalerrors\" : [";
	if(error_xml.size() != 0){
		String xmlActual = file_fatalError_xml.get(0);
		ArrayList<String> causas = new ArrayList<String>();
		for(int i = 0; i < fatalError_xml.size(); i++){
			if(!file_fatalError_xml.get(i).equals(xmlActual)){
				jsonData += "{\"file\" : \"" + xmlActual + "\", ";
				jsonData += "\"causes\" : [";
				for(int j = 0; j < causas.size(); j++){
					jsonData += "{\"cause\" : \"" + causas + "\"}";
					if(j < causas.size() - 1){
						jsonData += ", ";
					}
				}
				jsonData += "]}, ";
				xmlActual = file_fatalError_xml.get(i);
				causas = new ArrayList<String>();
			}
			else{ //Vamos recopilando las causas sobre el mismo fichero
				String causa = cause_fatalError_xml.get(i).replace("\"", "'");
				causas.add(causa);
			}
		}
		jsonData += "{\"file\" : \"" + xmlActual + "\", ";
		jsonData += "\"causes\" : [";
		for(int j = 0; j < causas.size(); j++){
			jsonData += "{\"cause\" : \"" + causas + "\"}";
			if(j < causas.size() - 1){
				jsonData += ", ";
			}
		}
		jsonData += "]}";
	}
	jsonData += "]}]}";
    }

    private void Examen(){
	String xmlExamen = "/home/bruno/public_html/webapps/p2/examen.xml";
	try{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		examenDoc = db.parse(xmlExamen);
		examenDoc.getDocumentElement().normalize();
	}
	catch(Exception e){
		e.printStackTrace();
	}
    }

    public ArrayList<Document> getDocList(){
	return docList;
    }

    public ArrayList<String> getFile_warning_xml(){
	return file_warning_xml;
    }

    public ArrayList<String> getCause_warning_xml(){
	return cause_warning_xml;
    }

    public int getNumber_warning_xml(){
	return number_warning_xml;
    }

    public ArrayList<String> getFile_error_xml(){
	return file_error_xml;
    }

    public ArrayList<String> getCause_error_xml(){
	return cause_error_xml;
    }

    public int getNumber_error_xml(){
	return number_error_xml;
    }

    public ArrayList<String> getFile_fatalError_xml(){
	return file_fatalError_xml;
    }

    public ArrayList<String> getCause_fatalError_xml(){
	return cause_fatalError_xml;
    }

    public int getNumber_fatalError_xml(){
	return number_fatalError_xml;
    }

    public String getJsonData(){
	return jsonData;
    }

    public Document getExamenDoc(){
	return examenDoc;
    }
}


class XML_XSD_ErrorHandler extends DefaultHandler {
    getErrores servlet;
    public XML_XSD_ErrorHandler (getErrores servlet){
	this.servlet = servlet;
    }
    public void warning(SAXParseException spe) throws SAXException{
	servlet.warning_xml.add(spe.toString());
	servlet.cause_warning_xml.add(spe.getMessage());
	servlet.file_warning_xml.add(spe.getSystemId());
    }
	
    public void error(SAXParseException spe) throws SAXException{
	servlet.error_xml.add(spe.toString());
	servlet.cause_error_xml.add(spe.getMessage());
	servlet.file_error_xml.add(spe.getSystemId());
	servlet.err_xml=true;
    }
	
    public void fatalerror(SAXParseException spe) throws SAXException{
	servlet.fatalError_xml.add(spe.toString());
	servlet.cause_fatalError_xml.add(spe.getMessage());
	servlet.file_fatalError_xml.add(spe.getSystemId());
	servlet.err_xml=true;
    }
}
