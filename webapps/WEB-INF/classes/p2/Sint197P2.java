package p2;

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

public class Sint197P2 extends HttpServlet {

    Document examenDoc;

    String password = "octsint197";
    URL MY_CSS;

    String xmlPath = "http://gssi.det.uvigo.es/users/agil/public_html/SINT/17-18/"; //Se le añadirá al final el archivo xml a analizar
    static String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    static String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    //Lista de errores, de tipo xml, y de tipo fichero (fichero xml no encontrado)
    public ArrayList <String> warning_xml;
    public ArrayList <String> cause_warning_xml;
    public ArrayList <String> file_warning_xml;
    public int number_warning_xml;
    public ArrayList <String> error_xml;
    public ArrayList <String> cause_error_xml;
    public ArrayList <String> file_error_xml;
    public int number_error_xml;
    public ArrayList <String> fatalError_xml;
    public ArrayList <String> cause_fatalError_xml;
    public ArrayList <String> file_fatalError_xml;
    public int number_fatalError_xml;

    //Flag que nos indica si se detectó un error de tipo xml en un determinado documento xml para descartar este por completo
    public boolean err_xml=false;
    
    public Document doc;
    public ArrayList<Document> docList; //Lista de árboles DOM utilizados en las consultas
    public ArrayList<String> xmlList;

    //Se ejecuta una vez, leyéndose los archivos xml y guardando los árboles generados de aquellos que hayan superado el parser del schema
    public void init(ServletConfig config){

	//Este será el schema empleado
	ServletContext servCont = config.getServletContext();
	String MY_SCHEMA = servCont.getRealPath("p2/mml.xsd");
	File xsdFile = new File(MY_SCHEMA);

	warning_xml = new ArrayList <String>();
	cause_warning_xml = new ArrayList <String>();
	file_warning_xml = new ArrayList <String>();
	error_xml = new ArrayList <String>();
	cause_error_xml = new ArrayList <String>();
	file_error_xml = new ArrayList <String>();
	fatalError_xml = new ArrayList <String>();
	cause_fatalError_xml = new ArrayList <String>();
	file_fatalError_xml = new ArrayList <String>();

	xmlList = new ArrayList<String>();
	xmlList.add("mml2001.xml"); //Se empieza por este archivo
	docList = new ArrayList<Document>();
	for(int i = 0; i < xmlList.size(); i++){
		try{
			String fXmlFile = xmlPath + xmlList.get(i);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			dbf.setNamespaceAware(true);
			dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			dbf.setAttribute(JAXP_SCHEMA_SOURCE, xsdFile);
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(new XML_XSD_ErrorHandler(this));
	
			doc = db.parse(fXmlFile);
		//Si el xml no está bien formado, lanzará una SAXEception
		}catch (SAXParseException spe){
			fatalError_xml.add(spe.toString());
			cause_fatalError_xml.add(spe.getMessage());
			file_fatalError_xml.add(spe.getSystemId());
			err_xml=true;
		}catch (Exception e){
			e.printStackTrace();
		}

		if(!err_xml){ //Si no hay errores se utilizarán los datos de este xml
			doc.getDocumentElement().normalize();
			docList.add(doc);
			xmlList = getXml(doc, xmlList);
		}
		err_xml = false;
	}
	NumberFiles();
	Examen();
    }

    private void Examen(){
	String xmlExamen = "/home/bruno/public_html/webapps/p2/examen.xml";
	try{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		examenDoc = db.parse(xmlExamen);
		examenDoc.getDocumentElement().normalize();
	}
	catch(Exception e){
		e.printStackTrace();
	}
    }

    //Función para obtener los nombres de los ficheros xml contenidos en los campos MML
    private ArrayList<String> getXml(Document doc, ArrayList<String> xmlList){

	NodeList newXmlList = doc.getElementsByTagName("MML");
	for(int i = 0; i < newXmlList.getLength(); i++){
		Node n = newXmlList.item(i);
		if(n.getNodeType() == Node.ELEMENT_NODE){
			Element xmlE = (Element) n;
			String xml = xmlE.getTextContent();
			for(int j = 0; j < xmlList.size(); j++){ //Nos aseguramos que no guardamos más de un único xml
				if(xmlList.get(j).equals(xml)){
					break;
				}
				if(j == xmlList.size() -1){
					xmlList.add(xml);
				}
			}
		}
	}
	return xmlList;
    }

    //Función para contar el número de ficheros de warnings, errors y fatalerrors
    private void NumberFiles(){
	int number = 0;
	number_warning_xml = 0;
	if(file_warning_xml.size() != 0){
		String xmlActual = file_warning_xml.get(0);
		number++;
		for(int i = 0; i < file_warning_xml.size(); i++){
			if(!file_warning_xml.get(i).equals(xmlActual)){
				xmlActual = file_warning_xml.get(i);
				number++;
			}
		}
	}
	number_warning_xml = number;
	number = 0;
	number_error_xml = 0;
	if(file_error_xml.size() != 0){
		String xmlActual = file_error_xml.get(0);
		number++;
		for(int i = 0; i < file_error_xml.size(); i++){
			if(!file_error_xml.get(i).equals(xmlActual)){
				xmlActual = file_error_xml.get(i);
				number++;
			}
		}
	}
	number_error_xml = number;
	number = 0;
	number_fatalError_xml = 0;
	if(file_fatalError_xml.size() != 0){
		String xmlActual = file_fatalError_xml.get(0);
		number++;
		for(int i = 0; i < file_fatalError_xml.size(); i++){
			if(!file_fatalError_xml.get(i).equals(xmlActual)){
				xmlActual = file_fatalError_xml.get(i);
				number++;
			}
		}
	}
	number_fatalError_xml = number;
	number = 0;
    }

    //Función llamada al realizar una consulta
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException
    {
	try{
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
        	PrintWriter out = res.getWriter();
		req.setCharacterEncoding("UTF-8");

		//Este será el css empleado
		StringBuffer url = req.getRequestURL();
		URL servlet_url = new URL(url.toString());
		MY_CSS = new URL(servlet_url, "p2/mml.css");

		//VENTANA DE FALTA DE CONTRASEÑA
		if(req.getParameter("p") == null){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				NoPasswordAuto(out);
			}
			else{
				NoPassword(out);
			}
			return;
		}

		//VENTANA DE CONTRASEÑA INCORRECTA
		if(req.getParameter("p") != null && !req.getParameter("p").equals(password)){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				WrongPasswordAuto(out);
			}
			else{
				WrongPassword(out);
			}
			return;
		}
		
		//VENTANA DE INICIO
		if(req.getParameter("pfase") == null || req.getParameter("pfase").equals("01")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				InicioAuto(out);
			}
			else{
				Inicio(out, req.getParameter("p"));
			}
		}

		//VENTANA DE FICHEROS DE ERRORES
		else if(req.getParameter("pfase").equals("02")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				ErroresAuto(out);
			}
			else{
				Errores(out, req.getParameter("p"));
			}
		}


		/////////// VENTANAS DE LA PRIMERA CONSULTA ////////////////////

		//VENTANA DE SELECCIÓN DE AÑO
		else if(req.getParameter("pfase").equals("11")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				ChooseC1AniosAuto(out);
			}
			else{
				ChooseC1Anios(out, req.getParameter("p"));
			}
		}

		//VENTANA DE SELECCIÓN DE PELÍCULA
		else if(req.getParameter("pfase").equals("12")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				if(req.getParameter("panio") == null){
					NoParamAuto(out, "panio");
				}
				else{
					ChooseC1PeliculasAuto(out, req.getParameter("panio"));
				}
			}
			else{
				if(req.getParameter("panio") == null){
					NoParam(out, "panio");
				}
				else{
					ChooseC1Peliculas(out, req.getParameter("p"), req.getParameter("panio"));
				}
			}
		}

		//VENTANA DE SELECCIÓN DE ACTOR
		else if(req.getParameter("pfase").equals("13")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				if(req.getParameter("panio") == null){
					NoParamAuto(out, "panio");
				}
				else if(req.getParameter("ppelicula") == null){
					NoParamAuto(out, "ppelicula");
				}
				else{
					ChooseC1ActoresAuto(out, req.getParameter("panio"), req.getParameter("ppelicula"));
				}
			}
			else{
				if(req.getParameter("panio") == null){
					NoParam(out, "panio");
				}
				else if(req.getParameter("ppelicula") == null){
					NoParam(out, "ppelicula");
				}
				else{
					ChooseC1Actores(out, req.getParameter("p"), req.getParameter("panio"), req.getParameter("ppelicula"));
				}
			}
		}

		//VENTANA DE SELECCIÓN DE ACTOR
		else if(req.getParameter("pfase").equals("14")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				if(req.getParameter("panio") == null){
					NoParamAuto(out, "panio");
				}
				else if(req.getParameter("ppelicula") == null){
					NoParamAuto(out, "ppelicula");
				}
				else if(req.getParameter("pact") == null){
					NoParamAuto(out, "pact");
				}
				else{
					ChooseC1FilmografiaAuto(out, req.getParameter("panio"), req.getParameter("ppelicula"), req.getParameter("pact"));
				}
			}
			else{
				if(req.getParameter("panio") == null){
					NoParam(out, "panio");
				}
				else if(req.getParameter("ppelicula") == null){
					NoParam(out, "ppelicula");
				}
				else if(req.getParameter("pact") == null){
					NoParam(out, "pact");
				}
				else{
					ChooseC1Filmografia(out, req.getParameter("p"), req.getParameter("panio"), req.getParameter("ppelicula"), req.getParameter("pact"));
				}
			}
		}

		else if(req.getParameter("pfase").equals("15")){
			ShowExamen(out, req.getParameter("p"));
		}


		/////////// VENTANAS DE LA SEGUNDA CONSULTA ////////////////////

		//VENTANA DE SELECCIÓN DE IDIOMA
		else if(req.getParameter("pfase").equals("21")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				ChooseC2LangsAuto(out);
			}
			else{
				ChooseC2Langs(out, req.getParameter("p"));
			}
		}

		//VENTANA DE SELECCIÓN DE ACTOR
		else if(req.getParameter("pfase").equals("22")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				if(req.getParameter("plang") == null){
					NoParamAuto(out, "plang");
				}
				else{
					ChooseC2ActsAuto(out, req.getParameter("plang"));
				}
			}
			else{
				if(req.getParameter("plang") == null){
					NoParam(out, "plang");
				}
				else{
					ChooseC2Acts(out, req.getParameter("p"), req.getParameter("plang"));
				}
			}
		}

		//VENTANA DE SELECCIÓN DE PAÍS
		else if(req.getParameter("pfase").equals("23")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				if(req.getParameter("plang") == null){
					NoParamAuto(out, "plang");
				}
				else if(req.getParameter("pact") == null){
					NoParamAuto(out, "pact");
				}
				else{
					ChooseC2PaisesAuto(out, req.getParameter("plang"), req.getParameter("pact"));
				}
			}
			else{
				if(req.getParameter("plang") == null){
					NoParam(out, "plang");
				}
				else if(req.getParameter("pact") == null){
					NoParam(out, "pact");
				}
				else{
					ChooseC2Paises(out, req.getParameter("p"), req.getParameter("plang"), req.getParameter("pact"));
				}
			}
		}

		//VENTANA DE PELÍCULAS
		else if(req.getParameter("pfase").equals("24")){
			if(req.getParameter("auto") != null && req.getParameter("auto").equals("si")){
				res.setContentType("text/xml");
				if(req.getParameter("plang") == null){
					NoParamAuto(out, "plang");
				}
				else if(req.getParameter("pact") == null){
					NoParamAuto(out, "pact");
				}
				else if(req.getParameter("ppais") == null){
					NoParamAuto(out, "ppais");
				}
				else{
					ChooseC2PeliculasAuto(out, req.getParameter("plang"), req.getParameter("pact"), req.getParameter("ppais"));
				}
			}
			else{
				if(req.getParameter("plang") == null){
					NoParam(out, "plang");
				}
				else if(req.getParameter("pact") == null){
					NoParam(out, "pact");
				}
				else if(req.getParameter("ppais") == null){
					NoParam(out, "ppais");
				}
				else{
					ChooseC2Peliculas(out, req.getParameter("p"), req.getParameter("plang"), req.getParameter("pact"), req.getParameter("ppais"));
				}
			}
		}
	} catch (Exception e) {
	e.printStackTrace();
        }
    }

///////////////// VENTANA DE FALTA DE CONTRASEÑA ///////////////

    private void NoPasswordAuto(PrintWriter out){
	out.println("<?xml version='1.0' encoding='utf-8'?>");
	out.println("<wrongRequest>no passwd</wrongRequest>");
    }

    private void NoPassword(PrintWriter out){

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Falta la contraseña</p></h2>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");
    }

///////////////// VENTANA DE CONTRASEÑA INCORRECTA ///////////////

    private void WrongPasswordAuto(PrintWriter out){
	out.println("<?xml version='1.0' encoding='utf-8'?>");
	out.println("<wrongRequest>bad passwd</wrongRequest>");
    }

    private void WrongPassword(PrintWriter out){

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Contraseña incorrecta</p></h2>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");
    }

///////////////// VENTANA DE FALTA DE PARÁMETRO ////////////////

    private void NoParamAuto(PrintWriter out, String param){
	out.println("<?xml version='1.0' encoding='utf-8'?>");
	out.println("<wrongRequest>no param:" + param +"</wrongRequest>");
    }

    private void NoParam(PrintWriter out, String param){

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Falta el parámetro " + param + "</p></h2>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");
    }


///////////////// VENTANA DE INICIO ////////////////////////////

    private void InicioAuto(PrintWriter out){
	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<service>");
	out.println("<status>OK</status>");
	out.println("</service>");
    }

    private void Inicio(PrintWriter out, String p){
        out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h1><p>Bienvenido a este servicio</p></h1>");
	out.println("		<h2><p>Selecciona una consulta:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<fieldset>");
	out.println("				<p><input type='radio' name='pfase' value='02' checked>Ver los ficheros erróneos</p>");
	out.println("				<p><input type='radio' name='pfase' value='11' checked>Consulta 1: Filmografía de un miembro del reparto</p>");
	out.println("				<p><input type='radio' name='pfase' value='21' checked>Consulta 2: Películas en un idioma, de un actor/actriz, producidas en un país</p>");
	out.println("			</fieldset>");
	out.println("			<input type='submit' value='Enviar'>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");
    }

//////////////// VENTANA DE FICHEROS DE ERRORES ///////////////////////////

    private void ErroresAuto(PrintWriter out){
	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<errores>");
	out.println("<warnings>");
	if(warning_xml.size() != 0){
		String xmlActual = file_warning_xml.get(0);
		ArrayList<String> causas = new ArrayList<String>();
		for(int i = 0; i < warning_xml.size(); i++){
			if(!file_warning_xml.get(i).equals(xmlActual)){
				out.println("<warning>");
				out.println("<file>" + xmlActual + "</file>");
				out.println("<cause>" + causas + "</cause>");
				out.println("</warning>");
				xmlActual = file_warning_xml.get(i);
				causas = new ArrayList<String>();
			}
			else{ //Vamos recopilando las causas sobre el mismo fichero
				causas.add(cause_warning_xml.get(i));
			}
		}
		out.println("<warning>");
		out.println("<file>" + xmlActual + "</file>");
		out.println("<cause>" + causas + "</cause>");
		out.println("</warning>");
	}
	out.println("</warnings>");
	out.println("<errors>");
	if(error_xml.size() != 0){
		String xmlActual = file_error_xml.get(0);
		ArrayList<String> causas = new ArrayList<String>();
		for(int i = 0; i < error_xml.size(); i++){
			if(!file_error_xml.get(i).equals(xmlActual)){
				out.println("<error>");
				out.println("<file>" + xmlActual + "</file>");
				out.println("<cause>" + causas + "</cause>");
				out.println("</error>");
				xmlActual = file_error_xml.get(i);
				causas = new ArrayList<String>();
			}
			else{ //Vamos recopilando las causas sobre el mismo fichero
				causas.add(cause_error_xml.get(i));
			}
		}
		out.println("<error>");
		out.println("<file>" + xmlActual + "</file>");
		out.println("<cause>" + causas + "</cause>");
		out.println("</error>");
	}
	out.println("</errors>");
	out.println("<fatalerrors>");
	if(fatalError_xml.size() != 0){
		String xmlActual = file_fatalError_xml.get(0);
		ArrayList<String> causas = new ArrayList<String>();
		for(int i = 0; i < fatalError_xml.size(); i++){
			if(!file_fatalError_xml.get(i).equals(xmlActual)){
				out.println("<fatalerror>");
				out.println("<file>" + xmlActual + "</file>");
				out.println("<cause>" + causas + "</cause>");
				out.println("</fatalerror>");
				xmlActual = file_fatalError_xml.get(i);
				causas = new ArrayList<String>();
			}
			else{ //Vamos recopilando las causas sobre el mismo fichero
				causas.add(cause_fatalError_xml.get(i));
			}
		}
		out.println("<fatalerror>");
		out.println("<file>" + xmlActual + "</file>");
		out.println("<cause>" + causas + "</cause>");
		out.println("</fatalerror>");
	}
	out.println("</fatalerrors>");
	out.println("</errores>");
    }

    private void Errores(PrintWriter out, String p){

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	
	out.println("		<form>");
	out.println("			<h2><p>Se han encontrado " + number_warning_xml + " ficheros con warnings</p></h2>");
	if(warning_xml.size() != 0){
		out.println("			<fieldset>");
		String xmlActual = file_warning_xml.get(0);
		out.println("			<p><b>" + xmlActual + "</b></p>");
		for(int i = 0; i < warning_xml.size(); i++){
			if(!file_warning_xml.get(i).equals(xmlActual)){
				xmlActual = file_warning_xml.get(i);
			out.println("		<p><b>" + xmlActual + "</b></p>");
			}
			out.println("		<p><i>" + cause_warning_xml.get(i) + "</i></p>");
		}
		out.println("			</fieldset>");
	}
	out.println("		</form>");

	out.println("		<h2><p>Se han encontrado " + number_error_xml + " ficheros con errores fatales</p></h2>");
	out.println("		<form>");
	if(error_xml.size() != 0){
		out.println("			<fieldset>");
		String xmlActual = file_error_xml.get(0);
		out.println("			<p><b>" + xmlActual + "</b></p>");
		for(int i = 0; i < error_xml.size(); i++){
			if(!file_error_xml.get(i).equals(xmlActual)){
				xmlActual = file_error_xml.get(i);
			out.println("		<p><b>" + xmlActual + "</b></p>");
			}
			out.println("		<p><i>" + cause_error_xml.get(i) + "</i></p>");
		}
		out.println("			</fieldset>");
	}
	out.println("		</form>");

	out.println("		<h2><p>Se han encontrado " + number_fatalError_xml + " ficheros con errores fatales</p></h2>");
	out.println("		<form>");
	if(fatalError_xml.size() != 0){
		out.println("			<fieldset>");
		String xmlActual = file_fatalError_xml.get(0);
		out.println("			<p><b>" + xmlActual + "</b></p>");
		for(int i = 0; i < fatalError_xml.size(); i++){
			if(!file_fatalError_xml.get(i).equals(xmlActual)){
				xmlActual = file_fatalError_xml.get(i);
			out.println("		<p><b>" + xmlActual + "</b></p>");
			}
			out.println("		<p><i>" + cause_fatalError_xml.get(i) + "</i></p>");
		}
		out.println("			</fieldset>");
	}
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' name='pfase' value='01'>");
	out.println("			<p><input type='submit' value='Atrás'></p>");
	out.println("		</form>");

	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");
    }

//////////////////// VENTANAS DE LA PRIMERA CONSULTA ////////////////////

//////////////////// VENTANA DE SELECCIÓN DE AÑO ////////////////////////

    private void ChooseC1AniosAuto(PrintWriter out){

	ArrayList<String> anios = new ArrayList<String>();
	anios = getC1Anios(out);
	
	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<anios>");
	for(int i = 0; i < anios.size(); i++){
		out.println("<anio>" + anios.get(i) + "</anio>");
	}
	out.println("</anios>");
    }

    private void ChooseC1Anios(PrintWriter out, String p){
	
	ArrayList<String> anios = new ArrayList<String>();
	anios = getC1Anios(out);
	
	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 1</p></h2>");
	out.println("		<h2><p>Selecciona un año:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='12'>");
	out.println("			<fieldset>");
	for(int i = 0; i < anios.size(); i++){
		out.println("			<p><input type='radio' name='panio' value=" + anios.get(i) + " checked>" + (i + 1) + ".- " + anios.get(i));
	}
	out.println("			</fieldset>");
	out.println("			<p><input type='submit' value='Enviar'></p>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '01'");
	out.println("	}");
	out.println("</script>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }

//////////////////// VENTANA DE SELECCIÓN DE PELÍCULA ////////////////////////

    private void ChooseC1PeliculasAuto(PrintWriter out, String panio){

	//Obtener ip de las películas
	ArrayList<String> peliculas = new ArrayList<String>();
	peliculas = getC1Peliculas(out, panio);

	//Ordenar por duración de la película en orden ascendente
	ArrayList<String> pelisPrintOrder = new ArrayList<String>();
	while(true){
		//Si sólo queda un país en la lista, terminamos
		if(peliculas.size() == 1){
			pelisPrintOrder.add(peliculas.get(0));
			break;
		}
		ArrayList<Integer> duraciones = new ArrayList<Integer>();
		for(int i = 0; i < peliculas.size(); i++){
			int duracion = durationPelicula(out, peliculas.get(i));
			duraciones.add(duracion);		
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
		pelisPrintOrder.add(peliculas.get(index));
		peliculas.remove(index);
	}
	
	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<peliculas>");
	for(int i = 0; i < pelisPrintOrder.size(); i++){
		String titulo = titlePelicula(out, pelisPrintOrder.get(i)); //Título
		int duracion = durationPelicula(out, pelisPrintOrder.get(i)); //Duración
		String idioma = languagePelicula(out, pelisPrintOrder.get(i)); //Idioma
		out.println("<pelicula duracion='" + duracion + "' langs='" + idioma + "'>" + titulo + "</pelicula>");
	}
	out.println("</peliculas>");
    }

    private void ChooseC1Peliculas(PrintWriter out, String p, String anio){
	
	//Obtener ip de las películas
	ArrayList<String> peliculas = new ArrayList<String>();
	peliculas = getC1Peliculas(out, anio);

	//Ordenar por duración de la película en orden ascendente
	ArrayList<String> pelisPrintOrder = new ArrayList<String>();
	while(true){
		//Si sólo queda un país en la lista, terminamos
		if(peliculas.size() == 1){
			pelisPrintOrder.add(peliculas.get(0));
			break;
		}
		ArrayList<Integer> duraciones = new ArrayList<Integer>();
		for(int i = 0; i < peliculas.size(); i++){
			int duracion = durationPelicula(out, peliculas.get(i));
			duraciones.add(duracion);		
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
		pelisPrintOrder.add(peliculas.get(index));
		peliculas.remove(index);
	}

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 1: Año=" + anio + "</p></h2>");
	out.println("		<h2><p>Selecciona una película:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='13'>");
	out.println("			<input type='hidden' name='panio' value=" + anio + ">");
	out.println("			<fieldset>");
	for(int i = 0; i < pelisPrintOrder.size(); i++){
		
		String titulo = titlePelicula(out, pelisPrintOrder.get(i)); //Título
		//Sustituimos los espacios del título por +
		String[] nombres = titulo.split(" ");
		String nombre = "";
		for(int j=0; j<nombres.length; j++){
			if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
				nombre += nombres[j];
			}
			else{
				nombre += nombres[j] + "+";
			}
		}

		int duracion = durationPelicula(out, pelisPrintOrder.get(i)); //Duración
		String idioma = languagePelicula(out, pelisPrintOrder.get(i)); //Idioma
		out.println("			<p><input type='radio' name='ppelicula' value=" + nombre + " checked>" + (i + 1) + ".- " + titulo + " (" + duracion + " min.) (" + idioma + ")</p>");
	}
	out.println("			</fieldset>");
	out.println("			<p><input type='submit' value='Enviar'></p>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("			<input type='submit' value='Inicio' onclick='goInicio()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '11'");
	out.println("	}");
	out.println("</script>");
	out.println("<script>");
	out.println("	function goInicio() {");
    	out.println("		document.getElementById('fase').value = '01'");
	out.println("	}");
	out.println("</script>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }

//////////////////// VENTANA DE SELECCIÓN DE ACTOR //////////////////////

    private void ChooseC1ActoresAuto(PrintWriter out, String anio, String pelicula){

	//Sustituimos los + por espacios
	String[] nombres = pelicula.split("\\+");
	String nombre = "";
	for(int j=0; j<nombres.length; j++){
		if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
			nombre += nombres[j];
		}
		else{
			nombre += nombres[j] + " ";
		}
	}

	ArrayList<String> actores = new ArrayList<String>();
	actores = getC1Actores(out, anio, nombre);

	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<reparto>");
	for(int i = 0; i < actores.size(); i++){
		String ciudad = getActCity(out, actores.get(i)); //Ciudad
		out.println("<act ciudad='" + ciudad + "'>" + actores.get(i) + "</act>");
	}
	out.println("</reparto>");
    }

    private void ChooseC1Actores(PrintWriter out, String p, String anio, String pelicula){
	
	//Sustituimos los + por espacios
	String[] titulos = pelicula.split("\\+");
	String titulo = "";
	for(int j=0; j<titulos.length; j++){
		if(j == titulos.length - 1){ //Evitamos dejar un espacio al final
			titulo += titulos[j];
		}
		else{
			titulo += titulos[j] + " ";
		}
	}

	ArrayList<String> actores = new ArrayList<String>();
	actores = getC1Actores(out, anio, titulo);
	
	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 1: Año=" + anio + ", Película=" + titulo + "</p></h2>");
	out.println("		<h2><p>Selecciona un actor:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='14'>");
	out.println("			<input type='hidden' name='panio' value=" + anio + ">");
	out.println("			<input type='hidden' name='ppelicula' value=" + pelicula + ">");
	out.println("			<fieldset>");
	for(int i = 0; i < actores.size(); i++){
		//Sustituimos los espacios del nombre del actor por +
		String[] nombres = actores.get(i).split(" ");
		String nombre = "";
		for(int j=0; j<nombres.length; j++){
			if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
				nombre += nombres[j];
			}
			else{
				nombre += nombres[j] + "+";
			}
		}

		String ciudad = getActCity(out, actores.get(i)); //Ciudad
		out.println("			<p><input type='radio' name='pact' value=" + nombre + " checked>" + (i + 1) + ".- " + actores.get(i) + " (" + ciudad + ")</p>");
	}
	out.println("			</fieldset>");
	out.println("			<p><input type='submit' value='Enviar'></p>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("			<input type='submit' value='Inicio' onclick='goInicio()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '12'");
	out.println("	}");
	out.println("</script>");
	out.println("<script>");
	out.println("	function goInicio() {");
    	out.println("		document.getElementById('fase').value = '01'");
	out.println("	}");
	out.println("</script>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }

//////////////////// VENTANA DE FILMOGRAFÍA DE ACTOR //////////////////////

    private void ChooseC1FilmografiaAuto(PrintWriter out, String anio, String pelicula, String actor){

	//Película: Sustituimos los + por espacios
	String[] titulos = pelicula.split("\\+");
	String titulo = "";
	for(int j=0; j<titulos.length; j++){
		if(j == titulos.length - 1){ //Evitamos dejar un espacio al final
			titulo += titulos[j];
		}
		else{
			titulo += titulos[j] + " ";
		}
	}

	//Actor: Sustituimos los + por espacios
	String[] nombres = actor.split("\\+");
	String nombre = "";
	for(int j=0; j<nombres.length; j++){
		if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
			nombre += nombres[j];
		}
		else{
			nombre += nombres[j] + " ";
		}
	}

	String characterName = getCharacterName(out, anio, titulo, nombre); //Nombre del personaje

	ArrayList<String> filmografia = getC1Filmografia(out, anio, titulo, nombre);
	ArrayList<String> filmografiaPrintOrder = new ArrayList<String>();
	for(int i=0; i<filmografia.size(); i++){
		String oscar = getOscarFromMovie(out, filmografia.get(i), nombre);
		if(oscar.equals("")){
			filmografiaPrintOrder.add(filmografia.get(i));
		}
	}
	for(int i=0; i<filmografia.size(); i++){
		String oscar = getOscarFromMovie(out, filmografia.get(i), nombre);
		if(oscar.equals("Principal")){
			filmografiaPrintOrder.add(filmografia.get(i));
		}
	}
	for(int i=0; i<filmografia.size(); i++){
		String oscar = getOscarFromMovie(out, filmografia.get(i), nombre);
		if(oscar.equals("Secundario")){
			filmografiaPrintOrder.add(filmografia.get(i));
		}
	}

	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<filmografia nombre='" + nombre + "' personaje='" + characterName + "'>");
	for(int i=0; i<filmografiaPrintOrder.size(); i++){
		String thisTitulo = titlePelicula(out, filmografiaPrintOrder.get(i));
		String oscar = getOscarFromMovie(out, filmografiaPrintOrder.get(i), nombre);
		if(oscar.equals("")){
			out.println("<film>" + thisTitulo + "</film>");
		}
		else{
			out.println("<film oscar='" + oscar + "'>" + thisTitulo + "</film>");
		}
	}
	out.println("</filmografia>");
    }

    private void ChooseC1Filmografia(PrintWriter out, String p, String anio, String pelicula, String actor){
	
	//Película: Sustituimos los + por espacios
	String[] titulos = pelicula.split("\\+");
	String titulo = "";
	for(int j=0; j<titulos.length; j++){
		if(j == titulos.length - 1){ //Evitamos dejar un espacio al final
			titulo += titulos[j];
		}
		else{
			titulo += titulos[j] + " ";
		}
	}

	//Actor: Sustituimos los + por espacios
	String[] nombres = actor.split("\\+");
	String nombre = "";
	for(int j=0; j<nombres.length; j++){
		if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
			nombre += nombres[j];
		}
		else{
			nombre += nombres[j] + " ";
		}
	}

	String characterName = getCharacterName(out, anio, titulo, nombre); //Nombre del personaje

	ArrayList<String> filmografia = getC1Filmografia(out, anio, titulo, nombre);
	ArrayList<String> filmografiaPrintOrder = new ArrayList<String>();
	for(int i=0; i<filmografia.size(); i++){
		String oscar = getOscarFromMovie(out, filmografia.get(i), nombre);
		if(oscar.equals("")){
			filmografiaPrintOrder.add(filmografia.get(i));
		}
	}
	for(int i=0; i<filmografia.size(); i++){
		String oscar = getOscarFromMovie(out, filmografia.get(i), nombre);
		if(oscar.equals("Principal")){
			filmografiaPrintOrder.add(filmografia.get(i));
		}
	}
	for(int i=0; i<filmografia.size(); i++){
		String oscar = getOscarFromMovie(out, filmografia.get(i), nombre);
		if(oscar.equals("Secundario")){
			filmografiaPrintOrder.add(filmografia.get(i));
		}
	}
	
	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 1: Año=" + anio + ", Película=" + titulo + ", Act=" + nombre + "</p></h2>");
	out.println("		<h2><p>El personaje es: " + characterName + "</p></h2>");
	out.println("		<h2><p>Esta es su filmografía:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='01'>");
	out.println("			<input type='hidden' name='panio' value=" + anio + ">");
	out.println("			<input type='hidden' name='ppelicula' value=" + pelicula + ">");
	out.println("			<input type='hidden' name='pact' value=" + actor + ">");
	out.println("			<fieldset>");
	for(int i=0; i<filmografiaPrintOrder.size(); i++){
		String thisTitulo = titlePelicula(out, filmografiaPrintOrder.get(i));
		String oscar = getOscarFromMovie(out, filmografiaPrintOrder.get(i), nombre);
		if(oscar.equals("")){
			out.println("			<p>·" + (i + 1) + ".- <b>Título=</b>" + thisTitulo + "</p>");
		}
		else{
			out.println("			<p>·" + (i + 1) + ".- <b>Título=</b>" + thisTitulo + ", <b>Óscar=</b>" + oscar + "</p>");
		}
	}
	out.println("			</fieldset>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("			<input type='submit' value='Inicio' onclick='goInicio()'>");
	out.println("			<input type='submit' value='Next' onclick='goExamen()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '13'");
	out.println("	}");
	out.println("</script>");
	out.println("<script>");
	out.println("	function goInicio() {");
    	out.println("		document.getElementById('fase').value = '01'");
	out.println("	}");
	out.println("</script>");
	out.println("<script>");
	out.println("	function goExamen() {");
    	out.println("		document.getElementById('fase').value = '15'");
	out.println("	}");
	out.println("</script>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }

    private void ShowExamen(PrintWriter out, String p){

	String anio = XmlExamenAnio(out);
	int oscares = XmlExamenOscares(out, anio);
	String color = XmlExamenColor(out);

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>EXAMEN</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='01'>");
	out.println("			<fieldset>");
	if(color.equals("rojo")){
		out.println("<p><l1>Año: " + anio + "</l1></p>");
		out.println("<p><l1>Oscares: " + oscares + "</l1></p>");
	}
	else{
		out.println("<p><l2>Año: " + anio + "</l2></p>");
		out.println("<p><l2>Oscares: " + oscares + "</l2></p>");
	}
	out.println("			</fieldset>");
	out.println("			<p><input type='submit' value='Inicio'></p>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }

    private String XmlExamenAnio(PrintWriter out){
	//Descubrir qué año se pide
	String anio = "";
	NodeList examenList = examenDoc.getElementsByTagName("examen");
	for(int i = 0; i < examenList.getLength(); i++){
		Node e = examenList.item(i);

		if(e.getNodeType() == Node.ELEMENT_NODE){
			Element examen = (Element) e;
			NodeList nameList = examen.getChildNodes();
			for(int j=0; j<nameList.getLength(); j++){
				Node n = nameList.item(j);

				//Anio
				if(n.getNodeType() == Node.TEXT_NODE && !n.getTextContent().trim().equals("")){
					anio = n.getTextContent().trim();
				}
			}
		}
	}
	return anio;
    }

    private int XmlExamenOscares(PrintWriter out, String anio){
	int oscares = 0;
	String thisOscar = "";
	//Oscar a buscar
	NodeList examenList = examenDoc.getElementsByTagName("oscar");
	for(int i = 0; i < examenList.getLength(); i++){
		Node e = examenList.item(i);

		if(e.getNodeType() == Node.ELEMENT_NODE){
			Element examen = (Element) e;
			thisOscar = examen.getTextContent();
		}
	}

	for(int turn=0; turn<docList.size(); turn++){
		NodeList anioList = docList.get(turn).getElementsByTagName("Anio");
		for(int i=0; i<anioList.getLength(); i++){
			Node a = anioList.item(i);
			if(a.getNodeType() == Node.ELEMENT_NODE){
				Element an = (Element) a;
				if(an.getTextContent().equals(anio)){ //Encontramos el año
					NodeList oscarList = docList.get(turn).getElementsByTagName("Oscar");
					for(int j=0; j<oscarList.getLength(); j++){
						Node o = oscarList.item(j);
						if(o.getNodeType() == Node.ELEMENT_NODE){
							Element oscar = (Element) o;
							if(oscar.getTextContent().equals(thisOscar)){
								oscares++;
							}
						}
					}
					return oscares;
				}
			}
		}
	}
	return oscares;
    }

    private String XmlExamenColor(PrintWriter out){
	//Descubrir qué color se pide
	String color = "";
	NodeList examenList = examenDoc.getElementsByTagName("oscar");
	for(int i = 0; i < examenList.getLength(); i++){
		Node e = examenList.item(i);

		if(e.getNodeType() == Node.ELEMENT_NODE){
			Element examen = (Element) e;
			color = examen.getAttribute("decoracion");
		}
	}
	return color;
    }

//////////////////// VENTANAS DE LA SEGUNDA CONSULTA ////////////////////

//////////////////// VENTANA DE SELECCIÓN DE IDIOMA /////////////////////

    private void ChooseC2LangsAuto(PrintWriter out){

	ArrayList<String> langs = new ArrayList<String>();
	langs = getC2Langs(out);
	
	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<langs>");
	for(int i = 0; i < langs.size(); i++){
		out.println("<lang>" + langs.get(i) + "</lang>");
	}
	out.println("</langs>");
    }

    private void ChooseC2Langs(PrintWriter out, String p){
	
	ArrayList<String> langs = new ArrayList<String>();
	langs = getC2Langs(out);
	
	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 2</p></h2>");
	out.println("		<h2><p>Selecciona un idioma:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='22'>");
	out.println("			<fieldset>");
	for(int i = 0; i < langs.size(); i++){
		out.println("			<p><input type='radio' name='plang' value=" + langs.get(i) + " checked>" + (i + 1) + ".- " + langs.get(i));
	}
	out.println("			</fieldset>");
	out.println("			<p><input type='submit' value='Enviar'></p>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '01'");
	out.println("	}");
	out.println("</script>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }

/////////////////// VENTANA DE SELECCIÓN DE ACTOR ///////////////////////

    private void ChooseC2ActsAuto(PrintWriter out, String plang){
	ArrayList<String> acts = new ArrayList<String>();
	acts = getC2Acts(out, plang);
	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<acts>");
	//Ordenamos de forma que nombramos primero a los actores con óscar
	ArrayList<String> actsPrintOrder = new ArrayList<String>();
	for(int i = 0; i < acts.size(); i++){
		boolean oscar = getActOscar(out, acts.get(i));
		if(oscar){
			actsPrintOrder.add(acts.get(i));
		}
	}
	for(int i = 0; i < acts.size(); i++){
		boolean oscar = getActOscar(out, acts.get(i));
		if(!oscar){
			actsPrintOrder.add(acts.get(i));
		}
	}	

	for(int i = 0; i < actsPrintOrder.size(); i++){
		String[] nombres = actsPrintOrder.get(i).split(" ");
		String nombre = "";
		for(int j=0; j<nombres.length; j++){
			if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
				nombre += nombres[j];
			}
			else{
				nombre += nombres[j] + "+";
			}
		}
		//Buscamos la info extra a mostrar pero que no se enviará en la url
		String city = getActCity(out, actsPrintOrder.get(i));
		boolean oscar = getActOscar(out, actsPrintOrder.get(i));
		out.println("<ac ciudad='" + city + "' oscar='" + oscar + "'>" + actsPrintOrder.get(i) + "</ac>");
	}
	out.println("</acts>");
    }

    private void ChooseC2Acts(PrintWriter out, String p, String plang){
	
	ArrayList<String> acts = new ArrayList<String>();
	acts = getC2Acts(out, plang);

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 2: Idioma=" + plang + "</p></h2>");
	out.println("		<h2><p>Selecciona un Actor/Actriz:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='23'>");
	out.println("			<input type='hidden' name='plang' value="+plang+">");
	out.println("			<fieldset>");

	//Ordenamos de forma que nombramos primero a los actores con óscar
	ArrayList<String> actsPrintOrder = new ArrayList<String>();
	for(int i = 0; i < acts.size(); i++){
		boolean oscar = getActOscar(out, acts.get(i));
		if(oscar){
			actsPrintOrder.add(acts.get(i));
		}
	}
	for(int i = 0; i < acts.size(); i++){
		boolean oscar = getActOscar(out, acts.get(i));
		if(!oscar){
			actsPrintOrder.add(acts.get(i));
		}
	}	

	for(int i = 0; i < actsPrintOrder.size(); i++){
		String[] nombres = actsPrintOrder.get(i).split(" ");
		String nombre = "";
		for(int j=0; j<nombres.length; j++){
			if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
				nombre += nombres[j];
			}
			else{
				nombre += nombres[j] + "+";
			}
		}
		//Buscamos la info extra a mostrar pero que no se enviará en la url
		String city = getActCity(out, actsPrintOrder.get(i));
		boolean oscar = getActOscar(out, actsPrintOrder.get(i));
		String haveOscar = "sin óscar";
		if(oscar){
			haveOscar = "con óscar";
		}
		out.println("			<p><input type='radio' name='pact' value=" + nombre + " checked>" + (i + 1) + ".- " + actsPrintOrder.get(i) + " (" + city + ") -- " + haveOscar + "</p>");
	}
	out.println("			</fieldset>");
	out.println("			<p><input type='submit' value='Enviar'></p>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("			<input type='submit' value='Inicio' onclick='goInicio()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '21'");
	out.println("	}");
	out.println("</script>");
	out.println("<script>");
	out.println("	function goInicio() {");
    	out.println("		document.getElementById('fase').value = '01'");
	out.println("	}");
	out.println("</script>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }

////////////////// VENTANA DE SELECCIÓN DE PAÍS ///////////////////////////

    private void ChooseC2PaisesAuto(PrintWriter out, String plang, String pact){
	String[] nombres = pact.split("\\+");
	String nombre = "";
	for(int j=0; j<nombres.length; j++){
		if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
			nombre += nombres[j];
		}
		else{
			nombre += nombres[j] + " ";
		}
	}
	ArrayList<String> paises = new ArrayList<String>();
	paises = getC2Paises(out, plang, nombre);

	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<paises>");
	//Ordenar por número de películas producidas en cada país
	ArrayList<String> paisesPrintOrder = new ArrayList<String>();
	while(true){
		//Si sólo queda un país en la lista, terminamos
		if(paises.size() == 1){
			paisesPrintOrder.add(paises.get(0));
			break;
		}
		ArrayList<Integer> numberList = new ArrayList<Integer>();
		for(int i = 0; i < paises.size(); i++){
			int number = getNumberPeliculas(out, paises.get(i));
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
		paisesPrintOrder.add(paises.get(index));
		paises.remove(index);
	}
	for(int i = 0; i < paisesPrintOrder.size(); i++){
		int number = getNumberPeliculas(out, paisesPrintOrder.get(i));
		String defLang = getDefaultLangPais(out, paisesPrintOrder.get(i));
		out.println("<pais lang='" + defLang + "' num='" + number + "'>" + paisesPrintOrder.get(i) + "</pais>");
	}
	out.println("</paises>");
    }

    private void ChooseC2Paises(PrintWriter out, String p, String plang, String pact){
    try{
	String[] nombres = pact.split("\\+");
	String nombre = "";
	for(int j=0; j<nombres.length; j++){
		if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
			nombre += nombres[j];
		}
		else{
			nombre += nombres[j] + " ";
		}
	}
	ArrayList<String> paises = new ArrayList<String>();
	paises = getC2Paises(out, plang, nombre);

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 2: Idioma=" + plang + ", Actor/Actriz " + nombre + "</p></h2>");
	out.println("		<h2><p>Selecciona un país:</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='24'>");
	out.println("			<input type='hidden' name='plang' value="+plang+">");
	out.println("			<input type='hidden' name='pact' value="+pact+">");
	out.println("			<fieldset>");

	//Ordenar por número de películas producidas en cada país
	ArrayList<String> paisesPrintOrder = new ArrayList<String>();
	while(true){
		//Si sólo queda un país en la lista, terminamos
		if(paises.size() == 1){
			paisesPrintOrder.add(paises.get(0));
			break;
		}
		ArrayList<Integer> numberList = new ArrayList<Integer>();
		for(int i = 0; i < paises.size(); i++){
			int number = getNumberPeliculas(out, paises.get(i));
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
		paisesPrintOrder.add(paises.get(index));
		paises.remove(index);
	}
	for(int i = 0; i < paisesPrintOrder.size(); i++){
		int number = getNumberPeliculas(out, paisesPrintOrder.get(i));
		String defLang = getDefaultLangPais(out, paisesPrintOrder.get(i));
		out.println("			<p><input type='radio' name='ppais' value=" + paisesPrintOrder.get(i) + " checked>" + (i + 1) + ".- " + paisesPrintOrder.get(i) + "(" + number + " películas) -- idioma por defecto='" + defLang + "'</p>");
	}
	out.println("			</fieldset>");
	out.println("			<p><input type='submit' value='Enviar'></p>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("			<input type='submit' value='Inicio' onclick='goInicio()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '22'");
	out.println("	}");
	out.println("</script>");
	out.println("<script>");
	out.println("	function goInicio() {");
    	out.println("		document.getElementById('fase').value = '01'");
	out.println("	}");
	out.println("</script>");

	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
	}catch(Exception e){
		out.println(e);
	}
    }

////////////////// VENTANA DE PELÍCULAS /////////////////////////////////

    private void ChooseC2PeliculasAuto(PrintWriter out, String plang, String pact, String ppais){
	
	String[] nombres = pact.split("\\+");
	String nombre = "";
	for(int j=0; j<nombres.length; j++){
		if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
			nombre += nombres[j];
		}
		else{
			nombre += nombres[j] + " ";
		}
	}
	ArrayList<String> peliculas = new ArrayList<String>();
	peliculas = getC2Peliculas(out, plang, nombre, ppais);

	out.println("<?xml version='1.0' encoding='utf-8' ?>");
	out.println("<titulos>");

	for(int i = 0; i < peliculas.size(); i++){
		String title = titlePelicula(out, peliculas.get(i));
		out.println("<titulo ip='" + peliculas.get(i) + "'>" + title + "</titulo>");
	}
	out.println("</titulos>");
    }

    private void ChooseC2Peliculas(PrintWriter out, String p, String plang, String pact, String ppais){
	
	String[] nombres = pact.split("\\+");
	String nombre = "";
	for(int j=0; j<nombres.length; j++){
		if(j == nombres.length - 1){ //Evitamos dejar un espacio al final
			nombre += nombres[j];
		}
		else{
			nombre += nombres[j] + " ";
		}
	}
	ArrayList<String> peliculas = new ArrayList<String>();
	peliculas = getC2Peliculas(out, plang, nombre, ppais);

	out.println("<html>");
        out.println("	<head>");
        out.println("		<title>Práctica 2</title>");
	out.println("		<meta charset='UTF-8'>");
	out.println("		<link rel='stylesheet' type='text/css' href=" + MY_CSS + ">");
        out.println("	</head>");
        out.println("	<body>");
        out.println("		<h1><p>Servicio de consultas de películas</p></h1>");
	out.println("		<h2><p>Consulta 2: Idioma=" + plang + ", Actor/Actriz=" + nombre + ", Pais=" + ppais + "</p></h2>");
	out.println("		<h2><p>Estas son sus películas</p></h2>");
	out.println("		<form>");
	out.println("			<input type='hidden' name='p' value=" + p + ">");
	out.println("			<input type='hidden' id='fase' name='pfase' value='01'>");
	out.println("			<input type='hidden' name='plang' value="+plang+">");
	out.println("			<input type='hidden' name='pact' value="+pact+">");
	out.println("			<input type='hidden' name='ppais' value="+ppais+">");
	out.println("			<fieldset>");
	

	for(int i = 0; i < peliculas.size(); i++){
		String title = titlePelicula(out, peliculas.get(i));
		out.println("			<p>·" + (i + 1) + ".- <b>Película=</b>" + title + ", <b>IP=</b>" + peliculas.get(i) + "</p>");
	}
	out.println("			</fieldset>");
	out.println("			<input type='submit' value='Inicio'>");
	out.println("			<input type='submit' value='Atrás' onclick='goBack()'>");
	out.println("<script>");
	out.println("	function goBack() {");
    	out.println("		document.getElementById('fase').value = '23'");
	out.println("	}");
	out.println("</script>");
	out.println("		</form>");
	out.println("		<address> Autor: Jaime Varela de la Escalera de Miguel </address>");
	out.println("	</body>");
        out.println("</html>");	
    }


    ////////////////// BÚSQUEDAS EN XML ///////////////////////////////////////

    private ArrayList<String> getC1Anios(PrintWriter out){

	ArrayList<String> anios = new ArrayList<String>();
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
	return anios;
    }

    private ArrayList<String> getC1Peliculas(PrintWriter out, String anio){

	ArrayList<String> peliculas = new ArrayList<String>();
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
	return peliculas;
    }

    private ArrayList<String> getC1Actores(PrintWriter out, String anio, String pelicula){

	ArrayList<String> actores = new ArrayList<String>();
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
										actores = actsFromMovie(out, thisPelicula);
										Collections.sort(actores, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
										return actores;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	return actores;
    }

    private ArrayList<String> getC1Filmografia(PrintWriter out, String anio, String pelicula, String actor){
	ArrayList<String> filmografia = new ArrayList<String>();
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
						actores = actsFromMovie(out, thisPelicula);
						for(int k=0; k<actores.size(); k++){
							if(actores.get(k).equals(actor)){ //El actor ha participado en esta película
								filmografia.add(thisPelicula.getAttribute("ip"));
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
		if(filmografia.size() == 1){
			filmografiaPrintOrder.add(filmografia.get(0));
			break;
		}
		ArrayList<String> nameList = new ArrayList<String>();
		for(int i = 0; i < filmografia.size(); i++){
			String name = titlePelicula(out, filmografia.get(i));
			nameList.add(name);		
		}
		//Ordenamos la lista
		Collections.sort(nameList, String.CASE_INSENSITIVE_ORDER);
		//Comprobamos a qué película le corresponde el primer título de la lista
		for(int i=0; i<filmografia.size(); i++){
			if(nameList.get(0).equals(titlePelicula(out, filmografia.get(i)))){
				filmografiaPrintOrder.add(filmografia.get(i));
				filmografia.remove(i);
				break;
			}
		}
	}
	return filmografiaPrintOrder;
    }

    private ArrayList<String> getC2Langs(PrintWriter out){
	
	ArrayList<String> langs = new ArrayList<String>();
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
					langs.add(idiomas[j]);					
				}
			}
		}
	}
	//En nuestra lista tenemos repetidos los mismos idiomas
	ArrayList<String> listaFinal = new ArrayList<String>();
	for(int i=0; i<langs.size(); i++){
		if(listaFinal.size() == 0){ //Añadimos el primero de la lista
			listaFinal.add(langs.get(i));
		}
		else{
			for(int j=0; j<listaFinal.size(); j++){
				if(langs.get(i).equals(listaFinal.get(j))){
					break; //Si encontramos una coincidencia, ya no vamos a añadirlo
				}
				if(j == listaFinal.size() - 1){ //Hemos recorrido toda la lista sin encontrar una coincidencia
					listaFinal.add(langs.get(i));
				}
			}
		}
	}
	Collections.sort(listaFinal, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
	Collections.reverse(listaFinal); //En orden inverso
	return listaFinal;
    }

    private ArrayList<String> getC2Acts(PrintWriter out, String plang){
	
	ArrayList<String> acts = new ArrayList<String>();
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
						parcialActs = actsFromMovie(out, pelicula);
						for(int k=0; k<parcialActs.size(); k++){
							acts.add(parcialActs.get(k));
						}
						break;
					}				
				}
			}
		}
	}
	//En nuestra lista tenemos repetidos los mismos actores
	ArrayList<String> listaFinal = new ArrayList<String>();
	for(int i=0; i<acts.size(); i++){
		if(listaFinal.size() == 0){ //Añadimos el primero de la lista
			listaFinal.add(acts.get(i));
		}
		else{
			for(int j=0; j<listaFinal.size(); j++){
				if(acts.get(i).equals(listaFinal.get(j))){
					break; //Si encontramos una coincidencia, ya no vamos a añadirlo
				}
				if(j == listaFinal.size() - 1){ //Hemos recorrido toda la lista sin encontrar una coincidencia
					listaFinal.add(acts.get(i));
				}
			}
		}
	}
	Collections.sort(listaFinal, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
	return listaFinal;
    }

    private ArrayList<String> getC2Paises(PrintWriter out, String plang, String pact){
	
	ArrayList<String> paises = new ArrayList<String>();
	for(int turn=0; turn<docList.size(); turn++){
		NodeList paisesList = docList.get(turn).getElementsByTagName("Pais");
		for(int i=0; i<paisesList.getLength(); i++){
			Node p = paisesList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pais = (Element) p;
				ArrayList<String> pelis = new ArrayList<String>();
				pelis = pelisFromPais(out, plang, pact, pais);
				if(pelis.size() != 0){
					paises.add(pais.getAttribute("pais"));
				}
			}
		}
	}
	//En nuestra lista tenemos repetidos los mismos países
	ArrayList<String> listaFinal = new ArrayList<String>();
	for(int i=0; i<paises.size(); i++){
		if(listaFinal.size() == 0){ //Añadimos el primero de la lista
			listaFinal.add(paises.get(i));
		}
		else{
			for(int j=0; j<listaFinal.size(); j++){
				if(paises.get(i).equals(listaFinal.get(j))){
					break; //Si encontramos una coincidencia, ya no vamos a añadirlo
				}
				if(j == listaFinal.size() - 1){ //Hemos recorrido toda la lista sin encontrar una coincidencia
					listaFinal.add(paises.get(i));
				}
			}
		}
	}
	Collections.sort(listaFinal, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente
	return listaFinal;
    }

    private ArrayList<String> getC2Peliculas(PrintWriter out, String plang, String pact, String ppais){
	
	ArrayList<String> peliculas = new ArrayList<String>();
	//El país ppais se eligió en función del actor e idioma seleccionados
	for(int turn=0; turn<docList.size(); turn++){
		NodeList paisesList = docList.get(turn).getElementsByTagName("Pais");
		for(int i=0; i<paisesList.getLength(); i++){
			Node p = paisesList.item(i);
			if(p.getNodeType() == Node.ELEMENT_NODE){
				Element pais = (Element) p;
				if(pais.getAttribute("pais").equals(ppais)){
					ArrayList<String> pelis = new ArrayList<String>();
					pelis = pelisFromPais(out, plang, pact, pais);
					if(pelis.size() != 0){
						for(int j=0; j<pelis.size(); j++){
							peliculas.add(pelis.get(j));
						}
					}
				}
			}
		}
	}
	Collections.sort(peliculas, String.CASE_INSENSITIVE_ORDER); //Lo ordenamos alfabéticamente	
	return peliculas;
    }

    private ArrayList<String> actsFromMovie(PrintWriter out, Element pelicula){
	
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

    private ArrayList<String> pelisFromPais(PrintWriter out, String plang, String pact, Element pais){
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
					parcialActs = actsFromMovie(out, pelicula);
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

    private String getTitleFromMovie(PrintWriter out, Element pelicula){
	
	String title = "";
	NodeList titleList = pelicula.getChildNodes();
	for(int i=0; i<titleList.getLength(); i++){
		Node t = titleList.item(i);
		if(t.getNodeType() == Node.ELEMENT_NODE){
			Element name = (Element) t;
			if(name.getTagName().equals("Titulo")){
				title = name.getTextContent();
			}
		}
	}
	return title;
    }


    //Información extra de actores
    private String getActCity(PrintWriter out, String act){
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

    private boolean getActOscar(PrintWriter out, String act){
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

    private String getOscarFromMovie(PrintWriter out, String pelicula, String actor){
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

    private String getCharacterName(PrintWriter out, String anio, String pelicula, String actor){
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
										personaje = getCharacter(out, thisPelicula, actor);
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

    private String getCharacter(PrintWriter out, Element pelicula, String actor){
	
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

    //Información extra de países
    private int getNumberPeliculas(PrintWriter out, String ppais){
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

    private String getDefaultLangPais(PrintWriter out, String ppais){

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

    //Información extra de películas
    private String titlePelicula(PrintWriter out, String pelicula){
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

    private int durationPelicula(PrintWriter out, String ip){

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

    private String languagePelicula(PrintWriter out, String ip){

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

}

class XML_XSD_ErrorHandler extends DefaultHandler {
    Sint197P2 servlet;
    public XML_XSD_ErrorHandler (Sint197P2 servlet){
	this.servlet = servlet;
    }
    public void warning(SAXParseException spe) throws SAXException{
	servlet.warning_xml.add(spe.toString());
	servlet.cause_warning_xml.add(spe.getMessage());
	servlet.file_warning_xml.add(spe.getSystemId());
    }
	
    public void error(SAXParseException spe) throws SAXException{
	servlet.error_xml.add(spe.toString());
	servlet.cause_error_xml.add(spe.getMessage());
	servlet.file_error_xml.add(spe.getSystemId());
	servlet.err_xml=true;
    }
	
    public void fatalerror(SAXParseException spe) throws SAXException{
	servlet.fatalError_xml.add(spe.toString());
	servlet.cause_fatalError_xml.add(spe.getMessage());
	servlet.file_fatalError_xml.add(spe.getSystemId());
	servlet.err_xml=true;
    }
}
