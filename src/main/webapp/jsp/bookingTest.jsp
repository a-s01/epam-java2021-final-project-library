<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<c:set var="action" value="booking.find" />
<l:setList var="list" value="Email Name State" />
<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
        <c:if test="${not empty bookings}">
            <div class="accordion" id="bookingAccordion">
                <c:forEach var="booking" items="${bookings}">
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="heading${booking.id}">
                            <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${booking.id}" aria-expanded="true" aria-controls="collapse${booking.id}">
                                <div class="container">
                                    <c:out value="${booking.id}"/>
                                    <c:out value="${booking.created}"/>
                                    <c:out value="${booking.state}"/>
                                    <c:out value="${booking.user.email}"/>
                                    <c:out value="${booking.user.name}" default="none"/>
                                    <c:out value="${booking.books.size()}"/>
                                </div>
                            </button>
                        </h2>
                    <div id="collapse${booking.id}" class="accordion-collapse collapse" aria-labelledby="heading${booking.id}" data-bs-parent="#bookingAccordion">
                        <div class="accordion-body">
                        <strong>This is the first item's accordion body.</strong> It is shown by default, until the collapse plugin adds the appropriate classes that we use to style each element. These classes control the overall appearance, as well as the showing and hiding via CSS transitions. You can modify any of this with custom CSS or overriding our default variables. It's also worth noting that just about any HTML can go within the <code>.accordion-body</code>, though the transition does limit overflow.
                        </div>
                    </div>
                </c:forEach>
            </div>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </c:if>
        <div class="container">
            <c:out value="${notFound}"/>
        </div>
</div>
<jsp:include page="/html/footer.html"/>