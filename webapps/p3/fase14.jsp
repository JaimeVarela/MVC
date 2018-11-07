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
		<h2><p>Consulta 1: Año=${panio}, Película=${ppelicula}, Act=${pact}</p></h2>
		<h2><p>El personaje es: ${filmografia.nombrePersonaje}</p></h2>
		<h2><p>Esta es su filmografía:</p></h2>
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<input type='hidden' id='fase' name='pfase' value='01'>
			<input type='hidden' id='panio' name='panio' value='${panio}'>
			<input type='hidden' id='ppelicula' name='ppelicula' value='${ppelicula}'>
			<input type='hidden' id='pact' name='pact' value='${pact}'>
			<fieldset>
				<c:forEach items="${filmografia.filmografia}" var="film" varStatus="status">
					<c:choose>
						<c:when test="${filmografia.oscares[status.index] == ''}">
							<p>·${status.index + 1}.- <b>Título</b>=${film}</p>
						</c:when>
						<c:otherwise>
							<p>·${status.index + 1}.- <b>Título</b>=${film}, <b>Óscar</b>=${filmografia.oscares[status.index]}</p>
    						</c:otherwise>
					</c:choose>
				</c:forEach>
			</fieldset>
			<input type='submit' value='Atrás' onclick='goBack()'>
			<input type='submit' value='Inicio' onclick='goInicio()'>
			<input type='submit' value='Next' onclick='goNext()'>
			<script>
				function goBack() {
					document.getElementById('fase').value = '13'
				}
			</script>
			<script>
				function goInicio() {
					document.getElementById('fase').value = '01'
				}
			</script>
			<script>
				function goNext() {
					document.getElementById('fase').value = '15'
				}
			</script>
		</form>
		<address> Autor: Jaime Varela de la Escalera de Miguel </address>
	</body>
</html>
