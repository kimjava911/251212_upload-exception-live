<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>--%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav>
    <h1>${param.pageName}</h1>
    <ul>
        <%-- <li><a href="${requestScope.contextPath}/">홈</a></li> --%>
        <%-- thymleaf @{} --%>
        <li><a href="<c:url value='/'/>">홈</a></li>
        <li><a href="<c:url value='/reviews'/>">리뷰</a></li>
    </ul>
    <hr>
</nav>