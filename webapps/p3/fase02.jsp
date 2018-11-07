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
		<form>
			<input type='hidden' id='password' name='p' value='${p}'>
			<p>Se han encontrado ${errores.number_warning_xml} ficheros con warnings</p>
			<fieldset>
				<c:set var = "xml_actual" value = "${errores.file_warning_xml[0]}"/>
				<p><b>${xml_actual}</b></p>
				<c:forEach items="${errores.file_warning_xml}" var="warning" varStatus="status">
					<c:if test="${warning != xml_actual}">
						<p><b>${warning}</b></p>
						<c:set var = "xml_actual" value = "${warning}"/>
					</c:if>
					<p><b>${errores.cause_warning_xml[status.index]}</i></p>
				</c:forEach>
			</fieldset>
			<p>Se han encontrado ${errores.number_error_xml} ficheros con errores</p>
			<fieldset>
				<c:set var = "xml_actual" value = "${errores.file_error_xml[0]}"/>
				<p><b>${xml_actual}</b></p>
				<c:forEach items="${errores.file_error_xml}" var="error" varStatus="status">
					<c:if test="${error != xml_actual}">
						<p><b>${error}</b></p>
						<c:set var = "xml_actual" value = "${error}"/>
					</c:if>
					<p><i>${errores.cause_error_xml[status.index]}</i></p>
				</c:forEach>
			</fieldset>
			<p>Se han encontrado ${errores.number_fatalError_xml} ficheros con errores fatales</p>
			<fieldset>
				<c:set var = "xml_actual" value = "${errores.file_fatalError_xml[0]}"/>
				<p><b>${xml_actual}</b></p>
				<c:forEach items="${errores.file_fatalError_xml}" var="fatalError" varStatus="status">
					<c:if test="${fatalError != xml_actual}">
						<p><b>${fatalError}</b></p>
						<c:set var = "xml_actual" value = "${fatalError}"/>
					</c:if>
					<p><i>${errores.cause_fatalError_xml[status.index]}</i></p>
				</c:forEach>
			</fieldset>
			<p><input type='submit' value='Atrás'></p>
		</form>
		<address> Autor: Jaime Varela de la Escalera de Miguel </address>
	</body>
</html>
