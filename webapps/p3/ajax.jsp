<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
	<head>
		<title>Práctica 3</title>
		<meta charset='UTF-8'>
		<link rel='stylesheet' type='text/css' href="${css}">
	</head>
	<body>
		<p><h1>Servicio de consultas de películas</h1></p>
		<input type='hidden' id='password' name='p' value='${p}'>
		<input type='hidden' id='pfase' name='pfase' value='30'>
		<div id="fase"></div>
		<p><button id="enviar">Enviar</button></p>
		<p><button id="atras">Atrás</button></p>
		<p><button id="inicio">Inicio</button></p>
		<address> Autor: Jaime Varela de la Escalera de Miguel </address>

		<script>
			var fase = document.getElementById("fase");
			var enviar = document.getElementById("enviar");

			//Fase inicial
			document.getElementById("enviar").style.display = "block";
			document.getElementById("atras").style.display = "none";
			document.getElementById("inicio").style.display = "none";
			fase01();

			enviar.addEventListener("click", function(){
				
				//Elementos a enviar
				var params = "?p=" + document.getElementById("password").value + "&ajax=ajax";
				if(document.getElementById("pfase01") != null){ //Fase inicial
					document.getElementById("pfase").value = document.querySelector('input[id="pfase01"]:checked').value;
					params += "&pfase=" + document.querySelector('input[id="pfase01"]:checked').value;
				}
				else{ //No es la fase inicial
					switch(document.getElementById("pfase").value){
						case "02": //getErrores
							document.getElementById("pfase").value = "01";
							params += "&pfase=01";
							break;
						case "11": //getC1Anios
							document.getElementById("pfase").value = "12";
							params += "&pfase=12&panio=" + document.querySelector('input[id="panio"]:checked').value;
							break;
						case "12": //getC1Peliculas
							document.getElementById("pfase").value = "13";
							params += "&pfase=13&panio=" + document.getElementById("panio").value + "&ppelicula=" + document.querySelector('input[id="ppelicula"]:checked').value;
							break;
						case "13": //getC1Acts
							document.getElementById("pfase").value = "14";
							params += "&pfase=14&panio=" + document.getElementById("panio").value + "&ppelicula=" + document.getElementById("ppelicula").value + "&pact=" + document.querySelector('input[id="pact"]:checked').value;
							break;
						case "14": //getC1Filmografia
							document.getElementById("pfase").value = "01";
							params += "&pfase=01";
							break;
						case "21": //getC2Langs
							document.getElementById("pfase").value = "22";
							params += "&pfase=22&plang=" + document.querySelector('input[id="plang"]:checked').value;
							break;
						case "22": //getC2Acts
							document.getElementById("pfase").value = "23";
							params += "&pfase=23&plang=" + document.getElementById("plang").value + "&pact=" + document.querySelector('input[id="pact"]:checked').value;
							break;
						case "23": //getC2Paises
							document.getElementById("pfase").value = "24";
							params += "&pfase=24&plang=" + document.getElementById("plang").value + "&pact=" + document.getElementById("pact").value + "&ppais=" + document.querySelector('input[id="ppais"]:checked').value;
							break;
						case "24": //getC2Peliculas
							document.getElementById("pfase").value = "01";
							params += "&pfase=01";
							break;
					}
				}
			AjaxProcess(params);
			});

			var atras = document.getElementById("atras");
			atras.addEventListener("click", function(){
				
				//Elementos a enviar
				var params = "?p=" + document.getElementById("password").value + "&ajax=ajax";
				switch(document.getElementById("pfase").value){
					case "11": //getC1Anios
						document.getElementById("pfase").value = "01";
						params += "&pfase=01";
						break;
					case "12": //getC1Peliculas
						document.getElementById("pfase").value = "11";
						params += "&pfase=11";
						break;
					case "13": //getC1Acts
						document.getElementById("pfase").value = "12";
						params += "&pfase=12&panio=" + document.getElementById("panio").value;
						break;
					case "14": //getC1Filmografia
						document.getElementById("pfase").value = "13";
						params += "&pfase=13&panio=" + document.getElementById("panio").value + "&ppelicula=" + document.getElementById("ppelicula").value;
						break;
					case "21": //getC2Langs
						document.getElementById("pfase").value = "01";
						params += "&pfase=01";
						break;
					case "22": //getC2Acts
						document.getElementById("pfase").value = "21";
						params += "&pfase=21";
						break;
					case "23": //getC2Paises
						document.getElementById("pfase").value = "22";
						params += "&pfase=22&plang=" + document.getElementById("plang").value;
						break;
					case "24": //getC2Peliculas
						document.getElementById("pfase").value = "23";
						params += "&pfase=23&plang=" + document.getElementById("plang").value + "&pact=" + document.getElementById("pact").value;
						break;
				}
			AjaxProcess(params);
			});

			var inicio = document.getElementById("inicio");
			inicio.addEventListener("click", function(){
				document.getElementById("pfase").value = "01";
				//Elementos a enviar
				var params = "?p=" + document.getElementById("password").value + "&ajax=ajax" + "&pfase=01";
				AjaxProcess(params);
			});

		function AjaxProcess(params){
			//Fase de envío
			fase.innerHTML = ""; //Borramos los elementos de la fase anterior
			var ourRequest = new XMLHttpRequest();
			ourRequest.open("GET", "P3M" + params); //Enviamos los datos al servlet
			ourRequest.onload = function(){
				if(ourRequest.status >= 200 && ourRequest.status < 400){
					if(document.getElementById("pfase").value != "01"){
						var ourData = JSON.parse(ourRequest.responseText);
					}
					switch(document.getElementById("pfase").value){
						case "01": //Fase inicial
							document.getElementById("enviar").style.display = "block";
							document.getElementById("atras").style.display = "none";
							document.getElementById("inicio").style.display = "none";
							fase01();
							break;
						case "02": //getErrores
							document.getElementById("enviar").style.display = "none";
							document.getElementById("atras").style.display = "none";
							document.getElementById("inicio").style.display = "block";
							fase02(ourData);
							break;
						case "11": //getC1Anios
							document.getElementById("enviar").style.display = "block";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "none";
							fase11(ourData);
							break;
						case "12": //getC1Peliculas
							document.getElementById("enviar").style.display = "block";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "block";
							fase12(ourData);
							break;
						case "13": //getC1Acts
							document.getElementById("enviar").style.display = "block";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "block";
							fase13(ourData);
							break;
						case "14": //getC1Filmografia
							document.getElementById("enviar").style.display = "none";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "block";
							fase14(ourData);
							break;
						case "21": //getC2Langs
							document.getElementById("enviar").style.display = "block";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "none";
							fase21(ourData);
							break;
						case "22": //getC2Acts
							document.getElementById("enviar").style.display = "block";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "block";
							fase22(ourData);
							break;
						case "23": //getC2Paises
							document.getElementById("enviar").style.display = "block";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "block";
							fase23(ourData);
							break;
						case "24": //getC2Películas
							document.getElementById("enviar").style.display = "none";
							document.getElementById("atras").style.display = "block";
							document.getElementById("inicio").style.display = "block";
							fase24(ourData);
							break;
					}
				}
				else{
					console.log("We connected to the server, but it returned an error: " + ourRequest.status);
				}
			};
			ourRequest.onerror = function(){
				console.log("Connection error");
			};
			ourRequest.send();
			}

			function fase01(){
				var htmlString = "";
				htmlString += "<p><h1>Bienvenido a este servicio</h1></p>";
				htmlString += "<p><h2>Selecciona una consulta:</h2></p>";
				htmlString += "<fieldset>";
				htmlString += "<p><input type='radio' name='pfase' id='pfase01' value='02' checked>Ver los ficheros erróneos</p>";
				htmlString += "<p><input type='radio' name='pfase' id='pfase01' value='11' checked>Consulta 1: Filmografía de un miembro del reparto</p>";
				htmlString += "<p><input type='radio' name='pfase' id='pfase01' value='21' checked>Consulta 2: Películas en un idioma, de un actor/actriz, producidas en un país</p>";
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase02(data) {
				var htmlString = "";
				htmlString += "<p><h2>Se han encontrado " + data.errores[0].warnings.length + " ficheros con warnings</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data.errores[0].warnings.length; i++){
					htmlString += "<p><b>" + data.errores[0].warnings[i].file + "</b></p>";
					for(j=0; j<data.errores[0].warnings[i].causes.length; j++){
						htmlString += "<p><i>" + data.errores[0].warnings[i].causes[j].cause + "</i></p>";
					}
				}
				htmlString += "</fieldset>";

				htmlString += "<p><h2>Se han encontrado " + data.errores[1].errors.length + " ficheros con errores</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data.errores[1].errors.length; i++){
					htmlString += "<p><b>" + data.errores[1].errors[i].file + "</b></p>";
					for(j=0; j<data.errores[1].errors[i].causes.length; j++){
						htmlString += "<p><i>" + data.errores[1].errors[i].causes[j].cause + "</i></p>";
					}
				}
				htmlString += "</fieldset>";

				htmlString += "<p><h2>Se han encontrado " + data.errores[2].fatalerrors.length + " ficheros con errores fatales</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data.errores[2].fatalerrors.length; i++){
					htmlString += "<p><b>" + data.errores[2].fatalerrors[i].file + "</b></p>";
					for(j=0; j<data.errores[2].fatalerrors[i].causes.length; j++){
						htmlString += "<p><i>" + data.errores[2].fatalerrors[i].causes[j].cause + "</i></p>";
					}
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase11(data) {
				var htmlString = "";
				htmlString += "<p><h1>Consulta 1</h1></p>";
				htmlString += "<p><h2>Selecciona un año:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data.anios.anio.length; i++){
					var number = i + 1;
					htmlString += "<p><input type='radio' name='panio' id='panio' value='" + data.anios.anio[i] + "' checked>" + number + ".- " + data.anios.anio[i] + "</p>";
					
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase12(data) {
				var htmlString = "";
				htmlString += "<input type='hidden' id='panio' name='panio' value='" + data[0].anio + "'>";
				htmlString += "<p><h1>Consulta 1: Año=" + data[0].anio + "</h1></p>";
				htmlString += "<p><h2>Selecciona una película:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data[1].peliculas.pelicula.length; i++){
					var number = i + 1;
					htmlString += "<p><input type='radio' name='ppelicula' id='ppelicula' value='" + data[1].peliculas.pelicula[i].nombre + "' checked>" + number + ".- " + data[1].peliculas.pelicula[i].nombre + " (" + data[1].peliculas.pelicula[i].duracion + " min.) (" + data[1].peliculas.pelicula[i].langs + ")</p>";
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase13(data) {
				var htmlString = "";
				htmlString += "<input type='hidden' id='panio' name='panio' value='" + data[0].anio + "'>";
				htmlString += "<input type='hidden' id='ppelicula' name='ppelicula' value='" + data[1].pelicula + "'>";
				htmlString += "<p><h1>Consulta 1: Año=" + data[0].anio + ", Película=" + data[1].pelicula + "</h1></p>";
				htmlString += "<p><h2>Selecciona un actor:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data[2].reparto.act.length; i++){
					var number = i + 1;
					htmlString += "<p><input type='radio' name='pact' id='pact' value='" + data[2].reparto.act[i].nombre + "' checked>" + number + ".- " + data[2].reparto.act[i].nombre + " (" + data[2].reparto.act[i].ciudad + ")</p>";
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase14(data) {
				var htmlString = "";
				htmlString += "<input type='hidden' id='panio' name='panio' value='" + data[0].anio + "'>";
				htmlString += "<input type='hidden' id='ppelicula' name='ppelicula' value='" + data[1].pelicula + "'>";
				htmlString += "<input type='hidden' id='pact' name='pact' value='" + data[2].filmografia.nombre + "'>";
				htmlString += "<p><h1>Consulta 1: Año=" + data[0].anio + ", Película=" + data[1].pelicula + ", Act=" + data[2].filmografia.nombre + "</h1></p>";
				htmlString += "<p><h2>El personaje es: "+ data[2].filmografia.personaje + "</h2></p>";
				htmlString += "<p><h2>Esta es su filmografía:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data[2].filmografia.film.length; i++){
					var number = i + 1;
					if(data[2].filmografia.film[i].oscar == ""){
						htmlString += "<p>·" + number + ".- <b>Título</b>=" + data[2].filmografia.film[i].nombre + "</p>";
					}
					else{
						htmlString += "<p>·" + number + ".- <b>Título</b>=" + data[2].filmografia.film[i].nombre + ", <b>Óscar</b>=" + data[2].filmografia.film[i].oscar + "</p>";
					}
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase21(data) {
				var htmlString = "";
				htmlString += "<p><h1>Consulta 2</h1></p>";
				htmlString += "<p><h2>Selecciona un idioma:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data.langs.lang.length; i++){
					var number = i + 1;
					htmlString += "<p><input type='radio' name='plang' id='plang' value='" + data.langs.lang[i] + "' checked>" + number + ".- " + data.langs.lang[i] + "</p>";
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase22(data) {
				var htmlString = "";
				htmlString += "<input type='hidden' id='plang' name='plang' value='" + data[0].idioma + "'>";
				htmlString += "<p><h1>Consulta 2: Idioma=" + data[0].idioma + "</h1></p>";
				htmlString += "<p><h2>Selecciona un Actor/Actriz:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data[1].acts.act.length; i++){
					var number = i + 1;
					htmlString += "<p><input type='radio' name='pact' id='pact' value='" + data[1].acts.act[i].nombre + "' checked>" + number + ".- " + data[1].acts.act[i].nombre + " (" + data[1].acts.act[i].ciudad + ") -- " + data[1].acts.act[i].oscar + "</p>";
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase23(data) {
				var htmlString = "";
				htmlString += "<input type='hidden' id='plang' name='plang' value='" + data[0].idioma + "'>";
				htmlString += "<input type='hidden' id='pact' name='pact' value='" + data[1].actor + "'>";
				htmlString += "<p><h1>Consulta 2: Idioma=" + data[0].idioma + ", Actor/Actriz=" + data[1].actor + "</h1></p>";
				htmlString += "<p><h2>Selecciona un país:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data[2].paises.pais.length; i++){
					var number = i + 1;
					htmlString += "<p><input type='radio' name='ppais' id='ppais' value='" + data[2].paises.pais[i].nombre + "' checked>" + number + ".- " + data[2].paises.pais[i].nombre + " (" + data[2].paises.pais[i].num + " películas) -- idioma por defecto='" + data[2].paises.pais[i].lang + "'</p>";
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}

			function fase24(data) {
				var htmlString = "";
				htmlString += "<input type='hidden' id='plang' name='plang' value='" + data[0].idioma + "'>";
				htmlString += "<input type='hidden' id='pact' name='pact' value='" + data[1].actor + "'>";
				htmlString += "<input type='hidden' id='ppais' name='ppais' value='" + data[2].pais + "'>";
				htmlString += "<p><h1>Consulta 2: Idioma=" + data[0].idioma + ", Actor/Actriz=" + data[1].actor + ", Pais=" + data[2].pais + "</h1></p>";
				htmlString += "<p><h2>Estas son sus películas:</h2></p>";
				htmlString += "<fieldset>";
				for(i=0; i<data[3].titulos.titulo.length; i++){
					var number = i + 1;
					htmlString += "<p>·" + number + ".- <b>Película</b>=" + data[3].titulos.titulo[i].nombre + ", <b>IP</b>=" + data[3].titulos.titulo[i].ip + "</p>";
				}
				htmlString += "</fieldset>";
				fase.insertAdjacentHTML('beforeend', htmlString);
			}
		</script>
	</body>
</html>
