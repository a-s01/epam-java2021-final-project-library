<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${lang.code}" />
<fmt:setBundle basename="i18n" />

<c:set var="action" value="book.find" />
<l:setList var="list" value="Title Author ISBN Year" />

<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="alert alert-success col-sm-6" style="display: none;" role="alert" id="addedBookAlert">
      <fmt:message key='message.book.added'/>
    </div>
    <div class="container pt-4">
        <c:if test="${not empty books}">
            <table class="table table-hover">
                <thead class="bg-secondary bg-gradient text-white">
                    <th scope="col"><fmt:message key='header.title'/></th>
                    <th scope="col"><fmt:message key='header.authors'/></th>
                    <th scope="col"><fmt:message key='isbn'/></th>
                    <th scope="col"><fmt:message key='header.year'/></th>
                    <c:if test="${not empty user and user.role eq 'USER'}">
                        <th scope="col"><fmt:message key='header.action'/></th>
                    </c:if>
                    <c:if test="${not empty user and user.role eq 'ADMIN'}">
                        <th scope="col"><fmt:message key='header.keep.period'/></th>
                        <th scope="col"><fmt:message key='header.total.amount'/></th>
                        <th scope="col"><fmt:message key='header.in.stock'/></th>
                        <th scope="col"><fmt:message key='header.reserved'/></th>
                        <th scope="col"><fmt:message key='header.was.booked'/></th>
                        <th scope="col"><fmt:message key='header.action'/></th>
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
                                            <a class="btn btn-warning" href="/jsp/book_edit.jsp?command=book.edit&id=${book.id}">
                                                <fmt:message key='header.edit'/>
                                            </a>
                                        </div>
                                        <div class="col-sm">
                                            <form action="/controller" method="post">
                                                <input type="hidden" name="command" value="book.delete">
                                                <input type="hidden" name="id" value="${book.id}">
                                                <button type="submit" class="btn btn-danger">
                                                    <fmt:message key='header.delete'/>
                                                </button>
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
            <a class="btn btn-info" href="/jsp/book_edit.jsp?command=book.add"><fmt:message key='header.create.book'/></a>
        </c:if>
        <div class="container">
            <h5><c:out value="${notFound}"/></h5>
        </div>
    </div>
</div>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<jsp:include page="/html/footer.html"/>