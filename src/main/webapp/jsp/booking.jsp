<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>

<c:set var="action" value="booking.find" />
<l:setList var="list" value="email name state" />

<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <c:if test="${not empty bookings}">
        <div class="container">
            <div class="accordion pt-4" id="bookingAccordion">
                <div class="accordion-item">
                    <h5 class="accordion-header" id="head">
                    <button class="accordion-button bg-secondary bg-gradient text-white">
                        <div class="container">
                            <div class="row fw-bold align-items-center">
                                <div class="col-2"><fmt:message key='header.created'/></div>
                                <div class="col-2"><fmt:message key='header.state'/></div>
                                <div class="col-2"><fmt:message key='header.email'/></div>
                                <div class="col-2"><fmt:message key='header.name'/></div>
                                <div class="col-2"><fmt:message key='header.book.count'/></div>
                            </div>
                        </div>
                    </button>
                    </h5>
                </div>
                <c:forEach var="booking" items="${bookings}">
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="heading${booking.id}">
                            <button class="accordion-button" type="button" data-bs-toggle="collapse"
                                data-bs-target="#collapse${booking.id}" aria-expanded="true"
                                aria-controls="collapse${booking.id}">
                                <div class="container">
                                    <div class="row">
                                        <div class="col-2"><l:printCalendar calendar="${booking.modified}" format="yyyy-MM-dd HH:mm:ss"/></div>
                                        <div class="col-2 text-lowercase"><c:out value="${booking.state}"/></div>
                                        <div class="col-2"><c:out value="${booking.user.email}"/></div>
                                        <div class="col-2"><c:out value="${booking.user.name}" default="none"/></div>
                                        <div class="col-2"><c:out value="${booking.books.size()}"/></div>
                                    </div>
                                </div>
                            </button>
                        </h2>
                        <div id="collapse${booking.id}" class="accordion-collapse collapse"
                            aria-labelledby="heading${booking.id}" data-bs-parent="#bookingAccordion">
                            <div class="accordion-body">
                                <t:listBooks books="${booking.books}" error="No books in this booking" state="${booking.state}" booking="${booking}"/>
                                <div class="container pt-2">
                                    <div class="row justify-content-end row-cols-auto">
                                        <c:if test="${booking.state eq 'BOOKED'}">
                                            <div class="col">
                                                <form action="/controller" method="post">
                                                    <input type="hidden" name="command" value="booking.deliver">
                                                    <input type="hidden" name="subscription" value="true">
                                                    <input type="hidden" name="bookingID" value="${booking.id}">
                                                    <button class="btn btn-primary" type="submit">
                                                        <fmt:message key='header.subscription'/>
                                                    </button>
                                                </form>
                                            </div>
                                            <div class="col">
                                                <form action="/controller" method="post">
                                                    <input type="hidden" name="command" value="booking.deliver">
                                                    <input type="hidden" name="subscription" value="false">
                                                    <input type="hidden" name="bookingID" value="${booking.id}">
                                                    <button class="btn btn-secondary" type="submit"><fmt:message key='header.in.house'/></button>
                                                </form>
                                            </div>
                                            <div class="col">
                                                <form action="/controller" method="post">
                                                    <input type="hidden" name="command" value="booking.cancel">
                                                    <input type="hidden" name="bookingID" value="${booking.id}">
                                                    <button class="btn btn-danger" type="submit"><fmt:message key='header.cancel.booking'/></button>
                                                </form>
                                            </div>
                                        </c:if>
                                        <c:if test="${booking.state eq 'DELIVERED'}">
                                            <div class="col">
                                                <form action="/controller" method="post">
                                                    <input type="hidden" name="command" value="booking.done">
                                                    <input type="hidden" name="bookingID" value="${booking.id}">
                                                    <button class="btn btn-primary" type="submit"><fmt:message key='header.done'/></a>
                                                </form>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <%@ include file="/WEB-INF/jspf/pagination.jspf" %>
        </div>
    </c:if>
    <c:if test="${not empty notFound}">
        <div class="container pt-4">
            <h5><fmt:message key='${notFound}'/></h5>
        </div>
    </c:if>
</div>
<jsp:include page="/html/footer.html"/>