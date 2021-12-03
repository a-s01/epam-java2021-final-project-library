<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>


<c:set var="action" value="author.find" />
<c:set var="searchLink" value="${authorSearchLink}" />
<l:setList var="list" value="name" />

<div class="container">
    <t:searchBar searchParameters="${list}" action="${action}" />
    <div class="container pt-4">
        <c:if test="${not empty authors}">
            <table class="table table-hover">
                <thead class="bg-secondary bg-gradient text-white">
                    <th scope="col"><fmt:message key='header.default.author.name'/></th>
                    <c:forEach var="lang" items="${langs}">
                        <th scope="col"><fmt:message key='header.name.in.lang'/> <c:out value="${lang.code}"/></th>
                    </c:forEach>
                    <th scope="col"><fmt:message key='header.action'/></th>
                </thead>
                <tbody>
                    <c:forEach var="author" items="${authors}">
                        <tr class="table-light">
                            <td><c:out value="${author.name}"/></td>
                            <c:forEach var="lang" items="${langs}">
                                <td>
                                    <l:printAuthor author="${author}" lang="${lang}" fallback='false'/>
                                </td>
                            </c:forEach>
                            <td>
                                <div class="row justify-content-start">
                                    <div class="col-auto">
                                        <a class="btn btn-warning" href="/controller?command=author.edit&id=${author.id}">Edit</a>
                                    </div>
                                    <div class="col-auto">
                                        <form action="/controller" method="post">
                                            <input type="hidden" value="author.delete" name="command">
                                            <input type="hidden" value="${author.id}" name="id">
                                            <button type="submit" class="btn btn-danger"><fmt:message key='header.delete'/></button>
                                        </form>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </c:if>
        <a class="btn btn-info" href="/jsp/admin/author_edit.jsp?command=author.add"><fmt:message key="header.create.author"/></a>
        <c:if test="${not empty notFound}">
            <div class="container pt-4">
                <h5><fmt:message key='${notFound}'/></h5>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/jspf/footer.jsp"/>