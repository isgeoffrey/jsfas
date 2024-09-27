<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="jsfas.common.object.MenuItem" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/> 
    <meta name="_cas_expire_url" content="${properties['cas.service.expire']}"/> 
    <meta name="buildTime" content="${properties['build.time']}"/>
    <meta name="buildId" content="${properties['build.id']}"/>

    <title>${pageTitle}</title>

	<!-- CSS -->
    <!-- Bootstrap Core CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css?v=${cacheVersion.staticVer}" rel="stylesheet">

	<!-- DataTables CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.bootstrap.min.css?v=${cacheVersion.staticVer}" rel="stylesheet">

    <!-- DataTables Responsive CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/dataTables.responsive.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
    <!-- DataTables Select CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/select.bootstrap.css?v=${cacheVersion.staticVer}" rel="stylesheet">
	
	<!-- Bootstrap Date Picker  -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap-datepicker.standalone.min.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
    <!-- Bootstrap Date Time Picker  -->
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap-datetimepicker.standalone.min.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
    <!-- Bootstrap Autocomplete Table CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/tautocomplete.bootstrap.min.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
    <!-- Font Awesome CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/font-awesome.min.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
    <!-- zTree Font Awesome CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/ztree.awesome.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
	<!-- Common CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/common.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
    <!-- loading spin -->
    <link href="${pageContext.request.contextPath}/resources/css/loading-spin.css?v=${cacheVersion.staticVer}" rel="stylesheet">
	
	<!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/resources/css/jsfas.css?v=${cacheVersion.staticVer}" rel="stylesheet">
    
	<!-- jQuery --> <%-- only jQuery js lib put on header --%>
    <script src="${pageContext.request.contextPath}/resources/js/jquery-3.3.1.min.js?v=${cacheVersion.staticVer}"></script>
    
</head>

<body class="${bodyCssClass}">
	<!-- loading spin -->
	<div class="loading" id="loadingSpin" style="display:none;">Loading...</div>
	
	<!-- Fixed navbar -->
	<nav id="nav" class="navbar navbar-default navbar-fixed-top">
		<div class="container">
			<div class="hkust-logo-title-box">
				<span class="logo"><img height="24px" src="${pageContext.request.contextPath}/resources/images/hkust.png"/></span>
				<span class="title title-long">${sysFullName}</span>
				<span class="title title-short">${sysShortName}</span>
			</div>
			<c:if test="${menuItemList != null}">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<c:forEach items="${menuItemList}" var="menuItem">
						<c:choose>
						<c:when test="${menuItem.subMenuList.size() > 0}">
							<li id="${menuItem.id}" class="dropdown"><a href="#" class="dropdown-toggle"
								data-toggle="dropdown" role="button" aria-haspopup="true"
								aria-expanded="false">${menuItem.name} <span class="caret"></span></a>
							<ul class="dropdown-menu">
							<c:forEach items="${menuItem.subMenuList}" var="subMenuItem">
								<c:choose>
								<c:when test="${subMenuItem.type == MenuItem.SEPARATOR}">
									<li role="separator" class="divider"></li>
								</c:when>
								<c:when test="${subMenuItem.type == MenuItem.DROPDOWN_HEADER}">
									<li class="dropdown-header">${subMenuItem.name}</li>
								</c:when>
								<c:when test="${subMenuItem.type == MenuItem.OUTSIDE_LINK}">
									<li id="${subMenuItem.id}"><a href="${subMenuItem.fullUrl}">${subMenuItem.name}</a></li>
								</c:when>
								<c:otherwise>
									<li id="${subMenuItem.id}"><a href="${pageContext.request.contextPath}${subMenuItem.restURI}">${subMenuItem.name}</a></li>
								</c:otherwise>
								</c:choose>
							</c:forEach>
							</ul></li>
						</c:when>
						<c:otherwise>
							<c:choose>
							<c:when test="${menuItem.type == MenuItem.OUTSIDE_LINK}">
								<li id="${menuItem.id}"><a href="${menuItem.fullUrl}">${menuItem.name}</a></li>
							</c:when>
							<c:otherwise>
								<li id="${menuItem.id}"><a href="${pageContext.request.contextPath}${menuItem.restURI}">${menuItem.name}</a></li>
							</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>
			</div>
			<!--/.nav-collapse -->
			</c:if>
		</div>
	</nav>
	<!-- Fixed navbar end -->

	<c:if test="${breadcrumbList !=null}">
	<!-- breadcrumb -->
	<div class="container">
		<span class="user-datetime">${userName} ${dateString}</span>
		<ol class="breadcrumb">
			<c:forEach items="${breadcrumbList}" var="breadcrumb" varStatus="status">
				<c:choose>
				<c:when test="${status.last}">
					<li class="active">${breadcrumb.name}</li>
				</c:when>
				<c:otherwise>
					<li><a href="${pageContext.request.contextPath}${breadcrumb.restURI}">${breadcrumb.name}</a></li>
				</c:otherwise>
				</c:choose>
	        </c:forEach>
		</ol>
	</div>
	<!-- breadcrumb end -->
	</c:if>
	
	<%-- alert box --%>
	<div id="alertBoxWrapper">
		<div id="alertBox" class="container"></div>
	</div>

	<%-- Success Modal --%>
	<div class="modal fade" id="successModal" tabindex="-1" role="dialog" aria-labelledby="successLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content panel-success">
				<div class="modal-header panel-heading">
					<h4 class="modal-title">Success</h4>
				</div>
				<div class="modal-body">
					<div class="row">
						<div class="col-md-12">
							<span id="successMsg"></span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<div class="">
						<button type="button" class="btn btn-primary btn-sm" data-dismiss="modal" id="okBtn">
							<span class="glyphicon glyphicon glyphicon-ok"></span> OK
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>