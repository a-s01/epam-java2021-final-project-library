<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<c:set var="action" value="book.find" />
<l:setList var="list" value="Title Author ISBN Year" />
<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="alert alert-success col-sm-6" style="display: none;" role="alert" id="addedBookAlert">
      Book successfully added!
    </div>
    <div class="container pt-4">
        <c:if test="${not empty books}">
            <table class="table table-hover">
                <thead>
                    <th scope="col">Title</th>
                    <th scope="col">Authors</th>
                    <th scope="col">ISBN</th>
                    <th scope="col">Year</th>
                    <c:if test="${not empty user and user.role eq 'USER'}">
                        <th scope="col">Action</th>
                    </c:if>
                    <c:if test="${not empty user and user.role eq 'ADMIN'}">
                        <th scope="col">Keep Period</th>
                        <th scope="col">Total amount</th>
                        <th scope="col">In stock</th>
                        <th scope="col">Reserved</th>
                        <th scope="col">Was booked</th>
                        <th scope="col">Action</th>
                    </c:if>
                </thead>
                <tbody>
                    <c:forEach var="book" items="${books}">
                        <tr class="table-light">
                            <td><c:out value="${book.title}"/></td>
                            <td>
                                <c:forEach var="author" items="${book.authors}">
                                    <c:out value="${author.name}"/>
                                </c:forEach>
                            </td>
                            <td><c:out value="${book.isbn}"/></td>
                            <td><c:out value="${book.year}"/></td>
                            <c:if test="${not empty user and user.role eq 'USER'}">
                                <td>
                                    <c:if test="${not empty booking and booking.books.contains(book)}">
                                        <c:set var="bookActionState" value="disabled" />
                                    </c:if>
                                    <c:if test="${not empty booking and not booking.books.contains(book)}">

                                    </c:if>
                                    <button onclick="addBook(${book.id})" class="btn btn-primary" id="<c:out value='${book.id}' />"
                                        <c:if test="${not empty booking and booking.books.contains(book)}">
                                            disabled
                                        </c:if>
                                    >
                                        Book
                                    </button>
                                </td>
                            </c:if>
                            <c:if test="${not empty user and user.role eq 'ADMIN'}">
                                <td scope="col"><c:out value="${book.keepPeriod}" /></td>
                                <td scope="col"><c:out value="${book.bookStat.total}" /></td>
                                <td scope="col"><c:out value="${book.bookStat.inStock}" /></td>
                                <td scope="col"><c:out value="${book.bookStat.reserved}" /></td>
                                <td scope="col"><c:out value="${book.bookStat.timesWasBooked}" /></td>
                                <td>
                                    <div class="row">
                                        <div class="col-sm">
                                            <a class="btn btn-warning" href="/jsp/book_edit.jsp?command=book.edit&id=${book.id}">Edit</a>
                                        </div>
                                        <div class="col-sm">
                                            <form action="/controller" method="post">
                                                <input type="hidden" name="command" value="book.delete">
                                                <input type="hidden" name="id" value="${book.id}">
                                                <button type="submit" class="btn btn-danger">Delete</button>
                                            </form>
                                        </div>
                                    </div>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </c:if>
        <c:if test="${not empty user and user.role eq 'ADMIN'}">
            <a class="btn btn-info" href="/jsp/book_edit.jsp?command=book.add">Create book</a>
        </c:if>
        <div class="container">
            <h5><c:out value="${notFound}"/></h5>
        </div>
    </div>
</div>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<jsp:include page="/html/footer.html"/>