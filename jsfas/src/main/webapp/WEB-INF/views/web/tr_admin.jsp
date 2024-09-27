<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="jsfas.common.constants.AppConstants" %>
<%@page import="jsfas.common.constants.RestURIConstants" %>
<jsp:include page="common/header.jsp" />

<div class="container">

	<div class="row">
		<%--
		<div class="col-md-3">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<div class="text-center">Function Groups</div>
				</div>
				<div class="list-group">
					<c:forEach items="${functionCatalogList}" var="functionCatalog">
						<a href="#funcCat${functionCatalog.gpFunctionCatalogPK.systemCatalogCode}${functionCatalog.gpFunctionCatalogPK.functionCatalogCode}" class="list-group-item">${functionCatalog.functionCatalogDesc}</a>
					</c:forEach>
				</div>
			</div>
		</div>
		--%>
		
		<div class="col-md-12">
			<div class="panel panel-info">
				<div class="panel-heading">
					<h5 class="text-center">${sysFullName}</h5>
				</div>
				<div class="panel-body">
					<c:forEach items="${functionCatalogList}" var="functionCatalog">
						<div class="panel panel-info" id="funcCat${functionCatalog.functionCatalogPK.systemCatalogCode}${functionCatalog.functionCatalogPK.functionCatalogCode}">
							<div class="panel-heading">
								${functionCatalog.functionCatalogDesc}
							</div>
							<div class="list-group">
								<c:forEach items="${functionCatalog.functions}" var="function">
									<c:if test="${function.menuGenInd.equalsIgnoreCase('Y')}">
									<a id="function${function.functionPK.functionCode}${function.functionPK.functionSubCode.trim()}" href="${pageContext.request.contextPath}/${function.firstPageName}?dummy=${cacheVersion.dynamicVer}" class="list-group-item">${function.functionDesc}</a>
									</c:if>
								</c:forEach>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
			
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-12">
			Built with Spring-Boot 2.1.3.RELEASE<br>
			(Development message, remove for new project)<br><br>
		</div>
	</div>

</div>
<div class="clearfix"></div>


<!-- /container -->

<script>
//---define global variables---

//---functions---

//---document.ready---
$(document).ready(function() {
	//define variables
	
	//custom vaildation methods
	
	//form vaildations
	
	//datepickers
	
	//button functions
	
	//alerts
	
	//others
});
</script>

<jsp:include page="common/footer.jsp" />