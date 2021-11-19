<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<c:if test="${not empty user and user.role eq 'ADMIN'}">
    <l:redirectIfEmpty value="${appRoles}" errorMsg="Application roles were not initialized at startup" />
</c:if>
    <div class="container-sm bg-light border col-sm-6 col-sm-offset-3 my-5 pt-2">
        <div class="container-sm col-sm-10 col-sm-offset-1">
            <div class="row">
                    <h1>Register</h1>
            </div>
            <form action="/controller" method="post">
                <input type="hidden" name="command" value="user.register">
                <div class="row mb-1">
                    <label for="email" class="col-md-3 col-form-label">Email: </label>
                    <div class="col-md-7">
                        <input name="emailReg" type="email" id="email" class=" form-control" required><br/>
                    </div>
                </div>
                <div class="row mb-1">
                    <label for="password" class="col-md-3 col-form-label">Password: </label>
                    <div class="col-md-7">
                        <input name="passwordReg" type="password" id="password" class="col-md-6 form-control" required><br/>
                    </div>
                </div>
                <div class="row mb-2">
                    <label for="anotherPass" class="col-md-3 col-form-label">Retype password: </label>
                    <div class="col-md-7">
                        <input name="anotherPass" type="password" id="anotherPass" class="col-md-6 form-control" required><br/>
                    </div>
                </div>
                <div class="row mb-2">
                    <label for="name" class="col-md-3 col-form-label">Name: </label>
                    <div class="col-md-7">
                        <input name="name" type="text" id="name" class="form-control"><br />
                    </div>
                </div>
                <c:if test="${not empty user and user.role eq 'ADMIN'}">
                    <c:if test="${not empty appRoles}">
                        <div class="row mb-2">
                            <label for="roles" class="col-md-3 col-form-label">Role: </label>
                            <div class="col-md-3">
                                <select id="roles" class="form-select" aria-label="Default select example">
                                    <c:forEach var="role" items="${appRoles}">
                                        <option><c:out value="${role}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </c:if>
                    <div class="form-check">
                      <input class="form-check-input" type="checkbox" name="state" id="flexCheckChecked" checked>
                      <label class="form-check-label" for="flexCheckChecked" >
                        Enable
                      </label>
                    </div>
                </c:if>
                <div class="row mb-2 my-2">
                <!-- TODO check if retype is the same as password -->
                    <div class="col-sm container overflow-hidden">
                        <p class="text-danger"><c:out value="${regErrorMsg}" /></p>
                        <button type="submit" class="btn btn-primary">Register</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
<jsp:include page="/html/footer.html"/>