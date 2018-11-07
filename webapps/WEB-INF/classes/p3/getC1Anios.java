package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC1Anios {

    private ArrayList<String> anios;
    private String jsonData;

    public getC1Anios(ArrayList<Document> docList){

	anios = new ArrayList<String>();
	for(int turn=0; turn<docList.size(); turn++){
		NodeList anioList = docList.get(turn).getElementsByTagName("Anio");

		for(int i=0; i<anioList.getLength(); i++){
			Node a = anioList.item(i);
			if(a.getNodeType() == Node.ELEMENT_NODE){
				Element anio = (Element) a;
				anios.add(anio.getTextContent());
			}
		}
	}
	Collections.sort(anios, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos cronológicamente
	JsonFormat();
    }

    public void JsonFormat(){
	jsonData = "{\"anios\" : {\"anio\" : [";
	for(int i=0; i<anios.size(); i++){
		jsonData += "\"" + anios.get(i) + "\"";
		if(i < anios.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}";
    }

    public ArrayList<String> getAnios(){
	return anios;
    }

    public String getJsonData(){
	return jsonData;
    }
}
