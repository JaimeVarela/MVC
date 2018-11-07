package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC2Peliculas {

    ArrayList<String> peliculas;
    ArrayList<String> ipPeliculas;
    String jsonData;

    public getC2Peliculas(ArrayList<Document> docList, String plang, String pact, String ppais){
	ipPeliculas = new ArrayList<String>(); //Ids de las peliculas
	//El país ppais se eligió en función del actor e idioma seleccionados
	for(int turn=0; turn<docList.size(); turn++){
		NodeList paisesList = docList.get(turn).getElementsByTagName("Pais");
		for(int i=0; i<paisesList.getLength(); i++){
			Node p = paisesList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pais = (Element) p;
				if(pais.getAttribute("pais").equals(ppais)){
					ArrayList<String> pelis = new ArrayList<String>();
					pelis = pelisFromPais(docList, plang, pact, pais);
					if(pelis.size() != 0){
						for(int j=0; j<pelis.size(); j++){
							ipPeliculas.add(pelis.get(j));
						}
					}
				}
			}
		}
	}
	Collections.sort(ipPeliculas, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente

	//Obtenemos el nombre de las películas
	peliculas = new ArrayList<String>();
	for(int i=0; i<ipPeliculas.size(); i++){
		peliculas.add(titlePelicula(docList, ipPeliculas.get(i)));
	}
	JsonFormat(plang, pact, ppais);
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

    private String titlePelicula(ArrayList<Document> docList, String pelicula){
	String title = "";
	for(int turn=0; turn<docList.size(); turn++){
		NodeList pelisList = docList.get(turn).getElementsByTagName("Pelicula");
		for(int i=0; i<pelisList.getLength(); i++){
			Node p = pelisList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element peli = (Element) p;
				if(peli.getAttribute("ip").equals(pelicula)){ //Película encontrada
					NodeList infoList = peli.getChildNodes();
					for(int j=0; j<infoList.getLength(); j++){
						Node in = infoList.item(j);
						if(in.getNodeType() == Node.ELEMENT_NODE){
							Element info = (Element) in;
							if(info.getTagName().equals("Titulo")){
								return title = info.getTextContent();
							}
						}
					}
				}
			}
		}
	}
	return title;
    }

    public void JsonFormat(String plang, String pact, String ppais){
	jsonData = "[{\"idioma\" : \"" + plang + "\"},"; //Idioma seleccionado anteriormente
	jsonData += "{\"actor\" : \"" + pact + "\"},"; //Actor seleccionado anteriormente
	jsonData += "{\"pais\" : \"" + ppais + "\"},"; //País seleccionado anteriormente
	jsonData += "{\"titulos\" : {\"titulo\" : [";
	for(int i=0; i<peliculas.size(); i++){
		jsonData += "{\"nombre\" : \"" + peliculas.get(i) + "\", \"ip\" : \"" + ipPeliculas.get(i)+ "\"}";
		if(i < peliculas.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}]";
    }

    public ArrayList<String> getPeliculas(){
	return peliculas;
    }

    public ArrayList<String> getIpPeliculas(){
	return ipPeliculas;
    }

    public String getJsonData(){
	return jsonData;
    }
}
