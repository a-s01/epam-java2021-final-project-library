<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<l:redirectIfEmpty value="${param.command}" errorMsg="No command passed" />

<c:if test="${not empty user and user.role eq 'ADMIN'}">
    <l:redirectIfEmpty value="${appRoles}" errorMsg="Application roles were not initialized at startup" />
    <c:if test="${not empty searchLink}" >
        <c:set value="${searchLink}" var="cancelLink"/>
    </c:if>
    <c:if test="${empty searchLink}">
        <c:set value="/jsp/admin/users.jsp" var="cancelLink"/>
    </c:if>
    <c:if test="${param.command eq 'user.add'}">
        <c:set value="header.create.user" var="currentHeader"/>
    </c:if>
    <c:if test="${param.command eq 'user.edit'}">
        <c:set value="header.edit.user" var="currentHeader"/>
    </c:if>
</c:if>

<c:if test="${empty user}">
    <c:set value="/jsp/home.jsp" var="cancelLink" />
    <c:set value="header.register" var="currentHeader"/>
</c:if>

<fmt:setLocale value="${lang.code}" />
<fmt:setBundle basename="i18n" />

<div class="container-sm bg-light border col-sm-6 col-sm-offset-3 my-5 pt-2">
    <div class="container-sm col-sm-10 col-sm-offset-1">
        <div class="row">
            <h1>
                <fmt:message key='${currentHeader}'/>
            </h1>
        </div>
        <form action="/controller" method="post" accept-charset="UTF-8">
            <input type="hidden" name="command" value="${param.command}">
            <div class="row mb-1">
                <label for="email" class="col-md-3 col-form-label"><fmt:message key='header.email'/>: </label>
                <div class="col-md-7">
                    <input name="email" type="email" id="email" class=" form-control" onkeyup="check(this);" required
                        <c:if test="${not empty user and user.role eq 'ADMIN'}">
                            value="<c:out value='${proceedUser.email}' />"
                        </c:if>
                    >
                </div>
            </div>
            <div class="row mb-1">
                <label for="password" class="col-md-3 col-form-label"><fmt:message key='header.password'/>: </label>
                <div class="col-md-7">
                    <input name="password" type="password" id="password"
                        class="col-md-6 form-control" onkeyup='checkPass();'>
                </div>
            </div>
            <div class="row mb-2">
                <label for="confirmPass" class="col-md-3 col-form-label"><fmt:message key='header.confirm.password'/>: </label>
                <div class="col-md-7">
                    <input name="confirmPass" type="password" id="confirmPass"
                        class="col-md-6 form-control" onkeyup='checkPass();'>
                    <span id='message' class="text-danger" hidden><fmt:message key="error.msg.passwords.dont.match"/></span>
                </div>
            </div>
            <div class="row mb-2">
                <label for="name" class="col-md-3 col-form-label"><fmt:message key='header.name'/>: </label>
                <div class="col-md-7">
                    <input name="name" type="text" id="name" class="form-control" onkeyup="makeValid(this);"
                        <c:if test="${not empty user and user.role eq 'ADMIN'}">
                            value="<c:out value='${proceedUser.name}' />"
                        </c:if>
                    >
                </div>
            </div>
            <c:if test="${not empty user and user.role eq 'ADMIN'}">
                <div class="row mb-2">
                    <label for="roles" class="col-md-3 col-form-label"><fmt:message key='header.role'/>: </label>
                    <div class="col-md-3">
                        <select name="role" id="roles" class="form-select" aria-label="<fmt:message key='header.role'/>">
                            <c:forEach var="role" items="${appRoles}">
                                <option <c:if test="${role eq fn:toLowerCase(proceedUser.role)}">selected</c:if>><c:out value="${role}" /></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" name="state" id="flexCheckChecked" <c:if test="${empty proceedUser or proceedUser.state eq 'VALID'}">checked</c:if>>
                  <label class="form-check-label" for="flexCheckChecked" >
                    <fmt:message key='header.enable'/>
                  </label>
                </div>
            </c:if>
            <div class="row mb-2 my-2">
                <div class="col-sm container overflow-hidden">
                    <p class="text-danger fw-bold"><c:if test="${not empty userError}"><fmt:message key='${userError}'/></c:if></p>
                    <button type="submit" class="btn btn-primary">
                        <fmt:message key='header.apply'/>
                    </button>
                    <a class="btn btn-danger" href="${cancelLink}">
                        <fmt:message key='header.cancel'/>
                    </a>
                </div>
            </div>
        </form>
    </div>
</div>
<jsp:include page="/WEB-INF/jspf/footer.jsp"/>
<c:set var="userError" scope="session" value="" />