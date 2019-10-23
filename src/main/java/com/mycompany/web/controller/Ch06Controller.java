package com.mycompany.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mycompany.web.dto.Ch06Board;

@Controller
@RequestMapping("/ch06")
public class Ch06Controller {

	private static final Logger logger = LoggerFactory.getLogger(Ch06Controller.class);

	@RequestMapping("/content")
	public String content() {
		return "ch06/content";
	}

	@PostMapping("/login")
	public String login(String mid, String mpassword, HttpSession session) {
		String loginResult = "";
		if (mid.contentEquals("admin")) {
			if (mpassword.equals("iot12345")) {
				loginResult = "success";
			} else {
				loginResult = "wrongMpassword";
			}
		} else {
			loginResult = "wrongMid";
		}

		session.setAttribute("loginResult", loginResult);

		return "redirect:/ch06/content"; // .jsp가 아니라 위의 /content를 다시 요청
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loginResult");
		return "redirect:/ch06/content";
	}

	@RequestMapping("/fileDownload")
	// 파일만 넘겨 받으면 되고 딱히 jsp로 응답을 생성할 필요가 없기 때문에 return 타입은 void
	public void fileDownload(String fname, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug(fname);
		// 응답 헤더에 추가
		ServletContext application = request.getServletContext();
		String mimeType = application.getMimeType(fname);
		String realPath = request.getServletContext().getRealPath("/resources/image/" + fname);
		response.addHeader("Content-Type", mimeType); // response.setHeader("Content-Type", "image/jpeg")

		String userAgent = request.getHeader("User-Agent");
		logger.debug(userAgent);
		String downloadName;

		// String 헤더에 오는 문자셋은 아스키, 파일 이름이 한글일 수 있으니 이거로 변환
		// IE11 이하 버전 브라우저에서 한글 파일을 복원하기 위해
		if (userAgent.contains("Trident/7.0") || userAgent.contains("MSIE")) {
			downloadName = URLEncoder.encode(fname, "UTF-8");
			// WebKit 기반 브라우저(Chrome, Safari, Firefox, Opera, Edge)에서 한글 파일을 복원하기 위해
		} else {
			downloadName = new String(fname.getBytes("UTF-8"), "ISO-8859-1"); //
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\""); // 다운로드 파일 제목 지정
		// response.setContentType("image/png");
		File file = new File(realPath);
		response.setHeader("Content-Length", String.valueOf(file.length()));	// 다운로드하려는 파일의 크기

		// 응답 본문에 데이터 추가
		OutputStream os = response.getOutputStream();
		InputStream is = new FileInputStream(realPath);

		byte[] buffer = new byte[1024];
		while (true) {
			int readByte = is.read(buffer);
			if (readByte == -1)
				break;
			os.write(buffer, 0, readByte);
		}
		
		os.flush();
		os.close();
		is.close();
	}
	
	@RequestMapping("/jsonDownload1")
	public String jsonDownload1(Model model) {
		Ch06Board board = new Ch06Board();
		board.setBno(100);
		board.setBtitle("공부하고 싶다.");
		board.setBcontent("까짓거 하면 되겠지! 열공!");
		board.setBwriter("감못자바");
		board.setDate(new Date());
		board.setHitcount(1);
		model.addAttribute("board", board);
		
		return "ch06/jsonDownload1";
	}
	
	@RequestMapping("/jsonDownload2")
	public void jsonDownload2(HttpServletResponse response) throws Exception {
		Ch06Board board = new Ch06Board();
		board.setBno(300);
		board.setBtitle("나는 오타쟁이");
		board.setBcontent("오타는 나의 인생, 오타 내는 것은 당연! 근데 못 찾으면 안 됨!");
		board.setBwriter("감잡자");
		board.setDate(new Date());
		board.setHitcount(1);
		
		JSONObject jsonObject = new JSONObject();	// 해당 제이슨 파일의 시작이 { }이므로 객체를 먼저 만들어줘야 함. 배열[]이면 JSONArray를 먼저 만들어야 함.
		jsonObject.put("bno", board.getBno());		// put(속성명, 속성값) 속성값에 따라 문자열은 ""을 알아서 붙여줌
		jsonObject.put("btitle", board.getBtitle());
		jsonObject.put("bwriter", board.getBwriter());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		jsonObject.put("date", sdf.format(board.getDate()));
		jsonObject.put("hitcount", board.getHitcount());
		String json = jsonObject.toString();
		
		
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter pw = response.getWriter();
		
		pw.write(json);
		pw.flush();
		pw.close();
	}
}
