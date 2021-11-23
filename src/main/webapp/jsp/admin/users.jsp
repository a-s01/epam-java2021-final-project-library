<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<c:set var="action" value="user.find" />
<l:setList var="list" value="Email Name Role State" />

<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="container pt-4">
        <c:if test="${not empty users}">
            <table class="table table-hover">
                <thead>
                    <th scope="col">Email</th>
                    <th scope="col">Role</th>
                    <th scope="col">State</th>
                    <th scope="col">Name</th>
                    <th scope="col">Edit</th>
                    <th scope="col">Delete</th>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.state ne 'DELETED'}" >
                            <tr class="table-light">
                                <td><c:out value="${user.email}"/></td>
                                <td><c:out value="${user.role}"/></td>
                                <td><c:out value="${user.state}"/></td>
                                <td><c:out value="${user.name}"/></td>
                                <td><a class="btn btn-warning" href="/jsp/admin/user_edit.jsp?id=${user.id}">Edit</a></td>
                                <td>
                                    <form action="/controller" method="post">
                                        <input type="hidden" value="user.delete" name="command">
                                        <input type="hidden" value="${user.id}" name="id">
                                        <button type="submit" class="btn btn-danger">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </c:if>
        <a class="btn btn-info" href="/jsp/register.jsp">Create user</a>
        <div class="container">
            <h5><c:out value="${notFound}"/></h5>
        </div>
    </div>
</div>

<jsp:include page="/html/footer.html"/>