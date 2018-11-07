package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC2Acts {

    ArrayList<String> acts;
    ArrayList<String> oscares;
    ArrayList<String> ciudades;
    String jsonData;

    public getC2Acts(ArrayList<Document> docList, String plang){

	ArrayList<String> allActs = new ArrayList<String>();
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
					if(idiomas[j].equals(plang)){ //Hemos encontrado una película con el idioma seleccionado
						ArrayList<String> parcialActs = new ArrayList<String>();
						parcialActs = actsFromMovie(docList, pelicula);
						for(int k=0; k<parcialActs.size(); k++){
							allActs.add(parcialActs.get(k));
						}
						break;
					}				
				}
			}
		}
	}
	//En nuestra lista tenemos repetidos los mismos actores
	ArrayList<String> actsPrintOrder = new ArrayList<String>();
	for(int i=0; i<allActs.size(); i++){
		if(actsPrintOrder.size() == 0){ //Añadimos el primero de la lista
			actsPrintOrder.add(allActs.get(i));
		}
		else{
			for(int j=0; j<actsPrintOrder.size(); j++){
				if(allActs.get(i).equals(actsPrintOrder.get(j))){
					break; //Si encontramos una coincidencia, ya no vamos a añadirlo
				}
				if(j == actsPrintOrder.size() - 1){ //Hemos recorrido toda la lista sin encontrar una coincidencia
					actsPrintOrder.add(allActs.get(i));
				}
			}
		}
	}
	Collections.sort(actsPrintOrder, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente

	//Ordenamos de forma que nombramos primero a los actores con óscar
	acts = new ArrayList<String>();
	oscares = new ArrayList<String>();
	ciudades = new ArrayList<String>();
	for(int i = 0; i < actsPrintOrder.size(); i++){
		boolean oscar = getActOscar(docList, actsPrintOrder.get(i));
		if(oscar){
			acts.add(actsPrintOrder.get(i));
			oscares.add("con óscar");
			ciudades.add(getActCity(docList, actsPrintOrder.get(i)));
		}
	}
	for(int i = 0; i < actsPrintOrder.size(); i++){
		boolean oscar = getActOscar(docList, actsPrintOrder.get(i));
		if(!oscar){
			acts.add(actsPrintOrder.get(i));
			oscares.add("sin óscar");
			ciudades.add(getActCity(docList, actsPrintOrder.get(i)));
		}
	}
	JsonFormat(plang);
    }

    private ArrayList<String> actsFromMovie(ArrayList<Document> docList, Element pelicula){
	
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

    private boolean getActOscar(ArrayList<Document> docList, String act){
	boolean actFound = false;
	boolean haveOscar = false;
	for(int turn=0; turn<docList.size(); turn++){
		NodeList repartoList = docList.get(turn).getElementsByTagName("Reparto");
		for(int i=0; i<repartoList.getLength(); i++){
			actFound = false;
			haveOscar = false;
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
						if(name.getTagName().equals("Oscar")){
							haveOscar = true;
						}
					}				
					if(j == nameList.getLength() - 1 && actFound && haveOscar){ //He recorrido toda la lista de nodos de este Reparto
						return true; //Sólo nos importa si tiene un óscar en ALGUNA película. Si en ésta no tiene, buscamos si participó en otras
					}
				}
			}
		}
	}
	return false;
    }

    private String getActCity(ArrayList<Document> docList, String act){
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

    public void JsonFormat(String plang){
	jsonData = "[{\"idioma\" : \"" + plang + "\"},"; //Idioma seleccionado anteriormente
	jsonData += "{\"acts\" : {\"act\" : [";
	for(int i=0; i<acts.size(); i++){
		jsonData += "{\"nombre\" : \"" + acts.get(i) + "\", \"ciudad\" : \"" + ciudades.get(i) + "\", \"oscar\" : \"" + oscares.get(i) + "\"}";
		if(i < acts.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}]";
    }

    public ArrayList<String> getActs(){
	return acts;
    }

    public ArrayList<String> getOscares(){
	return oscares;
    }

    public ArrayList<String> getCiudades(){
	return ciudades;
    }

    public String getJsonData(){
	return jsonData;
    }
}
