<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
    <spring:url var="res" value="/resources"></spring:url>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>accessDenied</title>
        <link href="${res}/css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link href="${res}/css/css.css" rel="stylesheet" media="screen">
    </head>
    <body>
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-10 col-md-offset-1 highlight" style="margin-top:20px">
                    <div class="bs-callout bs-callout-danger">
                        <h2><spring:message code="error.accessdenied.title" /></h2> 
                        <p><spring:message code="error.accessdenied.problemdescription" /></p>
			        </div>
			        <hr/>
			        <c:if test="${not empty exception}">
			            <div>
			                <h4><spring:message code="exception.details" /></h4>
			                <p><spring:message code="exception.message" /></p>
			                <div id="_exception">
			                    <c:out value="${exception.localizedMessage}" />
			                </div>
			                <p><spring:message code="exception.stacktrace" /></p>
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