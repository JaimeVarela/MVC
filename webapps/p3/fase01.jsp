<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
	<head>
		<title>Práctica 3</title>
		<meta charset='UTF-8'>
		<link rel='stylesheet' type='text/css' href="${css}">
	</head>
	<body>
		<h1><p>Servicio de consultas de películas</p></h1>
		<h1><p>Bienvenido a este servicio</p></h1>
		<h2><p>Selecciona una consulta:</p></h2>
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<fieldset>
				<p><input type='radio' name='pfase'id="pfase"  value='02' checked>Ver los ficheros erróneos</p>
				<p><input type='radio' name='pfase'id="pfase"  value='11' checked>Consulta 1: Filmografía de un miembro del reparto</p>
				<p><input type='radio' name='pfase'id="pfase"  value='21' checked>Consulta 2: Películas en un idioma, de un actor/actriz, producidas en un país</p>
				<p><input type='radio' name='pfase'id="pfase"  value='30' checked>Modo AJAX</p>
			</fieldset>
			<input type='submit' value='Enviar'>
		</form>
		<address> Autor: Jaime Varela de la Escalera de Miguel </address>
	</body>
</html>
