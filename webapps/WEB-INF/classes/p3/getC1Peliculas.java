package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC1Peliculas {

    private ArrayList<String> peliculas;
    private ArrayList<Integer> duracion;
    private ArrayList<String> idioma;
    private String jsonData;

    public getC1Peliculas(ArrayList<Document> docList, String anio){

	ArrayList<String> pelis = new ArrayList<String>();
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
							Element pelicula = (Element) p;
							pelis.add(pelicula.getAttribute("ip"));
						}
					}
				}
			}
		}
	}
	Collections.sort(pelis, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
	Collections.reverse(pelis); //En orden inverso

	//Ordenar por duración de la película en orden ascendente
	ArrayList<String> pelisPrintOrder = new ArrayList<String>();
	duracion = new ArrayList<Integer>();
	while(true){
		//Si sólo queda un país en la lista, terminamos
		if(pelis.size() == 1){
			pelisPrintOrder.add(pelis.get(0));
			duracion.add(durationPelicula(docList, pelis.get(0)));
			break;
		}
		ArrayList<Integer> duraciones = new ArrayList<Integer>();
		for(int i = 0; i < pelis.size(); i++){
			int dur = durationPelicula(docList, pelis.get(i));
			duraciones.add(dur);
		}
		//Buscamos el número más bajo, y guardamos la película correspondiente
		int minNum = 300;
		int index = 0;
		for(int i = 0; i < duraciones.size(); i++){
			if(duraciones.get(i) < minNum){
				minNum = duraciones.get(i);
				index = i;
			}
		}
		//Guardamos el país en la nueva lista y lo eliminamos de la antigua
		pelisPrintOrder.add(pelis.get(index));
		duracion.add(duraciones.get(index));
		pelis.remove(index);
	}
	//Hallar el nombre y el idioma de las películas
	peliculas = new ArrayList<String>();
	idioma = new ArrayList<String>();
	for(int i = 0; i < pelisPrintOrder.size(); i++){
		peliculas.add(titlePelicula(docList, pelisPrintOrder.get(i))); //Título
		idioma.add(languagePelicula(docList, pelisPrintOrder.get(i))); //Idioma
	}
	JsonFormat(anio);
    }

    private int durationPelicula(ArrayList<Document> docList, String ip){

	int duracion = 0;
	for(int turn=0; turn<docList.size(); turn++){
		NodeList peliculaList = docList.get(turn).getElementsByTagName("Pelicula");
		for(int i=0; i<peliculaList.getLength(); i++){
			Node p = peliculaList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pelicula = (Element) p;
				if(pelicula.getAttribute("ip").equals(ip)){ //Encontramos la ip de la pelicula
					NodeList infoList = pelicula.getChildNodes();
					for(int j=0; j<infoList.getLength(); j++){
						Node in = infoList.item(j);
						if(in.getNodeType() == Node.ELEMENT_NODE){
							Element info = (Element) in;
							if(info.getTagName().equals("Duracion")){
								return duracion = Integer.parseInt(info.getTextContent());
							}
						}
					}
				}
			}
		}
	}
	return duracion;
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

    private String languagePelicula(ArrayList<Document> docList, String ip){

	String lang = "";
	for(int turn=0; turn<docList.size(); turn++){
		NodeList peliculaList = docList.get(turn).getElementsByTagName("Pelicula");
		for(int i=0; i<peliculaList.getLength(); i++){
			Node p = peliculaList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pelicula = (Element) p;
				if(pelicula.getAttribute("ip").equals(ip)){ //Encontramos la ip de la pelicula
					lang = pelicula.getAttribute("langs");
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
					else{
						return lang;
					}
				}
			}
		}
	}
	return lang;
    }

    public void JsonFormat(String panio){
	jsonData = "[{\"anio\" : \"" + panio + "\"},"; //Año seleccionado anteriormente
	jsonData += "{\"peliculas\" : {\"pelicula\" : [";
	for(int i=0; i<peliculas.size(); i++){
		jsonData += "{\"nombre\" : \"" + peliculas.get(i) + "\", \"duracion\" : \"" + duracion.get(i) + "\", \"langs\" : \"" + idioma.get(i) + "\"}";
		if(i < peliculas.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}]";
    }

    public ArrayList<String> getPeliculas(){
	return peliculas;
    }

    public ArrayList<Integer> getDuracion(){
	return duracion;
    }

    public ArrayList<String> getIdioma(){
	return idioma;
    }

    public String getJsonData(){
	return jsonData;
    }
}
