<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${lang.code}" />
<fmt:setBundle basename="i18n" />

<%@ attribute name="action" required="true" %>
<%@ attribute name="searchParameters" required="true" rtexprvalue="true" type="java.util.List"%>


<div class="container pt-4">
    <form action="/controller" >
        <input type="hidden" name="command" value="${action}">
        <div class="row align-items-center">
            <input class="form-control-lg col-7" type="search" name="query" placeholder="<fmt:message key='search.placeholder.msg'/>" value="<c:out value='${param.query}' />">
            <div class="form-floating col" <c:if test="${searchParameters.size() eq 1}">hidden</c:if> >
                <select class="form-select" name="searchBy" id="searchBy" aria-label="searchBy">
                    <c:forEach var="attr" items="${searchParameters}">
                        <option class="dropdown-item" value="${attr}"><fmt:message key="header.${attr}"/></option>
                    </c:forEach>
                </select>
                <label for="searchBy"><fmt:message key="header.search.by"/></label>
            </div>
            <div class="form-floating col" <c:if test="${searchParameters.size() eq 1}">hidden</c:if>>
                <select class="form-select" name="sortBy" id="sortBy" aria-label="Sort by">
                    <c:forEach var="attr" items="${searchParameters}">
                        <option class="dropdown-item" value="${attr}"><fmt:message key="header.${attr}"/></option>
                    </c:forEach>
                </select>
                <label for="sortBy"><fmt:message key='header.sort.by'/></label>
            </div>
            <div class="form-floating col">
                <select class="form-select" id="showBy" aria-label="show by" name="num">
                    <option>5</option>
                    <option>10</option>
                    <option>20</option>
                </select>
                <label for="showBy"><fmt:message key='header.show.by'/></label>
            </div>
            <div class="col">
                <button type="submit" class="btn btn-primary"><fmt:message key='header.search'/></button>
            </div>
        </div>
    </form>
</div>