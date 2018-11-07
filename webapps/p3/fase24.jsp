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
		<h2><p>Consulta 2: Idioma=${plang}, Actor/Actriz=${pact}, Pais=${ppais}</p></h2>
		<h2><p>Estas son sus películas:</p></h2>
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<input type='hidden' id='fase' name='pfase' value='01'>
			<input type='hidden' id='plang' name='plang' value='${plang}'>
			<input type='hidden' id='pact' name='pact' value='${pact}'>
			<input type='hidden' id='ppais' name='ppais' value='${ppais}'>
			<fieldset>
				<c:forEach items="${peliculas.peliculas}" var="pelicula" varStatus="status">
					<p>${status.index + 1}.- <b>Película</b>=${pelicula}, <b>IP</b>=${peliculas.ipPeliculas[status.index]}</p>
				</c:forEach>
			</fieldset>
			<input type='submit' value='Atrás' onclick='goBack()'>
			<input type='submit' value='Inicio' onclick='goInicio()'>
			<script>
				function goBack() {
					document.getElementById('fase').value = '23'
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
