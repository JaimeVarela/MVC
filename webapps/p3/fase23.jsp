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
		<h2><p>Consulta 2: Idioma=${plang}, Actor/Actriz=${pact}</p></h2>
		<h2><p>Selecciona un Actor/Actriz:</p></h2>
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<input type='hidden' id='fase' name='pfase' value='24'>
			<input type='hidden' id='plang' name='plang' value='${plang}'>
			<input type='hidden' id='pact' name='pact' value='${pact}'>
			<fieldset>
				<c:forEach items="${paises.paises}" var="pais" varStatus="status">
					<p><input type='radio' name='ppais' value='${pais}' checked>${status.index + 1}.- ${pais} (${paises.numPeliculas[status.index]} películas) -- idioma por defecto='${paises.defLangs[status.index]}'</p>
				</c:forEach>
			</fieldset>
			<p><input type='submit' value='Enviar'></p>
			<input type='submit' value='Atrás' onclick='goBack()'>
			<input type='submit' value='Inicio' onclick='goInicio()'>
			<script>
				function goBack() {
					document.getElementById('fase').value = '22'
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
