<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<h5>멤버 정보</h5>
<table class="table table-sm">
  <thead>
    <tr>
      <th scope="col">아이디</th>
      <th scope="col">이름</th>
      <th scope="col">패스워드</th>
    </tr>
  </thead>
  <tbody>
		<tr>
	      <td>${member.mid}</td>	<!-- .mid는 필드에 접근하는 게 아니라 getter를 호출해서 가져옴 -->
	      <td>${member.mname}</td>
	      <td>${member.mpassword}</td>
	    </tr>
  </tbody>
</table>