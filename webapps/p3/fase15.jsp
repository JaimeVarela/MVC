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
		<h2><p>EXAMEN</p></h2>
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<input type='hidden' id='pfase' name='pfase' value='01'>
			<fieldset>
				<c:choose>
					<c:when test="${examen.color == 'rojo'}">
						<p><l1>Año: ${examen.anio}<l1></p>
						<p><l1>Oscares: ${examen.oscares}<l1></p>
					</c:when>
					<c:otherwise>
						<p><l2>Año: ${examen.anio}<l2></p>
						<p><l2>Oscares: ${examen.oscares}<l2></p>
					</c:otherwise>
				</c:choose>
			</fieldset>
			<p><input type='submit' value='Inicio'></p>
		</form>
		<address> Autor: Jaime Varela de la Escalera de Miguel </address>
	</body>
</html>
