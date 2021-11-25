<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>


<c:if test="${not empty user and user.role eq 'ADMIN'}">
    <l:redirectIfEmpty value="${appRoles}" errorMsg="Application roles were not initialized at startup" />
</c:if>
<l:redirectIfEmpty value="${users}" errorMsg="No users are in session" />
<l:redirectIfEmpty value="${param.id}" errorMsg="No id passed" />
<l:getEntityByID value="${users}" var="proceedUser" lookUpID="${param.id}" />


<div class="container-sm bg-light border col-sm-6 col-sm-offset-3 my-5 pt-2">
    <div class="container-sm col-sm-10 col-sm-offset-1">
        <div class="row">
                <h1><fmt:message key='header.edit'/></h1>
        </div>
        <form action="/controller" method="post">
            <input type="hidden" name="command" value="user.edit">
            <input type="hidden" name="userID" value="<c:out value='${param.id}' />">
            <div class="row mb-1">
                <label for="email" class="col-md-3 col-form-label"><fmt:message key='header.email'/>: </label>
                <div class="col-md-7">
                    <input name="emailReg" type="email" id="email" class=" form-control" required value="<c:out value='${proceedUser.email}' />"><br/>
                </div>
            </div>
            <div class="row mb-1">
                <label for="password" class="col-md-3 col-form-label"><fmt:message key='header.password'/>: </label>
                <div class="col-md-7">
                    <input name="passwordReg" type="password" id="password" class="col-md-6 form-control">
                </div>
            </div>
            <div class="row mb-2">
                <label for="anotherPass" class="col-md-3 col-form-label"><fmt:message key='header.confirm.password'/>: </label>
                <div class="col-md-7">
                    <input name="anotherPass" type="password" id="anotherPass" class="col-md-6 form-control">
                </div>
            </div>
            <div class="row mb-2">
                <label for="name" class="col-md-3 col-form-label"><fmt:message key='header.name'/>: </label>
                <div class="col-md-7">
                    <input name="name" type="text" id="name" class="form-control" value="<c:out value='${proceedUser.name}' />">
                </div>
            </div>
            <div class="row mb-2">
                <label for="roles" class="col-md-3 col-form-label"><fmt:message key='header.role'/>: </label>
                <div class="col-md-3">
                    <select id="roles" class="form-select" aria-label="Default select example">
                        <c:forEach var="role" items="${appRoles}">
                            <option <c:if test="${role eq fn:toLowerCase(proceedUser.role)}">selected</c:if>><c:out value="${role}" /></option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-check">
              <input class="form-check-input" type="checkbox" name="state" id="flexCheckChecked" <c:if test="${proceedUser.state eq 'VALID'}">checked</c:if>>
              <label class="form-check-label" for="flexCheckChecked" >
                <fmt:message key='header.enable'/>
              </label>
            </div>
            <div class="row mb-2 my-2">
            <!-- TODO check if retype is the same as password -->
                <div class="col-sm container overflow-hidden">
                    <p class="text-danger fw-bold"><c:out value="${userError}" /></p>
                    <button type="submit" class="btn btn-primary">
                        <fmt:message key='header.update'/>
                    </button>
                    <a class="btn btn-danger" href="/jsp/users.jsp">
                        <fmt:message key='header.cancel'/>
                    </a>
                </div>
            </div>
        </form>
    </div>
</div>
<jsp:include page="/html/footer.html"/>
<c:set var="userError" scope="session" value="" />