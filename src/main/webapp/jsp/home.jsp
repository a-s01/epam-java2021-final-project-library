<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<c:if test="${not empty user}">
    <div class="container">${user.email}</div>
</c:if>
<div class="container">
    <form action="/findBook">
        <div class="row">
            <input class="form-control-lg col-md-8 col-lg-8" type="text" placeholder="Search for book...">
            <div class="form-group col-md-2 col-lg-2">
                  <label for="searchBy" class="col-md-1 col-lg-1">Search By</label>
                  <select class="col-md-1 col-lg-1" id="searchBy">
                    <option>Everywhere</option>
                    <option>Title</option>
                    <option>Author</option>
                    <option>ISBN</option>
                  </select>
            </div>
            <button type="button" class="btn btn-primary col-md-1 col-lg-1">Search</button>
        </div>
    </form>
    <div class="container">

    </div>
</div>
<jsp:include page="/html/footer.html"/>