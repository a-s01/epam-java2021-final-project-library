<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<l:redirectIfEmpty value="${param.command}" errorMsg="No command passed" />

<c:if test="${not empty user and user.role eq 'ADMIN'}">
    <c:if test="${not empty searchLink}" >
        <c:set value="${searchLink}" var="cancelLink"/>
    </c:if>
    <c:if test="${empty searchLink}">
        <c:set value="/jsp/admin/users.jsp" var="cancelLink"/>
    </c:if>
    <c:if test="${param.command eq 'book.add'}">
        <c:set value="header.create.book" var="currentHeader"/>
    </c:if>
    <c:if test="${param.command eq 'book.edit'}">
        <c:set value="header.edit" var="currentHeader"/>
    </c:if>
</c:if>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="thisYear" value="${now}" pattern="yyyy" />

<div class="container-sm bg-light border col-sm-6 col-sm-offset-3 my-5 pt-2">
    <div class="container-sm col-sm-10 col-sm-offset-1">
        <div class="row">
            <h1>
                <fmt:message key='${currentHeader}'/>
            </h1>
        </div>
        <form action="/controller" method="post">
            <input type="hidden" name="command" value="${param.command}">
            <div class="row mb-1">
                <label for="title" class="col-md-3 col-form-label"><fmt:message key='header.title'/>: </label>
                <div class="col-md-7">
                    <input name="title" type="text" id="title"
                        class="col-md-6 form-control" required
                        <c:if test="${param.command eq 'book.edit'}">
                            value="<c:out value='${proceedBook.title}' />"
                        </c:if>
                        >
                </div>
            </div>
            <div class="row mb-1">
                <label for="isbn" class="col-md-3 col-form-label"><fmt:message key='header.isbn'/>: </label>
                <div class="col-md-7">
                    <input name="isbn" type="text" id="isbn"
                        class="col-md-6 form-control" required
                        <c:if test="${param.command eq 'book.edit'}">
                            value="<c:out value='${proceedBook.isbn}' />"
                        </c:if>
                        >
                </div>
            </div>
            <div class="row mb-2">
                <label for="year" class="col-md-3 col-form-label"><fmt:message key='header.year'/>: </label>
                <div class="col-md-7">
                    <input name="year" type="range" id="year" min=1900 max="${thisYear}"
                        class="form-range"
                        <c:if test="${param.command eq 'book.edit'}">
                            value="<c:out value='${proceedBook.year}' />"
                        </c:if>
                        oninput="this.nextElementSibling.value = this.value" required>
                    <output>
                        <c:if test="${param.command eq 'book.edit'}">
                            <c:out value='${proceedBook.year}' />
                        </c:if>
                    </output>
                </div>
            </div>
            <div class="row mb-2">
                <label for="langCode" class="col-md-3 col-form-label"><fmt:message key='link.language'/>: </label>
                <div class="col-md-7">
                    <input name="langCode" type="text" id="langCode" class="form-control"
                    <c:if test="${param.command eq 'book.edit'}">
                        value="<c:out value='${proceedBook.langCode}' />"
                    </c:if>
                    required>
                </div>
            </div>
            <div class="row mb-2">
                <label for="keepPeriod" class="col-md-3 col-form-label"><fmt:message key='header.keep.period'/>: </label>
                <div class="col-md-7">
                    <input name="keepPeriod" type="range" min="1" max="60" step="1" id="keepPeriod"
                        class="form-range"
                        <c:if test="${param.command eq 'book.edit'}">
                            value="<c:out value='${proceedBook.keepPeriod}' />"
                        </c:if>
                        oninput="this.nextElementSibling.value = this.value" required>
                    <output>
                        <c:if test="${param.command eq 'book.edit'}">
                            <c:out value='${proceedBook.keepPeriod}' />
                        </c:if>
                    </output>
                </div>
            </div>
            <div class="row mb-2">
                <label for="total" class="col-md-3 col-form-label"><fmt:message key='header.amount'/>: </label>
                <div class="col-md-7">
                    <input name="total" type="number" id="total" class="form-control"
                        <c:if test="${param.command eq 'book.edit'}">
                            value="<c:out value='${proceedBook.bookStat.total}' />"
                        </c:if>
                        required>
                </div>
            </div>
            <!-- TODO add authors -->
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
<c:set var="proceedBook" scope="session" value="" />