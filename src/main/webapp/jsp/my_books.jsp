<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
    <div class="container">
        <c:if test="${not empty booksInBooking}">
            <div class="card">
                <div class="card-header">
                </div>
                <div class="card-body">
                    <table class="table table-hover">
                        <thead>
                            <th scope="col">Name</th>
                            <th scope="col">Authors</th>
                            <th scope="col">ISBN</th>
                            <th scope="col">Year</th>
                            <th scope="col">Return up to date</th>
                        </thead>
                        <tbody>
                            <c:forEach var="book" items="${booksInBooking}">
                                <tr class="table-light">
                                    <td><c:out value="${book.title}"/></td>
                                    <td>
                                        <c:forEach var="author" items="${book.authors}">
                                            <c:out value="${author.name}"/>
                                        </c:forEach>
                                    </td>
                                    <td><c:out value="${book.isbn}"/></td>
                                    <td><c:out value="${book.year}"/></td>
                                    <td class="text-danger">NOT READY YET</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
        <c:if test="${empty booksInBooking}">
            <div>You don't have any book in subscription</div>
        </c:if>
    </div>
<jsp:include page="/html/footer.html"/>