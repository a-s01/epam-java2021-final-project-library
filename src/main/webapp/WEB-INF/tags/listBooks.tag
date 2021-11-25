<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="books" required="true" rtexprvalue="true" type="java.util.List"%>
<%@ attribute name="state" required="true" rtexprvalue="true"%>
<%@ attribute name="error" required="true" %>

<c:if test="${not empty books}">
    <div class="card">
        <div class="card-header">
        </div>
        <div class="card-body">
            <table class="table table-hover">
                <thead>
                    <th scope="col"><fmt:message key='header.title'/></th>
                    <th scope="col"><fmt:message key='header.authors'/></th>
                    <th scope="col"><fmt:message key='header.isbn'/></th>
                    <th scope="col"><fmt:message key='header.year'/></th>
                    <c:if test="${state eq 'DELIVERED'}">
                        <th scope="col"><fmt:message key='header.return.up.to'/></th>
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
                            <c:if test="${state eq 'DELIVERED'}" >
                                <td class="text-danger">NOT READY YET</td>
                            </c:if>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</c:if>
<c:if test="${empty books}">
    <div><c:out value="${error}" /></div>
</c:if>