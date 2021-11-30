<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<c:set var="action" value="user.find" />
<l:setList var="list" value="email name role state" />

<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="container pt-4">
        <c:if test="${not empty users}">
            <table class="table table-hover">
                <thead>
                    <th scope="col"><fmt:message key='header.email'/></th>
                    <th scope="col"><fmt:message key='header.role'/></th>
                    <th scope="col"><fmt:message key='header.state'/></th>
                    <th scope="col"><fmt:message key='header.name'/></th>
                    <th scope="col"><fmt:message key='header.fine'/></th>
                    <th scope="col"><fmt:message key='header.edit'/></th>
                    <th scope="col"><fmt:message key='header.delete'/></th>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <c:if test="${user.state ne 'DELETED'}" >
                            <tr class="table-light">
                                <td><c:out value="${user.email}"/></td>
                                <td><c:out value="${user.role}"/></td>
                                <td><c:out value="${user.state}"/></td>
                                <td><c:out value="${user.name}"/></td>
                                <td><c:out value="${user.fine}"/></td>
                                <td><a class="btn btn-warning" href="/controller?command=user.edit&id=${user.id}">Edit</a></td>
                                <td>
                                    <form action="/controller" method="post">
                                        <input type="hidden" value="user.delete" name="command">
                                        <input type="hidden" value="${user.id}" name="id">
                                        <button type="submit" class="btn btn-danger"><fmt:message key='header.delete'/></button>
                                    </form>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </c:if>
        <a class="btn btn-info" href="/jsp/register.jsp?command=user.add"><fmt:message key="header.create.user"/></a>
        <c:if test="${not empty notFound}">
            <div class="container pt-4">
                <h5><fmt:message key='${notFound}'/></h5>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/html/footer.html"/>