<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/WEB-INF/libTags.tld" prefix="l" %>
<%@ include file="/WEB-INF/jspf/normal_page_directive.jspf" %>
<c:set var="action" value="/controller?command=findUser" />
<l:setList var="list" value="Email Name Role State" />
<%@ include file="/WEB-INF/jspf/search.jspf" %>
<jsp:include page="/html/footer.html"/>