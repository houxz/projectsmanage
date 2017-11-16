<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
    <spring:url var="res" value="/resources"></spring:url>
    <head>
        <meta charset="UTF-8">
        <title>UncaughtException</title>
        <link href="${res}/css/bootstrap.min.css" rel="stylesheet" media="screen">
    </head>
    <body>
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span10 offset1 well" style="margin-top:20px">
                    <h2 class="text-error"><spring:message code="error.uncaughtexception.title" /></h2>
			        <p><spring:message code="error.uncaughtexception.problemdescription" /></p>
			        <c:if test="${not empty exception}">
			            <div>
			                <h4><spring:message code="exception.details" /></h4>
			                <p><spring:message var="message" code="exception.message" /></p>
			                <div id="_exception">
			                    <c:out value="${exception.localizedMessage}" />
			                </div>
			                <p><spring:message var="stacktrace" code="exception.stacktrace" /></p>
			                <div id="_stacktrace">
			                    <c:forEach items="${exception.stackTrace}" var="trace">
			                        <c:out value="${trace}"/><br/>
			                    </c:forEach>
			                </div>
			            </div>
			        </c:if>
			    </div>  
			</div>
        </div>
    </body>
</html>