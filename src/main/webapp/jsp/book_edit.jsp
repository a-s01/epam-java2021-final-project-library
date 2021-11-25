<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="thisYear" value="${now}" pattern="yyyy" />

<c:if test="${not empty user and user.role eq 'ADMIN'}">
    <l:redirectIfEmpty value="${appRoles}" errorMsg="Application roles were not initialized at startup" />
</c:if>
<l:redirectIfEmpty value="${books}" errorMsg="No books are in session" />
<l:redirectIfEmpty value="${param.command}" errorMsg="No command passed" />

<l:getEntityByID value="${books}" var="proceedBook" lookUpID="${param.id}" />
    <div class="container-sm bg-light border col-sm-6 col-sm-offset-3 my-5 pt-2">
        <div class="container-sm col-sm-10 col-sm-offset-1">
            <div class="row">
                    <h1><fmt:message key='header.edit'/></h1>
            </div>
            <form action="/controller" method="post">
                <input type="hidden" name="command" value="${param.command}">
                <input type="hidden" name="bookID" value="<c:out value='${param.id}' />">
                <div class="row mb-1">
                    <label for="title" class="col-md-3 col-form-label"><fmt:message key='header.title'/>: </label>
                    <div class="col-md-7">
                        <input name="title" type="text" id="title"
                            class="col-md-6 form-control" required value="<c:out value='${proceedBook.title}' />">
                    </div>
                </div>
                <div class="row mb-1">
                    <label for="isbn" class="col-md-3 col-form-label"><fmt:message key='header.isbn'/>: </label>
                    <div class="col-md-7">
                        <input name="isbn" type="text" id="isbn"
                            class="col-md-6 form-control" required value="<c:out value='${proceedBook.isbn}' />">
                    </div>
                </div>
                <div class="row mb-2">
                    <label for="year" class="col-md-3 col-form-label"><fmt:message key='header.year'/>: </label>
                    <div class="col-md-7">
                        <input name="year" type="range" id="year" min=1900 max="${thisYear}"
                            class="form-range" value="<c:out value='${proceedBook.year}' />"
                            oninput="this.nextElementSibling.value = this.value">
                        <output><c:out value='${proceedBook.year}' /></output>
                    </div>
                </div>
                <div class="row mb-2">
                    <label for="keepPeriod" class="col-md-3 col-form-label"><fmt:message key='header.keep.period'/>: </label>
                    <div class="col-md-7">
                        <input name="keepPeriod" type="range" min="1" max="60" step="1" id="keepPeriod" class="form-range" value="<c:out value='${proceedBook.keepPeriod}' />" oninput="this.nextElementSibling.value = this.value">
                        <output><c:out value='${proceedBook.keepPeriod}' /></output>
                    </div>
                </div>
                <div class="row mb-2">
                    <label for="total" class="col-md-3 col-form-label"><fmt:message key='header.amount'/>: </label>
                    <div class="col-md-7">
                        <input name="total" type="number" id="total" class="form-control"
                            value="<c:out value='${proceedBook.bookStat.total}' />">
                    </div>
                </div>
                <!-- TODO add authors -->
                <div class="row mb-2 my-2">
                    <div class="col-sm container overflow-hidden">
                        <p class="text-danger"><c:out value="${userError}" /></p>
                        <button type="submit" class="btn btn-primary"><fmt:message key='header.update'/></button>
                        <a class="btn btn-danger" href="/jsp/home.jsp"><fmt:message key='header.cancel'/></a>
                    </div>
                </div>
            </form>
        </div>
    </div>
<jsp:include page="/html/footer.html"/>
<c:set var="userError" scope="session" value="" />