<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<c:set var="action" value="/findBook" />
<l:setList var="list" value="Title Author ISBN Year" />
<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="container">
        <c:if test="${not empty books}">
            <table class="table table-hover">
                <thead>
                    <th scope="col">Name</th>
                    <th scope="col">Authors</th>
                    <th scope="col">ISBN</th>
                    <th scope="col">Year</th>
                    <c:if test="${not empty user and user.role eq 'USER'}">
                        <th scope="col">Action</th>
                    </c:if>
                    <c:if test="${not empty user and user.role eq 'ADMIN'}">
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
                                <td><a href="/booking?command=addBook&id=${book.id}">Book</a></td>
                            </c:if>
                            <c:if test="${not empty user and user.role eq 'ADMIN'}">
                                <td><a href="/book?command=edit&id=${book.id}">Edit</a></td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <ul class="pagination justify-content-end">
                <li class="page-item">
                  <a class="page-link" href="#" aria-label="Previous">
                    <span aria-hidden="true">&laquo;</span>
                  </a>
                </li>
                <li class="page-item"><a class="page-link" href="/findBook?page=0">1</a></li>
                <li class="page-item"><a class="page-link" href="/findBook?page=1">2</a></li>
                <li class="page-item"><a class="page-link" href="/findBook?page=2">3</a></li>
                <li class="page-item">
                  <a class="page-link" href="#" aria-label="Next">
                    <span aria-hidden="true">&raquo;</span>
                  </a>
                </li>
            </ul>
        </c:if>
        <div class="container">
            <c:out value="${bookNotFound}"/>
        </div>
    </div>
</div>
<jsp:include page="/html/footer.html"/>