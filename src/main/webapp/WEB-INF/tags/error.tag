<%@ attribute name="msg" required="true" rtexprvalue="true"%>
<%@ attribute name="msgParams" rtexprvalue="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${lang.code}" />
<fmt:setBundle basename="i18n" />

<p class="text-danger">
    <c:if test="${not empty msg}" >
        <fmt:message key='${msg}'>
            <c:if test="${not empty msgParams}" >
                <c:forEach var="param" items="${msgParams}">
                    <fmt:param value="${param}" />
                </c:forEach>
            </c:if>
        </fmt:message>
    </c:if>
</p>