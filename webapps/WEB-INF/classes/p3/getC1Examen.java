package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC1Examen {

    private String anio;
    private int oscares;
    private String color;
    private String jsonData;

    public getC1Examen(Document examenDoc, ArrayList<Document> docList){
	//Descubrir qué año se pide
	anio = "";
	NodeList examenList = examenDoc.getElementsByTagName("examen");
	for(int i = 0; i < examenList.getLength(); i++){
		Node e = examenList.item(i);

		if(e.getNodeType() == Node.ELEMENT_NODE){
			Element examen = (Element) e;
			NodeList nameList = examen.getChildNodes();
			for(int j=0; j<nameList.getLength(); j++){
				Node n = nameList.item(j);

				//Anio
				if(n.getNodeType() == Node.TEXT_NODE && !n.getTextContent().trim().equals("")){
					anio = n.getTextContent().trim();
				}
			}
		}
	}
	XmlExamenOscares(examenDoc, docList);
	XmlExamenColor(examenDoc);
	JsonFormat();
    }

    private void XmlExamenOscares(Document examenDoc, ArrayList<Document> docList){
	oscares = 0;
	String thisOscar = "";
	//Oscar a buscar
	NodeList examenList = examenDoc.getElementsByTagName("oscar");
	for(int i = 0; i < examenList.getLength(); i++){
		Node e = examenList.item(i);

		if(e.getNodeType() == Node.ELEMENT_NODE){
			Element examen = (Element) e;
			thisOscar = examen.getTextContent();
		}
	}

	for(int turn=0; turn<docList.size(); turn++){
		NodeList anioList = docList.get(turn).getElementsByTagName("Anio");
		for(int i=0; i<anioList.getLength(); i++){
			Node a = anioList.item(i);
			if(a.getNodeType() == Node.ELEMENT_NODE){
				Element an = (Element) a;
				if(an.getTextContent().equals(anio)){ //Encontramos el año
					NodeList oscarList = docList.get(turn).getElementsByTagName("Oscar");
					for(int j=0; j<oscarList.getLength(); j++){
						Node o = oscarList.item(j);
						if(o.getNodeType() == Node.ELEMENT_NODE){
							Element oscar = (Element) o;
							if(oscar.getTextContent().equals(thisOscar)){
								oscares++;
							}
						}
					}
					return;
				}
			}
		}
	}
    }

    private void XmlExamenColor(Document examenDoc){
	//Descubrir qué color se pide
	color = "";
	NodeList examenList = examenDoc.getElementsByTagName("oscar");
	for(int i = 0; i < examenList.getLength(); i++){
		Node e = examenList.item(i);

		if(e.getNodeType() == Node.ELEMENT_NODE){
			Element examen = (Element) e;
			color = examen.getAttribute("decoracion");
		}
	}
    }

    public void JsonFormat(){
	jsonData = "{\"anio\" : \"" + anio + "\"}";
	jsonData += "{\"oscares\" : \"" + oscares + "\"}";
	jsonData += "{\"color\" : \"" + color + "\"}";
    }

    public String getAnio(){
	return anio;
    }

    public int getOscares(){
	return oscares;
    }

    public String getColor(){
	return color;
    }

    public String getJsonData(){
	return jsonData;
    }
}
