<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- JSTL --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<jsp:include page="../common/head.jsp">
    <jsp:param name="pageName" value="${pageName}"/>
</jsp:include>
<body>
<jsp:include page="../common/nav.jsp">
    <jsp:param name="pageName" value="${pageName}"/>
</jsp:include>

<%-- Flash 메시지 (등록/수정/삭제 후 한 번만 보여주는 메시지 --%>
<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>

<p>
<%--    <a href="${requestScope.contextPath}/reviews/new">새 리뷰 작성</a>--%>
    <a href="<c:url value='/reviews/new' />">새 리뷰 작성</a>
</p>

<c:if test="${empty reviews}">
    <p>등록된 리뷰가 없습니다.</p>
</c:if>

<c:forEach var="review" items="${reviews}">
    <div style="border: 1px solid #ccc; padding: 15px; margin: 10px 0;">
        <%-- 이미지가 있으면 썸네일 표시 --%>
        <c:if test="${not empty review.imageUrl}">
            <img src="${review.imageUrl}" alt="리뷰 이미지"
                 style="max-width: 150px; max-height: 150px;">
        </c:if>

        <h3>
            <a href="<c:url value='/reviews/${review.id}'/>">${review.title}</a>
        </h3>

        <%-- 평점을 별(★)로 표시 --%>
        <div class="d-flex justify-content-start">
            <div class="pe-1">평점:</div>
            <div class="d-flex justify-content-start">
                <c:forEach begin="1" end="${review.rating}"><span>★</span></c:forEach>
                <c:forEach begin="${review.rating + 1}" end="5"><span>☆</span></c:forEach>
            </div>
            <div class="ps-1">(${review.rating}/5)</div>
        </div>

        <p style="color: #666; font-size: 0.9em;">
                <%-- 작성일시 출력 --%>
                ${review.createdAt}
        </p>
    </div>
</c:forEach>
</body>
</html>