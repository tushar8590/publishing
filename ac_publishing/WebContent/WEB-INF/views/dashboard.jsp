<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
      <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
      <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href='<c:url value="/resources/style/view.css"  />' media="all">
<script type="text/javascript" src='<c:url value="/resources/js/view.js" />'></script>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Select the module</title>
</head>
<body>
<img id="top" src='<c:url value="/resources/images/top.png"/>' alt="">
	<div id="form_container">
	
		<h1>Test </h1>
		<form:form id="form_1089583" method="post" action="loadModule" cssClass="appnitro" commandName="module">
		
					<div class="form_description">
			<h2>DataMaster Modules</h2>
			
		</div>						
			<ul >
			
					<li id="li_1" >
		<label class="description" for="element_1"> Select Module </label>
		<span>
		

<form:radiobuttons path="moduleName" items="${radioList}" cssClass="element radio"/>

		</span> 
		</li>
			
					<li class="buttons">
			    
			    
				<input id="saveForm" class="button_text" type="submit" name="submit" value="Submit" />
		</li>
			</ul>
		</form:form>	
	
	</div>
	<img id="bottom" src='<c:url value="/resources/images/bottom.png" />'  alt="">
</body>
</html>