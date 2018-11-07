package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC2Langs {

    ArrayList<String> langs;
    String jsonData;

    public getC2Langs(ArrayList<Document> docList){
	ArrayList<String> allLangs = new ArrayList<String>();
	for(int turn=0; turn<docList.size(); turn++){
		NodeList peliculaList = docList.get(turn).getElementsByTagName("Pelicula");

		for(int i=0; i<peliculaList.getLength(); i++){
			Node p = peliculaList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pelicula = (Element) p;
				String lang = pelicula.getAttribute("langs");
				if(lang.equals("")){ //La película no tiene idioma. Se comprueba el del país
					NodeList paisList = docList.get(turn).getElementsByTagName("Pais");
					for(int j=0; j<paisList.getLength(); j++){
						Node pa = paisList.item(j);
						if(pa.getNodeType() == Node.ELEMENT_NODE){
							Element pais = (Element) pa;
							NodeList peliCompareList = pais.getChildNodes();
							for(int k=0; k<peliCompareList.getLength(); k++){
								Node pe = peliCompareList.item(k);
								if(pe.getNodeType() == Node.ELEMENT_NODE){
									Element peliCompare = (Element) pe;
									if(peliCompare.equals(pelicula)){
										lang = pais.getAttribute("lang");
									}
								}
							}
						}
					}
				}		
				String[] idiomas = lang.split(" "); //Una película puede estar en varios idiomas
			
				for(int j=0; j<idiomas.length; j++){				
					allLangs.add(idiomas[j]);					
				}
			}
		}
	}
	//En nuestra lista tenemos repetidos los mismos idiomas
	langs = new ArrayList<String>();
	for(int i=0; i<allLangs.size(); i++){
		if(langs.size() == 0){ //Añadimos el primero de la lista
			langs.add(allLangs.get(i));
		}
		else{
			for(int j=0; j<langs.size(); j++){
				if(allLangs.get(i).equals(langs.get(j))){
					break; //Si encontramos una coincidencia, ya no vamos a añadirlo
				}
				if(j == langs.size() - 1){ //Hemos recorrido toda la lista sin encontrar una coincidencia
					langs.add(allLangs.get(i));
				}
			}
		}
	}
	Collections.sort(langs, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
	Collections.reverse(langs); //En orden inverso
	JsonFormat();
    }

    public void JsonFormat(){
	jsonData = "{\"langs\" : {\"lang\" : [";
	for(int i=0; i<langs.size(); i++){
		jsonData += "\"" + langs.get(i) + "\"";
		if(i < langs.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}";
    }

    public ArrayList<String> getLangs(){
	return langs;
    }

    public String getJsonData(){
	return jsonData;
    }
}
