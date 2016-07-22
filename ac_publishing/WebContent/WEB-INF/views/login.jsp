<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
<style>
 <style>
.error {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #a94442;
	background-color: #f2dede;
	border-color: #ebccd1;
}

.msg {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #31708f;
	background-color: #d9edf7;
	border-color: #bce8f1;
}

#login-box {
	width: 300px;
	padding: 20px;
	margin: 100px auto;
	background: #fff;
	-webkit-border-radius: 2px;
	-moz-border-radius: 2px;
	border: 1px solid #000;
}
</style>

</head>
<body>
<div id="login-box">
 
	<c:if test="${not empty errorMsg}">
		<div class="error"> <c:out value = "${errorMsg}" /> </div>
	</c:if>
	
  <form:form modelAttribute="userAttribute" action="authenticate">
   
    <form:label path="userName" >User Name</form:label>
    <form:input path="userName"/>
    <form:errors path="userName" cssClass="error"/>
    	<br />
    <form:label path="password" >Password</form:label>
    <form:input path="password"/>
    <form:errors path="password" cssClass="error"/><br />
    <input type="submit" value = "Login">
 
      
    
  </form:form>
  </div>
</body>
</html>