<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <c:set var="root" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
</head>
<body>

	<%-- 페이지만의 내용 --%>
	<div class="container p-4">
	
	  <h2>로그인</h2>
	  <form id="loginForm" action="${root}/auth" method="post">
	  	<input type="hidden" name="action" value="login">
	    <div class="form-group">
	      <label for="id">아이디</label>
	      <input type="text" class="form-control" id="id" name="id" placeholder="아이디" value="${cookie.rememberId.value}">
		  <input type="checkbox" name="isRemember" ${(empty cookie.rememberId)?"" : "checked"} /> 아이디 저장
	    </div>
	    <div class="form-group">
	      <label for="password">비밀번호</label>
	      <input type="password" class="form-control" id="password" name="password" placeholder="비밀번호">
	    </div>

	    <button type="submit" class="btn btn-primary" >로그인</button>
	    <a class="btn btn-secondary" href="${root}" >취소</a>
	  </form>
	
	
	</div>
	<%-- --%>