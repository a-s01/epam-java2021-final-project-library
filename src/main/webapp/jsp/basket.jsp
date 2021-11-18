<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
    <div class="container">
        <c:if test="${not empty booking}">
            <div class="card">
                <div class="card-header">
                    <div><c:out value="${booking.created}"/></div>
                    <div><c:out value="${booking.state}"/></div>
                </div>
                <div class="card-body">
                    <c:if test="${not empty booking.books}">
                        <table class="table table-hover">
                            <thead>
                                <th scope="col">Name</th>
                                <th scope="col">Authors</th>
                                <th scope="col">ISBN</th>
                                <th scope="col">Year</th>
                                <c:if test="${booking.state == 'NEW'}">
                                    <th scope="col">Action</th>
                                </c:if>
                            </thead>
                            <tbody>
                                <c:forEach var="book" items="${booking.books}">
                                    <tr class="table-light">
                                        <td><c:out value="${book.title}"/></td>
                                        <td>
                                            <c:forEach var="author" items="${book.authors}">
                                                <c:out value="${author.name}"/>
                                            </c:forEach>
                                        </td>
                                        <td><c:out value="${book.isbn}"/></td>
                                        <td><c:out value="${book.year}"/></td>
                                        <c:if test="${booking.state == 'NEW'}">
                                            <td><a href="/booking?command=removeBook&id=${book.id}">Delete</a></td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <c:if test="${booking.state eq 'NEW'}">
                            <div class="container">
                                <div class="row">
                                    <div class="container">
                                        <form action="/booking" method="post">
                                            <input type="hidden" name="command" value="book">
                                            <button type="submit" class="btn btn-primary">Finish booking</button>
                                        </form>
                                    </div>
                                    <div class="container">
                                        <form action="/booking" method="post">
                                            <input type="hidden" name="command" value="cancel">
                                            <button type="submit" class="btn btn-danger">Cancel booking</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:if>
                </div>
            </div>
        </c:if>
        <c:if test="${empty booking}">
            <div>You don't have any booking yet</div>
        </c:if>
    </div>
<jsp:include page="/html/footer.html"/>