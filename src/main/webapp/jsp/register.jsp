<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<l:redirectIfEmpty value="${param.command}" errorMsg="No command passed" />

<c:choose>
    <c:when test="${not empty savedUserInput}">
        <c:set value="${savedUserInput}" var="userToEdit" />
    </c:when>
    <c:otherwise>
        <c:set value="${proceedUser}" var="userToEdit" />
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${not empty user and user.role eq 'ADMIN'}">
        <l:redirectIfEmpty value="${appRoles}" errorMsg="Application roles were not initialized at startup" />
        <c:choose>
            <c:when test="${not empty userSearchLink}" >
                <c:set value="${userSearchLink}" var="cancelLink"/>
            </c:when>
            <c:otherwise>
                <c:set value="/jsp/admin/users.jsp" var="cancelLink"/>
            </c:otherwise>
        </c:choose>
        <c:if test="${param.command eq 'user.add'}">
            <c:set value="header.create.user" var="currentHeader"/>
        </c:if>
        <c:if test="${param.command eq 'user.edit'}">
            <c:set value="header.edit.user" var="currentHeader"/>
        </c:if>
        <c:if test="${empty userToEdit or userToEdit.id ne user.id}">
            <c:set var="notHidden" value="true" />
        </c:if>
    </c:when>
    <c:when test="${empty user}">
        <c:set value="/jsp/login.jsp" var="cancelLink" />
        <c:set value="header.register" var="currentHeader"/>
    </c:when>
    <c:otherwise>
        <c:set value="/jsp/home.jsp" var="cancelLink" />
        <c:set value="header.edit.my.info" var="currentHeader"/>
    </c:otherwise>
</c:choose>


<fmt:setLocale value="${lang.code}" />
<fmt:setBundle basename="i18n" />

<div class="container-sm bg-light border col-sm-6 col-sm-offset-3 my-5 pt-2">
    <div class="container-sm col-sm-10 col-sm-offset-1">
        <div class="row pb-2">
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
                            value="<c:out value='${userToEdit.email}' />"
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
                            value="<c:out value='${userToEdit.name}' />"
                    >
                </div>
            </div>
            <c:if test="${empty user}" >
                <t:captcha />
            </c:if>
            <c:choose>
                <%-- if existing user edits herself --%>
                <c:when test="${empty notHidden and not empty userToEdit}" >
                    <input name="state" value="${userToEdit.state}" type="hidden">
                    <input name="role" value="${userToEdit.role}" type="hidden">
                </c:when>
                <%-- if user is not authenticated yet and tried to register --%>
                <c:when test="${empty userToEdit and empty user}" >
                    <input name="state" value="VALID" type="hidden">
                    <input name="role" value="USER" type="hidden">
                </c:when>
                <%-- if user is ADMIN and edit not himself --%>
                <c:otherwise>
                    <div class="row mb-2">
                        <label for="roles" class="col-md-3 col-form-label"><fmt:message key='header.role'/>: </label>
                        <div class="col-md-3">
                            <select name="role" id="roles" class="form-select"
                                aria-label="<fmt:message key='header.role'/>"
                            >
                                <c:forEach var="role" items="${appRoles}">
                                    <option <c:if test="${role eq fn:toLowerCase(userToEdit.role)}">selected</c:if>><c:out value="${role}" /></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-check">
                      <input class="form-check-input" type="checkbox" name="state" id="flexCheckChecked" value="valid"
                        <c:if test="${empty userToEdit or userToEdit.state eq 'VALID'}">checked</c:if>
                      >
                      <label class="form-check-label" for="flexCheckChecked" >
                        <fmt:message key='header.enable'/>
                      </label>
                    </div>
                </c:otherwise>
            </c:choose>
            <div class="row mb-2 my-2">
                <div class="col-sm container overflow-hidden">
                    <t:error />
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

<c:remove var="savedUserInput" scope="session" />
<jsp:include page="/WEB-INF/jspf/footer.jsp"/>

