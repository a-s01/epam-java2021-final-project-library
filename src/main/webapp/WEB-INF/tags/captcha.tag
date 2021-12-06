<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${lang.code}" />
<fmt:setBundle basename="i18n" />

<div class="row mb-1">
    <label for="name" class="col-md-3 col-form-label"><fmt:message key='header.captcha'/>: </label>
    <div class="col-md-7">
        <input name="captcha" value="" class="form-control" type='text' required>
    </div>
</div>
<div class="row mb-1 pt-1 justify-content-center">
    <div class="col-auto">
        <img src="data:image/jpg;base64,${captcha.base64Image}" width="200" height="70"/>
    </div>
</div>
<jsp:setProperty name="captcha" property="used" value="true" />