<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="common/header.jsp" />

    <div class="container">
        <div class="panel panel-danger">
            <div class="panel-heading">
				<h4><span class="glyphicon glyphicon-exclamation-sign"></span> ${errorTitle != null ? errorTitle : "System Error!"}</h4>
			</div>
			<c:if test="${errorMsg != null}">
            <div class="panel-body">
                <p>${errorMsg}</p>
            </div>
			</c:if>
        </div>    
    </div>

<jsp:include page="common/footer.jsp" />