<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<c:set var="action" value="user.find" />
<l:setList var="list" value="Email Name Role State" />

<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="container">
        <c:if test="${not empty users}">
            <table class="table table-hover">
                <thead>
                    <th scope="col">Email</th>
                    <th scope="col">Role</th>
                    <th scope="col">State</th>
                    <th scope="col">Name</th>
                    <th scope="col">Action</th>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr class="table-light">
                            <td><c:out value="${user.email}"/></td>
                            <td><c:out value="${user.role}"/></td>
                            <td><c:out value="${user.state}"/></td>
                            <td><c:out value="${user.name}"/></td>
                            <td><a href="/controller?command=user.edit&id=${user.id}">Edit</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </c:if>
        <div class="container">
            <c:out value="${notFound}"/>
        </div>
    </div>
</div>

<jsp:include page="/html/footer.html"/>