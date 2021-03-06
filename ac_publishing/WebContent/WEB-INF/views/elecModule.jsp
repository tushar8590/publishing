<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href='<c:url value="/resources/style/view.css"  />' media="all">
<script type="text/javascript" src='<c:url value="/resources/js/view.js" />'></script>

<title>Electronics Module</title>
</head>
<body>
<img id="top" src='<c:url value="/resources/images/top.png"/>' alt="">
	<div id="form_container">
	<form:form id="form_1089583" method="post" action="startELectronicsDataUpdate" cssClass="appnitro" commandName="newMenuAttribute">
					<div class="form_description">
			<h2>DataMaster Electronics Modules</h2>
			
		</div>						
			<ul >
		<li id="li_1" >
		<label class="description" for="section"> Select Category </label>
		<span>
		<form:select path="section" multiple="true" items="${categoryList}">
		</form:select>
		<form:hidden path="subModuleName" value = "${modelName}"/><br />
		<form:radiobutton path="allCats" value="allCats" label="All categories"/>
		<input id="saveForm" class="button_text" type="submit" name="submit" value="Submit" />
		
		<br /><br /><br /><br /><br /><br />
		</span>
	</ul>
	
	</form:form>
	
	</div>
	<img id="bottom" src='<c:url value="/resources/images/bottom.png" />'  alt="">
</body>
</html>