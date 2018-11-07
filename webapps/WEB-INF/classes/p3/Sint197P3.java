package p3;

import java.util.Date;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;
import java.io.File;

public class Sint197P3 extends HttpServlet {

    getC1Examen getExamen;
    public Document examenDoc;

    getErrores errores;
    getC1Anios getAnios;
    getC1Peliculas getPeliculas;
    getC1Actores getActores;
    getC1Filmografia getFilmografia;
    getC2Langs getLangs;
    getC2Acts getActs;
    getC2Paises getPaises;
    getC2Peliculas getPeliculas2;
    ServletConfig thisConfig;
    String password = "octsint197";


    public ArrayList<Document> docList; //Lista de árboles DOM utilizados en las consultas

    //Se ejecuta una vez, leyéndose los archivos xml y guardando los árboles generados de aquellos que hayan superado el parser del schema
    public void init(ServletConfig config){
	thisConfig = config; //Para poder emplearlo en doGet()
	errores = new getErrores(config);
	docList = new ArrayList<Document>(errores.getDocList());
	examenDoc = errores.getExamenDoc();
    }



    protected void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	ServletContext sc = thisConfig.getServletContext();

	//Este será el css empleado
	StringBuffer url = req.getRequestURL();
	URL servlet_url = new URL(url.toString());
	URL MY_CSS = new URL(servlet_url, "p3/mml.css");
	req.setAttribute("css", MY_CSS);

	if(req.getParameter("ajax") != null){
		String data = "";
		switch(req.getParameter("pfase")){
			case "02":
				data = errores.getJsonData();
				break;
			case "11":
				getAnios = new getC1Anios(docList);
				data = getAnios.getJsonData();
				break;
			case "12":
				getPeliculas = new getC1Peliculas(docList, req.getParameter("panio"));
				data = getPeliculas.getJsonData();
				break;
			case "13":
				getActores = new getC1Actores(docList, req.getParameter("panio"), req.getParameter("ppelicula"));
				data = getActores.getJsonData();
				break;
			case "14":
				getFilmografia = new getC1Filmografia(docList, req.getParameter("panio"), req.getParameter("ppelicula"), req.getParameter("pact"));
				data = getFilmografia.getJsonData();
				break;
			//EXAMEN
			case "15":
				getExamen = new getC1Examen(examenDoc, docList);
				data = getExamen.getJsonData();
				break;
			case "21":
				getLangs = new getC2Langs(docList);
				data = getLangs.getJsonData();
				break;
			case "22":
				getActs = new getC2Acts(docList, req.getParameter("plang"));
				data = getActs.getJsonData();
				break;
			case "23":
				getPaises = new getC2Paises(docList, req.getParameter("plang"), req.getParameter("pact"));
				data = getPaises.getJsonData();
				break;
			case "24":
				getPeliculas2 = new getC2Peliculas(docList, req.getParameter("plang"), req.getParameter("pact"), req.getParameter("ppais"));
				data = getPeliculas2.getJsonData();
				break;
		}
		res.setContentType("application/json");
		res.getWriter().write(data);
	}	

	//VENTANA DE FALTA DE CONTRASEÑA
	else if(req.getParameter("p") == null){
		RequestDispatcher rd = sc.getRequestDispatcher("/p3/noPassword.jsp");
		rd.forward(req,res);
	}

	//VENTANA DE CONTRASEÑA INCORRECTA
	else if(req.getParameter("p") != null && !req.getParameter("p").equals(password)){
		RequestDispatcher rd = sc.getRequestDispatcher("/p3/wrongPassword.jsp");
		rd.forward(req,res);
	}

	//VENTANA DE INICIO
	else if(req.getParameter("pfase") == null || req.getParameter("pfase").equals("01")){
		req.setAttribute("p", req.getParameter("p"));
		RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase01.jsp");
		rd.forward(req,res);
	}

	//VENTANA DE FICHEROS ERRÓNEOS
	else if(req.getParameter("pfase").equals("02")){
		req.setAttribute("p", req.getParameter("p"));
		req.setAttribute("errores", errores);
		RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase02.jsp");
		rd.forward(req,res);
	}

	/////////// VENTANAS DE LA PRIMERA CONSULTA ////////////////////

	//VENTANA DE SELECCIÓN DE AÑO
	else if(req.getParameter("pfase").equals("11")){
		req.setAttribute("p", req.getParameter("p"));
		getAnios = new getC1Anios(docList);
		req.setAttribute("anios", getAnios);
		RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase11.jsp");
		rd.forward(req,res);
	}

	//VENTANA DE SELECCIÓN DE PELÍCULA
	else if(req.getParameter("pfase").equals("12")){
		if(req.getParameter("panio") == null){
			req.setAttribute("parametro", "panio");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else{
			req.setAttribute("p", req.getParameter("p"));
			req.setAttribute("panio", req.getParameter("panio"));
			getPeliculas = new getC1Peliculas(docList, req.getParameter("panio"));
			req.setAttribute("peliculas", getPeliculas);
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase12.jsp");
			rd.forward(req,res);
		}
	}

	//VENTANA DE SELECCIÓN DE ACTOR
	else if(req.getParameter("pfase").equals("13")){
		if(req.getParameter("panio") == null){
			req.setAttribute("parametro", "panio");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else if(req.getParameter("ppelicula") == null){
			req.setAttribute("parametro", "ppelicula");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else{
			req.setAttribute("p", req.getParameter("p"));
			req.setAttribute("panio", req.getParameter("panio"));
			req.setAttribute("ppelicula", req.getParameter("ppelicula"));
			getActores = new getC1Actores(docList, req.getParameter("panio"), req.getParameter("ppelicula"));
			req.setAttribute("actores", getActores);
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase13.jsp");
			rd.forward(req,res);
		}
	}

	//VENTANA DE FILMOGRAFÍA DEL ACTOR
	else if(req.getParameter("pfase").equals("14")){
		if(req.getParameter("panio") == null){
			req.setAttribute("parametro", "panio");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else if(req.getParameter("ppelicula") == null){
			req.setAttribute("parametro", "ppelicula");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else if(req.getParameter("pact") == null){
			req.setAttribute("parametro", "pact");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else{
			req.setAttribute("p", req.getParameter("p"));
			req.setAttribute("panio", req.getParameter("panio"));
			req.setAttribute("ppelicula", req.getParameter("ppelicula"));
			req.setAttribute("pact", req.getParameter("pact"));
			getFilmografia = new getC1Filmografia(docList, req.getParameter("panio"), req.getParameter("ppelicula"), req.getParameter("pact"));
			req.setAttribute("filmografia", getFilmografia);
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase14.jsp");
			rd.forward(req,res);
		}
	}

	//EXAMEN
	else if(req.getParameter("pfase").equals("15")){
		req.setAttribute("p", req.getParameter("p"));
		getExamen = new getC1Examen(examenDoc, docList);
		req.setAttribute("examen", getExamen);
		RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase15.jsp");
		rd.forward(req,res);
	}

	/////////// VENTANAS DE LA SEGUNDA CONSULTA ////////////////////

	//VENTANA DE SELECCIÓN DEL IDIMA
	else if(req.getParameter("pfase").equals("21")){
		req.setAttribute("p", req.getParameter("p"));
		getLangs = new getC2Langs(docList);
		req.setAttribute("langs", getLangs);
		RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase21.jsp");
		rd.forward(req,res);
	}
	
	//VENTANA DE SELECCIÓN DE ACTOR
	else if(req.getParameter("pfase").equals("22")){
		if(req.getParameter("plang") == null){
			req.setAttribute("parametro", "plang");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else{
			req.setAttribute("p", req.getParameter("p"));
			req.setAttribute("plang", req.getParameter("plang"));
			getActs = new getC2Acts(docList, req.getParameter("plang"));
			req.setAttribute("acts", getActs);
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase22.jsp");
			rd.forward(req,res);
		}
	}

	//VENTANA DE SELECCIÓN DE PAÍS
	else if(req.getParameter("pfase").equals("23")){
		if(req.getParameter("plang") == null){
			req.setAttribute("parametro", "plang");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else if(req.getParameter("pact") == null){
			req.setAttribute("parametro", "pact");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else{
			req.setAttribute("p", req.getParameter("p"));
			req.setAttribute("plang", req.getParameter("plang"));
			req.setAttribute("pact", req.getParameter("pact"));
			getPaises = new getC2Paises(docList, req.getParameter("plang"), req.getParameter("pact"));
			req.setAttribute("paises", getPaises);
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase23.jsp");
			rd.forward(req,res);
		}
	}

	//VENTANA DE PELÍCULAS DEL ACTOR
	else if(req.getParameter("pfase").equals("24")){
		if(req.getParameter("plang") == null){
			req.setAttribute("parametro", "plang");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else if(req.getParameter("pact") == null){
			req.setAttribute("parametro", "pact");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else if(req.getParameter("ppais") == null){
			req.setAttribute("parametro", "ppais");
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/noParam.jsp");
			rd.forward(req,res);
		}
		else{
			req.setAttribute("p", req.getParameter("p"));
			req.setAttribute("plang", req.getParameter("plang"));
			req.setAttribute("pact", req.getParameter("pact"));
			req.setAttribute("ppais", req.getParameter("ppais"));
			getPeliculas2 = new getC2Peliculas(docList, req.getParameter("plang"), req.getParameter("pact"), req.getParameter("ppais"));
			req.setAttribute("peliculas", getPeliculas2);
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/fase24.jsp");
			rd.forward(req,res);
		}
	}

	//VENTANA DE MODO AJAX
	else if(req.getParameter("pfase").equals("30")){
			req.setAttribute("p", req.getParameter("p"));
			RequestDispatcher rd = sc.getRequestDispatcher("/p3/ajax.jsp");
			rd.forward(req,res);
	}
    }
}
