<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
    <div class="container hstack gap-2">
        <div class="container bg-light border">
            <h1>Login</h1>
            <form action="/login" method="post">
                <label for="email" class="col-md-2">Email: </label>
                <input name="email" type="email" id="email" class="col-md-6" required><br/>
                <label for="password" class="col-md-2">Password: </label>
                <input name="password" type="password" id="password"  class="col-md-6" required><br/>
                <div class="col-md-4">
                    <p class="text-danger">${errorErrorMsg}</p>
                </div>
                <input type="submit" value="Login" class="col-md-2">
            </form>
        </div>
        <div class="vr"></div>
        <div class="container bg-light border">
            <h1>Register</h1>
            <form action="/register" method="post">
                <label for="email" class="col-md-3">Email: </label>
                <input name="emailReg" type="email" id="emailReg" class="col-md-6" required><br/>
                <label for="password" class="col-md-3">Password: </label>
                <input name="passwordReg" type="password" id="passwordReg" class="col-md-6" required><br/>
                <label for="anotherPass" class="col-md-3">Retype password: </label>
                <input name="anotherPass" type="password" id="anotherPass" class="col-md-6" required><br/>
                <label for="name" class="col-md-3">Name: </label>
                <input name="name" type="text" id="name" class="col-md-6"><br />
                <div class="container"><p class="text-danger">${regErrorMsg}</p></div>
                <!-- TODO check if retype is the same as password -->
                <input type="submit" value="Register" class="col-md-2">
            </form>
        </div>
    </div>
<jsp:include page="/html/footer.html"/>