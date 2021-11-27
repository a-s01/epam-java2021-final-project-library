<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>

<c:set var="action" value="author.find" />
<l:setList var="list" value="name" />

<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="container pt-4">
        <c:if test="${not empty authors}">
            <table class="table table-hover">
                <thead>
                    <th scope="col"><fmt:message key='header.default.author.name'/></th>
                    <th scope="col"><c:out value='header.translations'/></th>
                </thead>
                <tbody>
                    <c:forEach var="author" items="${authors}">
                        <tr class="table-light">
                            <td><c:out value="${author.name}"/></td>
                            <td>
                                <div class="row">
                                    <c:forEach var="name" items="${author.i18Names}">
                                        <div class="col">
                                            <c:out value='${name.lang.code}'/>
                                            <c:out value='${name.name}'/>
                                        </div>
                                    </c:forEach>
                                </div>
                            </td>
                            <td><a class="btn btn-warning" href="/controller?command=author.edit&id=${author.id}">Edit</a></td>
                            <td>
                                <form action="/controller" method="post">
                                    <input type="hidden" value="author.delete" name="command">
                                    <input type="hidden" value="${author.id}" name="id">
                                    <button type="submit" class="btn btn-danger"><fmt:message key='header.delete'/></button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </c:if>
        <a class="btn btn-info" href="/jsp/admin/author_add.jsp?command=author.add"><fmt:message key="header.create.author"/></a>
        <c:if test="${not empty notFound}">
            <div class="container pt-4">
                <h5><fmt:message key='${notFound}'/></h5>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/html/footer.html"/>