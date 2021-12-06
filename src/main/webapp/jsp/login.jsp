<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>

<c:if test="${not empty successMsg}">
    <div class="container-sm pt-2 col-sm-4 col-sm-offset-4">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <fmt:message key="${successMsg}" />. <fmt:message key="message.try.to.login" />
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </div>
</c:if>
<div class="container-sm pt-2 bg-light border col-sm-4 col-sm-offset-4 my-5 ">
    <div class="row">
        <h1><fmt:message key='header.login'/></h1>
    </div>
    <form action="/controller" method="post">
        <input type="hidden" name="command" value="user.login">
        <div class="row mb-1">
            <label for="email" class="col-sm-3 col-form-label"><fmt:message key='header.email'/>:</label>
            <div class="col-md-7">
                <input name="email" type="email" class="form-control" id="email" required>
            </div>
        </div>
        <div class="row mb-1">
            <label for="password" class="col-sm-3 col-form-label"><fmt:message key='header.password'/>:</label>
            <div class="col-md-7">
                <input name="password" type="password" class="form-control" id="password" required>
            </div>
        </div>
        <c:if test="${not empty loginTriesNumber}">
            <t:captcha />
        </c:if>
        <div class="row my-2">
            <t:error />
            <div class="col-sm container overflow-hidden">
                <button type="submit" class="btn btn-primary"><fmt:message key='link.sign.in'/></button>
                <a href="/jsp/register.jsp?command=user.add" class="btn btn-secondary"><fmt:message key='link.sign.up'/></a>
            </div>
        </div>
    </form>
</div>
<c:remove var="successMsg" scope="session" />
<c:remove var="savedUserInput" scope="session" />

<jsp:include page="/WEB-INF/jspf/footer.jsp"/>