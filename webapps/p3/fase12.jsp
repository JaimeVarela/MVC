<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
	<head>
		<title>Práctica 3</title>
		<meta charset='UTF-8'>
		<link rel='stylesheet' type='text/css' href="${css}">
	</head>
	<body>
		<h1><p>Servicio de consultas de películas</p></h1>
		<h2><p>Consulta 1: Año=${panio}</p></h2>
		<h2><p>Selecciona una película:</p></h2>
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<input type='hidden' id='fase' name='pfase' value='13'>
			<input type='hidden' id='panio' name='panio' value='${panio}'>
			<fieldset>
				<c:forEach items="${peliculas.peliculas}" var="pelicula" varStatus="status">
					<p><input type='radio' name='ppelicula' value='${pelicula}' checked>${status.index + 1}.- ${pelicula} (${peliculas.duracion[status.index]} min.) (${peliculas.idioma[status.index]})</p>
				</c:forEach>
			</fieldset>
			<p><input type='submit' value='Enviar'></p>
			<input type='submit' value='Atrás' onclick='goBack()'>
			<input type='submit' value='Inicio' onclick='goInicio()'>
			<script>
				function goBack() {
					document.getElementById('fase').value = '11'
				}
			</script>
			<script>
				function goInicio() {
					document.getElementById('fase').value = '01'
				}
			</script>
		</form>
		<address> Autor: Jaime Varela de la Escalera de Miguel </address>
	</body>
</html>
