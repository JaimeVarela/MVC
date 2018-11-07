package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC1Filmografia {

    String nombrePersonaje;
    ArrayList<String> filmografia;
    ArrayList<String> oscares;
    String jsonData;

    public getC1Filmografia(ArrayList<Document> docList, String anio, String pelicula, String actor){
	nombrePersonaje = getCharacterName(docList, anio, pelicula, actor); //Nombre del personaje

	//Se obtiene las id de las películas
	ArrayList<String> film = new ArrayList<String>();
	for(int turn=0; turn<docList.size(); turn++){
		NodeList anioList = docList.get(turn).getElementsByTagName("Anio");

		for(int i=0; i<anioList.getLength(); i++){
			Node a = anioList.item(i);
			if(a.getNodeType() == Node.ELEMENT_NODE){
				Element thisAnio = (Element) a;
				NodeList pelisList = docList.get(turn).getElementsByTagName("Pelicula");
				for(int j=0; j<pelisList.getLength(); j++){
					Node p = pelisList.item(j);
					if(p.getNodeType() == Node.ELEMENT_NODE){
						Element thisPelicula = (Element) p;
						ArrayList<String> actores = new ArrayList<String>();
						actores = actsFromMovie(docList, thisPelicula);
						for(int k=0; k<actores.size(); k++){
							if(actores.get(k).equals(actor)){ //El actor ha participado en esta película
								film.add(thisPelicula.getAttribute("ip"));
							}
						}
					}
				}
			}
		}
	}
	//Ordenar por el título de la película
	ArrayList<String> filmografiaPrintOrder = new ArrayList<String>();
	while(true){
		//Si sólo queda una peli en la lista, terminamos
		if(film.size() == 1){
			filmografiaPrintOrder.add(film.get(0));
			break;
		}
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0; i < film.size(); i++){
			String name = titlePelicula(docList, film.get(i));
			nameList.add(name);		
		}
		//Ordenamos la lista
		Collections.sort(nameList, String.CASE_INSENSITIVE_ORDER);
		//Comprobamos a qué película le corresponde el primer título de la lista
		for(int i=0; i<film.size(); i++){
			if(nameList.get(0).equals(titlePelicula(docList, film.get(i)))){
				filmografiaPrintOrder.add(film.get(i));
				film.remove(i);
				break;
			}
		}
	}

	//Ahora hay que ordenarlo por óscar
	ArrayList<String> IdFilmografia = new ArrayList<String>();
	oscares = new ArrayList<String>();
	for(int i=0; i<filmografiaPrintOrder.size(); i++){
		String oscar = getOscarFromMovie(docList, filmografiaPrintOrder.get(i), actor);
		if(oscar.equals("")){
			IdFilmografia.add(filmografiaPrintOrder.get(i));
			oscares.add(oscar);
		}
	}
	for(int i=0; i<filmografiaPrintOrder.size(); i++){
		String oscar = getOscarFromMovie(docList, filmografiaPrintOrder.get(i), actor);
		if(oscar.equals("Principal")){
			IdFilmografia.add(filmografiaPrintOrder.get(i));
			oscares.add(oscar);
		}
	}
	for(int i=0; i<filmografiaPrintOrder.size(); i++){
		String oscar = getOscarFromMovie(docList, filmografiaPrintOrder.get(i), actor);
		if(oscar.equals("Secundario")){
			IdFilmografia.add(filmografiaPrintOrder.get(i));
			oscares.add(oscar);
		}
	}
	//Finalmente obtenemos los nombres de las películas
	filmografia = new ArrayList<String>();
	for(int i=0; i<IdFilmografia.size(); i++){
		filmografia.add(titlePelicula(docList, IdFilmografia.get(i)));
	}
	JsonFormat(anio, pelicula, actor);
    }

    private String getCharacterName(ArrayList<Document> docList, String anio, String pelicula, String actor){
	String personaje = "";
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
										personaje = getCharacter(docList, thisPelicula, actor);
										return personaje;
									}
								}
							}
						}
					}
				}
			}
		}
	}	
	return personaje;
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

    private String getCharacter(ArrayList<Document> docList, Element pelicula, String actor){
	
	String personaje = "";
	boolean actFound = false;
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
						Element element = (Element) n;
						if(element.getTagName().equals("Nombre") && element.getTextContent().equals(actor)){ //Encontramos al actor
							actFound = true;
						}
						if(element.getTagName().equals("Personaje")){
							personaje = element.getTextContent();
						}
					}
					if(j == nombreList.getLength() -1 && actFound){ //Si hemos obtenido el nombre del actor que buscábamos
						return personaje;
					}
				}
			}
		}
	}
	
	return personaje;
    }


    private String getOscarFromMovie(ArrayList<Document> docList, String pelicula, String actor){
	String oscar = "";
	boolean actFound = false;
	for(int turn=0; turn<docList.size(); turn++){
		NodeList peliculaList = docList.get(turn).getElementsByTagName("Pelicula");
		for(int i=0; i<peliculaList.getLength(); i++){
			Node p = peliculaList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element thisPelicula = (Element) p;
				if(thisPelicula.getAttribute("ip").equals(pelicula)){ //Encontramos la película
					NodeList repartoList = thisPelicula.getChildNodes();
					for(int j=0; j<repartoList.getLength(); j++){
						actFound = false;
						Node r = repartoList.item(j);
						if(r.getNodeType() == Node.ELEMENT_NODE){
							Element reparto = (Element) r;
							if(reparto.getTagName().equals("Reparto")){
								NodeList infoList = reparto.getChildNodes();
								for(int k=0; k<infoList.getLength(); k++){
									Node in = infoList.item(k);
									if(in.getNodeType() == Node.ELEMENT_NODE){
										Element info = (Element) in;
										if(info.getTextContent().equals(actor)){ //Encontramos al actor
											actFound = true;
										}
										if(info.getTagName().equals("Oscar")){
											oscar = info.getTextContent();
										}
									}
									if(k == infoList.getLength() - 1){
										if(actFound){
											return oscar;
										}
									
										else{
											oscar="";
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
	return oscar;
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

    public void JsonFormat(String panio, String ppelicula, String pact){
	jsonData = "[{\"anio\" : \"" + panio + "\"},"; //Año seleccionado anteriormente
	jsonData += "{\"pelicula\" : \"" + ppelicula + "\"},"; //Película seleccionada anteriormente
	jsonData += "{\"filmografia\" : {";
	jsonData += "\"nombre\" : \"" + pact + "\","; //Actor seleccionado anteriormente
	jsonData += "\"personaje\" : \"" + nombrePersonaje + "\",";
	jsonData += "\"film\" : [";
	for(int i=0; i<filmografia.size(); i++){
		jsonData += "{\"nombre\" : \"" + filmografia.get(i) + "\", \"oscar\" : \"" + oscares.get(i) + "\"}";
		if(i < filmografia.size() - 1){
			jsonData += ", ";
		}
	}
	jsonData += "]}}]";
    }

    public String getNombrePersonaje(){
	return nombrePersonaje;
    }

    public ArrayList<String> getFilmografia(){
	return filmografia;
    }

    public ArrayList<String> getOscares(){
	return oscares;
    }

    public String getJsonData(){
	return jsonData;
    }
}
