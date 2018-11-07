package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC1Actores {

    private ArrayList<String> actores;
    private ArrayList<String> ciudades;
    private String jsonData;

    public getC1Actores(ArrayList<Document> docList, String anio, String pelicula){
	actores = new ArrayList<String>();
	for(int turn=0; turn<docList.size(); turn++){
		NodeList anioList = docList.get(turn).getElementsByTagName("Anio");

		for(int i=0; i<anioList.getLength(); i++){
			Node a = anioList.item(i);
			if(a.getNodeType() == Node.ELEMENT_NODE){
				Element thisAnio = (Element) a;
				if(thisAnio.getTextContent().equals(anio)){ //Encontramos el año
					NodeList pelisList = docList.get(turn).getElementsByTagName("Pelicula");
					for(int j=0; j<pelisList.getLength(); j++){
						Node p = pelisList.item(j);
						if(p.getNodeType() == Node.ELEMENT_NODE){
							Element thisPelicula = (Element) p;
							NodeList infoList = thisPelicula.getChildNodes();
							for(int k=0; k<infoList.getLength(); k++){
								Node in = infoList.item(k);
								if(in.getNodeType() == Node.ELEMENT_NODE){
									Element thisInfo = (Element) in;
									if(thisInfo.getTextContent().equals(pelicula)){ //Encontramos la película
										actores = actsFromMovie(thisPelicula);
										Collections.sort(actores, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
										getActCity(docList);
										JsonFormat(anio, pelicula);
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
    }

    private ArrayList<String> actsFromMovie(Element pelicula){
	
	ArrayList<String> respuesta = new ArrayList<String>();
	NodeList repartoList = pelicula.getChildNodes();
	for(int i=0; i<repartoList.getLength(); i++){
		Node r = repartoList.item(i);
		if(r.getNodeType() == Node.ELEMENT_NODE){
			Element name = (Element) r;
			if(name.getTagName().equals("Reparto")){
				NodeList nombreList = name.getChildNodes();
				for(int j=0; j<nombreList.getLength(); j++){
					Node n = nombreList.item(j);
					if(n.getNodeType() == Node.ELEMENT_NODE){
						Element actor = (Element) n;
						if(actor.getTagName().equals("Nombre")){
							respuesta.add(actor.getTextContent().toString());
							
						}
					}
				}
			}
		}
	}
	return respuesta;
    }

    private void getActCity(ArrayList<Document> docList){
	ciudades = new ArrayList<String>();
	for(int i = 0; i < actores.size(); i++){
		ciudades.add(thisCity(docList, actores.get(i)));
	}
    }

    private String thisCity(ArrayList<Document> docList, String act){
	String city = "";
	boolean actFound = false;
	for(int turn=0; turn<docList.size(); turn++){
		NodeList repartoList = docList.get(turn).getElementsByTagName("Reparto");
		for(int i=0; i<repartoList.getLength(); i++){
			actFound = false;
			Node r = repartoList.item(i);
			if(r.getNodeType() == Node.ELEMENT_NODE){
				Element reparto = (Element) r;
				NodeList nameList = reparto.getChildNodes();
				for(int j=0; j<nameList.getLength(); j++){
					Node n = nameList.item(j);
					if(n.getNodeType() == Node.ELEMENT_NODE){
						Element name = (Element) n;
						if(name.getTextContent().equals(act)){ //Encontramos al actor
							actFound = true;
						}
					}
					if(n.getNodeType() == Node.TEXT_NODE && !n.getTextContent().trim().equals("")){
						city = n.getTextContent().trim();	
					}
					if(j == nameList.getLength() - 1 && actFound){ //He recorrido toda la lista de nodos de este Reparto
						return city;
					}
				}
			}
		}
	}
	return city;
    }

    public void JsonFormat(String panio, String ppelicula){
	jsonData = "[{\"anio\" : \"" + panio + "\"},"; //Año seleccionado anteriormente
	jsonData += "{\"pelicula\" : \"" + ppelicula + "\"},"; //Película seleccionada anteriormente
	jsonData += "{\"reparto\" : {\"act\" : [";
	for(int i=0; i<actores.size(); i++){
		jsonData += "{\"nombre\" : \"" + actores.get(i) + "\", \"ciudad\" : \"" + ciudades.get(i) + "\"}";
		if(i < actores.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}]";
    }

    public ArrayList<String> getActores(){
	return actores;
    }

    public ArrayList<String> getCiudades(){
	return ciudades;
    }

    public String getJsonData(){
	return jsonData;
    }
}
