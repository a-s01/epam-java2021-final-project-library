<%@ include file="/WEB-INF/jspf/error_page_directive.jspf" %>

<div class="container pt-4">
    <div class="container">
        <h1><fmt:message key='message.sorry'/></h1></div>
        <p><fmt:message key="${serviceError}"/></p>
    </div>
</div>

<c:set var="serviceError" scope="session" value="${null}" />

<jsp:include page="/WEB-INF/jspf/footer.jsp"/>