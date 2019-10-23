<%@ page contentType = "text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Insert title here</title>
	
		<style type="text/css">

			html, body { 
				height:100%; 
				font-family: 'Roboto', sans-serif;
			}
		
			#wrapper {
				width:100%;
				height:100%;
				display:flex;
				flex-direction:column;
			} 
			
			#header {
				border-bottom:1px solid black;
				margin-bottom:10px;
				text-align: center;
			}
		
			#content {
				flex-grow:1;
				display:flex;
				min-height:0;
			}

			#content #sideBar {
				width:400px;
				background-color:#dddddd;
				padding-right:10px;
				border-right:1px solid gray;
				overflow-y:scroll;
			}

			#content #center {
				flex-grow:1 ;
				padding:10px;
			}
			
			#center iframe {
				margin-top:0px;
				width:100%;
				height:100%;
			}
			
			#footer {
				border-top:1px solid black;
				margin-top:10px;
				margin-bottom:10px;
				text-align: center;
				height: 50px;
				line-height: 50px
			}
		</style>
		<link href="https://fonts.googleapis.com/css?family=Roboto:100,300,400,700&display=swap" rel="stylesheet">
	</head>
	<body>
		<div id="wrapper">
			<div id="header">
				<h2>SpringProgramming2</h2>
			</div>
			<div id="content">
				<div id="sideBar">
					<ul>
						<li><a href="info" target="iframe">Ch01. 컨트롤러 생성</a></li>
						<li><a href="ch02/content" target="iframe">Ch02. 요청 매핑</a></li>
						<li><a href="ch03/content" target="iframe">Ch03. 요청 파라미터</a></li>
						<li><a href="ch04/content" target="iframe">Ch04. 요청 헤더값과 쿠키값 설정 및 읽기</a></li>
						<li><a href="ch05/content" target="iframe">Ch05. 컨트롤러에서 뷰로 데이터 전달</a></li>
						<li><a href="ch06/content" target="iframe">Ch06. 매개변수 타입과 리턴 타입</a></li>
						<li><a href="ch08/content" target="iframe">Ch08. 파일 업로드</a></li>
						<li><a href="ch09/content" target="iframe">Ch09. 의존성 주입(DI)</a>
						<li><a href="ch10/content" target="iframe">Ch10. 데이터베이스 연동</a>
					</ul>
					
				</div>
				<div id="center">
					<iframe name="iframe" src="http://tomcat.apache.org" frameborder="0"></iframe>
				</div>
			</div>
			<div id="footer">2019. IoT. K.E.J. &copy;<a href="https://www.github.com/Framda" target="_blank">Framda</a> </div>
		</div>
	</body>
</html>