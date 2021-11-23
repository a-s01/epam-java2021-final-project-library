<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<c:set var="action" value="booking.find" />
<l:setList var="list" value="Email Name State" />
<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
        <c:if test="${not empty bookings}">
                <table class="table table-hover">
                    <thead>
                        <th scope="col">Created</th>
                        <th scope="col">State</th>
                        <th scope="col">User email</th>
                        <th scope="col">User name</th>
                        <th scope="col">Book count</th>
                        <th scope="col">Actions</th>
                    </thead>
                    <tbody>
                        <c:forEach var="booking" items="${bookings}">
                            <tr class="table-light">
                                <td><c:out value="${booking.created}"/></td>
                                <td><c:out value="${booking.state}"/></td>
                                <td><c:out value="${booking.user.email}"/></td>
                                <td><c:out value="${booking.user.name}" default="none"/></td>
                                <td><c:out value="${booking.books.size()}"/></td>
                                <td>
                                    <c:if test="${booking.state eq 'BOOKED'}">
                                        <form action="/controller" method="post">
                                            <input type="hidden" name="command" value="booking.deliver">
                                            <input type="hidden" name="subscription" value="true">
                                            <input type="hidden" name="bookingID" value="${booking.id}">
                                            <button class="btn btn-primary" type="submit">Subscription</a>
                                        </form>
                                        <form action="/controller" method="post">
                                            <input type="hidden" name="command" value="booking.deliver">
                                            <input type="hidden" name="subscription" value="false">
                                            <input type="hidden" name="bookingID" value="${booking.id}">
                                            <button class="btn btn-secondary" type="submit">In-house</a>
                                        </form>
                                        <form action="/controller" method="post">
                                            <input type="hidden" name="command" value="booking.cancel">
                                            <input type="hidden" name="bookingID" value="${booking.id}">
                                            <button class="btn btn-danger" type="submit">Cancel</a>
                                        </form>
                                    </c:if>
                                    <c:if test="${booking.state eq 'DELIVERED'}">
                                        <form action="/controller" method="post">
                                            <input type="hidden" name="command" value="booking.done">
                                            <input type="hidden" name="bookingID" value="${booking.id}">
                                            <button class="btn btn-primary" type="submit">Done</a>
                                        </form>
                                    </c:if>
                                </td>
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
<jsp:include page="/html/footer.html"/>