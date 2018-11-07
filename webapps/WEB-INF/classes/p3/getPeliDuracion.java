package p3;

import java.io.*;
import java.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class getC1Peliculas {

    private ArrayList<String> peliculas;

    public getC1Peliculas(ArrayList<Document> docList, String anio){

	peliculas = new ArrayList<String>();
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
							peliculas.add(pelicula.getAttribute("ip"));
						}
					}
				}
			}
		}
	}
	Collections.sort(peliculas, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
	Collections.reverse(peliculas); //En orden inverso
    }

    public ArrayList<String> getPeliculas(){
	return peliculas;
    }
}
