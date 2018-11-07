package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC2Paises {

    ArrayList<String> paises;
    ArrayList<Integer> numPeliculas;
    ArrayList<String> defLangs;
    String jsonData;

    public getC2Paises(ArrayList<Document> docList, String plang, String pact){

	ArrayList<String> allPaises = new ArrayList<String>();
	for(int turn=0; turn<docList.size(); turn++){
		NodeList paisesList = docList.get(turn).getElementsByTagName("Pais");
		for(int i=0; i<paisesList.getLength(); i++){
			Node p = paisesList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pais = (Element) p;
				ArrayList<String> pelis = new ArrayList<String>();
				pelis = pelisFromPais(docList, plang, pact, pais);
				if(pelis.size() != 0){
					allPaises.add(pais.getAttribute("pais"));
				}
			}
		}
	}
	//En nuestra lista tenemos repetidos los mismos países
	ArrayList<String> paisesPrintOrder = new ArrayList<String>();
	for(int i=0; i<allPaises.size(); i++){
		if(paisesPrintOrder.size() == 0){ //Añadimos el primero de la lista
			paisesPrintOrder.add(allPaises.get(i));
		}
		else{
			for(int j=0; j<paisesPrintOrder.size(); j++){
				if(allPaises.get(i).equals(paisesPrintOrder.get(j))){
					break; //Si encontramos una coincidencia, ya no vamos a añadirlo
				}
				if(j == paisesPrintOrder.size() - 1){ //Hemos recorrido toda la lista sin encontrar una coincidencia
					paisesPrintOrder.add(allPaises.get(i));
				}
			}
		}
	}
	Collections.sort(paisesPrintOrder, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente

	//Ordenar por número de películas producidas en cada país
	paises = new ArrayList<String>();
	numPeliculas = new ArrayList<Integer>();
	defLangs = new ArrayList<String>();
	while(true){
		//Si sólo queda un país en la lista, terminamos
		if(paisesPrintOrder.size() == 1){
			paises.add(paisesPrintOrder.get(0));
			numPeliculas.add(getNumberPeliculas(docList, paisesPrintOrder.get(0)));
			defLangs.add(getDefaultLangPais(docList, paisesPrintOrder.get(0)));
			break;
		}
		ArrayList<Integer> numberList = new ArrayList<Integer>();
		for(int i = 0; i < paisesPrintOrder.size(); i++){
			int number = getNumberPeliculas(docList, paisesPrintOrder.get(i));
			numberList.add(number);		
		}
		//Buscamos el número más alto, y guardamos el país correspondiente
		int maxNum = 0;
		int index = 0;
		for(int i = 0; i < numberList.size(); i++){
			if(numberList.get(i) > maxNum){
				maxNum = numberList.get(i);
				index = i;
			}
		}
		//Guardamos el país en la nueva lista y lo eliminamos de la antigua
		paises.add(paisesPrintOrder.get(index));
		numPeliculas.add(numberList.get(index));
		defLangs.add(getDefaultLangPais(docList, paisesPrintOrder.get(index)));
		paisesPrintOrder.remove(index);
	}
	JsonFormat(plang, pact);
    }

    private ArrayList<String> pelisFromPais(ArrayList<Document> docList, String plang, String pact, Element pais){
	ArrayList<String> pelis = new ArrayList<String>(); //Tendrá contenido si el actor pact participó en la película de idioma plang
	NodeList pelisList = pais.getChildNodes();
	for(int i=0; i<pelisList.getLength(); i++){
		Node p = pelisList.item(i);
		if(p.getNodeType() == Node.ELEMENT_NODE){
			Element pelicula = (Element) p;
			String lang = pelicula.getAttribute("langs");
			if(lang.equals("")){ //La película no tiene idioma. Se comprueba el del país
				lang = pais.getAttribute("lang");
			}		
			String[] idiomas = lang.split(" "); //Una película puede estar en varios idiomas
			for(int j=0; j<idiomas.length; j++){				
				if(idiomas[j].equals(plang)){ //Encontramos una película con el idioma seleccionado
					ArrayList<String> parcialActs = new ArrayList<String>();
					parcialActs = actsFromMovie(docList, pelicula);
					for(int k=0; k<parcialActs.size(); k++){
						if(parcialActs.get(k).equals(pact)){ //El actor ha participado en esta película
							pelis.add(pelicula.getAttribute("ip"));
						}
					}
					break;
				}				
			}
		}
	}
	return pelis;
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

    private int getNumberPeliculas(ArrayList<Document> docList, String ppais){
	int number = 0;
	for(int turn=0; turn<docList.size(); turn++){
		NodeList paisesList = docList.get(turn).getElementsByTagName("Pais");
		for(int i=0; i<paisesList.getLength(); i++){
			Node p = paisesList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pais = (Element) p;
				if(pais.getAttribute("pais").equals(ppais)){ //País encontrado
					NodeList pelisList = pais.getChildNodes();
					for(int j=0; j<pelisList.getLength(); j++){
						Node pe = pelisList.item(j);
						if(pe.getNodeType() == Node.ELEMENT_NODE){
							Element peli = (Element) pe;
							if(peli.getTagName().equals("Pelicula")){
								number++;
							}
						}
					}
				}
			}
		}
	}
	return number;
    }

    private String getDefaultLangPais(ArrayList<Document> docList, String ppais){

	String lang = "";
	for(int turn=0; turn<docList.size(); turn++){
		NodeList paisesList = docList.get(turn).getElementsByTagName("Pais");
		for(int i=0; i<paisesList.getLength(); i++){
			Node p = paisesList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pais = (Element) p;
				if(pais.getAttribute("pais").equals(ppais)){ //País encontrado
					lang = pais.getAttribute("lang");
				}
			}
		}
	}
	return lang;
    }

    public void JsonFormat(String plang, String pact){
	jsonData = "[{\"idioma\" : \"" + plang + "\"},"; //Idioma seleccionado anteriormente
	jsonData += "{\"actor\" : \"" + pact + "\"},"; //Actor seleccionado anteriormente
	jsonData += "{\"paises\" : {\"pais\" : [";
	for(int i=0; i<paises.size(); i++){
		jsonData += "{\"nombre\" : \"" + paises.get(i) + "\", \"lang\" : \"" + defLangs.get(i) + "\", \"num\" : \"" + numPeliculas.get(i) + "\"}";
		if(i < paises.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}]";
    }

    public ArrayList<String> getPaises(){
	return paises;
    }

    public ArrayList<Integer> getNumPeliculas(){
	return numPeliculas;
    }

    public ArrayList<String> getDefLangs(){
	return defLangs;
    }

    public String getJsonData(){
	return jsonData;
    }
}
