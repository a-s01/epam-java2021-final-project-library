<%@ include file="/WEB-INF/jspf/error_page_directive.jspf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="container">
    <div><h1><fmt:message key='message.sorry'/></h1></div>
    <div>${serviceError}</div>
</div>
<jsp:include page="/html/footer.html"/>