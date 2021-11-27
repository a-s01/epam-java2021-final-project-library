<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<l:redirectIfEmpty value="${param.command}" errorMsg="No command passed" />

<c:if test="${not empty searchLink}" >
    <c:set value="${searchLink}" var="cancelLink"/>
</c:if>
<c:if test="${empty searchLink}">
    <c:set value="/jsp/admin/users.jsp" var="cancelLink"/>
</c:if>

<div class="container-sm bg-light border col-sm-6 col-sm-offset-3 my-5 pt-2">
    <div class="container-sm col-sm-10 col-sm-offset-1">
        <div class="row">
            <h1>
                <fmt:message key='header.edit'/>
            </h1>
        </div>
        <form action="/controller" method="post">
            <input type="hidden" name="command" value="${param.command}">
            <c:forEach var="i18name" items="${proceedAuthor.i18Names}">
                <div class="row mb-1">
                    <label for="${i18name.lang.code}" class="col-md-3 col-form-label">
                        <fmt:message key='header.name.in.lang'/> <c:out value="${i18name.lang.code}" />:
                    </label>
                    <div class="col-md-7">
                        <input name="${i18name.lang.code}" type="text" id="${i18name.lang.code}"
                            class="col-md-6 form-control"
                            required value="<c:out value='${i18name.name}' />"
                            >
                    </div>
                </div>
            </c:forEach>
            <div class="row mb-2">
                <label for="primaryLang" class="col-md-3 col-form-label"><fmt:message key='header.primary.language'/>: </label>
                <div class="col-md-3">
                    <select name="primaryLang" id="primaryLang" class="form-select" aria-label="<fmt:message key='header.primary.language'/>">
                        <c:forEach var="lang" items="${langs}">
                            <option><c:out value="${lang.code}" /></option>
                        </c:forEach>
                    </select>
                </div>
             </div>
            <div class="row mb-2 my-2">
                <div class="col-sm container overflow-hidden">
                    <p class="text-danger">
                        <c:if test="${not empty userError}" >
                            <fmt:message key='${userError}'/>
                        </c:if>
                    </p>
                    <button type="submit" class="btn btn-primary"><fmt:message key='header.apply'/></button>
                    <a class="btn btn-danger" href="${cancelLink}"><fmt:message key='header.cancel'/></a>
                </div>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/html/footer.html"/>
<c:set var="userError" scope="session" value="" />