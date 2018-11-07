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
		<h2><p>Consulta 1</p></h2>
		<h2><p>Selecciona un año:</p></h2>
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<input type='hidden' id='pfase' name='pfase' value='12'>
			<fieldset>
				<c:forEach items="${anios.anios}" var="anio" varStatus="status">
					<p><input type='radio' name='panio' value='${anio}' checked>${status.index + 1}.- ${anio}</p>
				</c:forEach>
			</fieldset>
			<p><input type='submit' value='Enviar'></p>
			<input type='submit' value='Atrás' onclick='goBack()'>
			<script>
				function goBack() {
					document.getElementById('fase').value = '01'
				}
			</script>
		</form>
		<address> Autor: Jaime Varela de la Escalera de Miguel </address>
	</body>
</html>
