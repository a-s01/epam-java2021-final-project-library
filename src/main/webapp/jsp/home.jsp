<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<c:set var="action" value="book.find" />
<l:setList var="list" value="Title Author ISBN Year" />
<div class="container">
    <%@ include file="/WEB-INF/jspf/search.jspf" %>
    <div class="alert alert-success col-sm-6" style="display: none;" role="alert" id="addedBookAlert">
      Book successfully added!
    </div>
    <div class="container">
        <c:if test="${not empty books}">
            <table class="table table-hover">
                <thead>
                    <th scope="col">Title</th>
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
                                <td>
                                    <c:if test="${not empty booking and booking.books.contains(book)}">
                                        <c:set var="bookActionState" value="disabled" />
                                    </c:if>
                                    <c:if test="${not empty booking and not booking.books.contains(book)}">

                                    </c:if>
                                    <button onclick="addBook(${book.id})" class="btn btn-primary" id="<c:out value='${book.id}' />"
                                        <c:if test="${not empty booking and booking.books.contains(book)}">
                                            disabled
                                        </c:if>
                                    >
                                        Book
                                    </button>
                                </td>
                            </c:if>
                            <c:if test="${not empty user and user.role eq 'ADMIN'}">
                                <td><a href="/controller?command=book.edit&id=${book.id}">Edit</a></td>
                            </c:if>
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
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
    function addBook(id) {
        var id = id;
        $.ajax({
            url     : '/controller',
            method  : 'POST',
            data    : {id : id, command : 'booking.addBook'},
            success : function(resultText) {
                        $('#' + id).prop("disabled", true);
                        if ( !$( "#bookedBooksNum" ).length ) {
                            $("#bookedBooksNumParent").html('My booking<span class="position-absolute top-0 start-99 translate-middle badge rounded-pill bg-success" id="bookedBooksNum"></span>');
                        }
                        $( "#bookedBooksNum" ).text(resultText);
                        $('#addedBookAlert').show("slow", "swing", function(){ $('#addedBookAlert').delay(700).hide("slow") } );
                      },
            error   : function(jqXHR, exception) {
                        console.log('Error occured!!!');
                      },
        });
    }
</script>
<jsp:include page="/html/footer.html"/>