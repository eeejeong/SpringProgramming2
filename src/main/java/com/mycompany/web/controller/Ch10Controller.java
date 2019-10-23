package com.mycompany.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.web.dto.Ch10Board;
import com.mycompany.web.dto.Ch10Member;
import com.mycompany.web.service.Ch10Service;
import com.mycompany.web.service.LoginResult;

@Controller
@RequestMapping("/ch10")
public class Ch10Controller {
	private static final Logger logger = LoggerFactory.getLogger(Ch10Controller.class);

	// @Autowired
	@Resource(name = "dataSource")
	private DataSource dataSource;	// DataSource는 인터페이스, dataSource는 이걸 구현한 객체. 

	@RequestMapping("/content")
	public String content() {
		return "/ch10/content";
	}

	@RequestMapping("/connTest")
	public void connTest(HttpServletResponse response) {
		boolean result = false;
		
		try {
			// Connection pool에서 연결된 커넥션 대여
			Connection conn = dataSource.getConnection();
			// 값을 얻었으면 연결이 된 것. 연결이 안 되어 있으면 예외가 발생.
			if(conn != null) result = true;
			
			// Connection pool로 Connection 반납
			conn.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		try {
			response.setContentType("application/json; charset=UTF-8");
			PrintWriter pw = response.getWriter();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("result", result);
			pw.print(jsonObject.toString());
			pw.flush();
			pw.close();
		} catch(IOException e) { }
	}
	
	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;	// MyBatis의 기능을 쓰기 위해서 가져옴. 이 template 자체는 서블릿 설정 파일에서 bean으로 생성해줌.
	
	@Autowired
	private Ch10Service service;
	
	@RequestMapping("/getMember")
	public String getMember(String mid, Model model) {
		// PK로 하나의 행만 가져올 때는 selectOne
		Ch10Member member = sqlSessionTemplate.selectOne("member.selectMemberByMid", mid) ; // member.xml의 namespace
		model.addAttribute("member", member);
		return "ch10/getMember";
	}
	
	@RequestMapping("/boardList")
	public String boardList(Model model, @RequestParam(defaultValue = "1") int pageNo, HttpSession session) {
		session.setAttribute("pageNo", pageNo);
		// 페이지 당 행 수
		int rowsPerPage = 10;	
		// 버튼 그룹 개수(이전, 다음을 클릭했을 때 나오는 페이지 수)
		int pagesPerGroup = 5;	
		// 전체 게시물 수
		int totalRowNum = service.getTotalRowNo();
		// 전체 페이지 수
		int totalPageNum = totalRowNum / rowsPerPage;	
		if(totalRowNum % rowsPerPage != 0) totalPageNum++;
		// 전체 그룹 수
		int totalGroupNum = totalPageNum / pagesPerGroup;
		if(totalPageNum % pagesPerGroup != 0) totalGroupNum++;
		
		// 현재 페이지의 그룹 번호
		int groupNo = (pageNo - 1) / pagesPerGroup + 1;
		// 현재 그룹의 시작 페이지 번호
		int startPageNo = (groupNo - 1) * pagesPerGroup + 1;
		// 현재 그룹의 마지막 페이지 번호
		int endPageNo = startPageNo + pagesPerGroup - 1;
		if(groupNo == totalGroupNum) endPageNo = totalPageNum;	// 마지막 그룹은 5개 꽉 안 채워져 있을 수 있으니
		
		
		// 현재 페이지의 시작 행 번호
		int startRowNo = (pageNo - 1) * rowsPerPage + 1;
		// 현재 페이지의 끝 행 번호
		int endRowNo = pageNo * rowsPerPage;
		if(pageNo == totalPageNum) endRowNo = totalRowNum;
		
		// 현재 페이지의 게시물 가져오기
		List<Ch10Board> boardList = service.getBoardList(startRowNo, endRowNo);
		// JSP로 페이지 정보 넘기기
		model.addAttribute("pagesPerGroup", pagesPerGroup);
		model.addAttribute("totalPageNum", totalPageNum);
		model.addAttribute("totalGroupNum", totalGroupNum);
		model.addAttribute("groupNo", groupNo);
		model.addAttribute("startPageNo", startPageNo);
		model.addAttribute("endPageNo", endPageNo);
		model.addAttribute("pageNo", pageNo);
		
		model.addAttribute("boardList", boardList);		// 현재 페이지 내용
		
		return "ch10/boardList";
	}
	
	@RequestMapping("/writeBoardForm")
	public String writeBoardForm(HttpSession session) {
		String mid = (String) session.getAttribute("mid");		// 로그인이 되어 있는지 확인.
		if(mid == null) {
			return "redirect:/ch10/loginForm";
		}
		return "ch10/writeBoardForm";
	}
	
	@RequestMapping("/writeBoard")
	public String writeBoard(Ch10Board board, HttpSession session) {
		logger.debug("dao 실행 전: " + String.valueOf(board.getBno()));
		board.setBwriter((String) session.getAttribute("mid"));
		service.writeBoard(board);
		logger.debug("dao 실행 후: " + String.valueOf(board.getBno()));
		return "redirect:/ch10/boardList";	// server가 지시, 너는 다시 boardList를 요청을 하거라~
	}
	
	@RequestMapping("/loginForm")
	public String loginForm(String error, Model model) {
		if(error != null) {
			if(error.equals("fail_mid")) {
				model.addAttribute("midError", "*아이디가 존재하지 않습니다.");
			} else if(error.equals("fail_mpassword")) {
				model.addAttribute("mpasswordError", "*패스워드가 틀립니다.");
			}
		}
		return "ch10/loginForm";
	}
	
	@PostMapping("/login")
	public String login(String mid, String mpassword, HttpSession session) {
		LoginResult result = service.login(mid, mpassword);
		if(result == LoginResult.FAIL_MID) {
			//session.setAttribute("midError", "*ID가 존재하지 않습니다.");		// model은 redirect로는 전달할 수 없음. session을 써줘야 함.
			return "redirect:/ch10/loginForm?error=fail_mid";					// 혹은 이렇게 get 방식으로 넘겨주거나
		} else if(result == LoginResult.FAIL_MPASSWORD) {
			// session.setAttribute("mpasswordError", "*PASSWORD가 존재하지 않습니다.");
			return "redirect:/ch10/loginForm?error=fail_mpassword";
		} 
		
		session.setAttribute("mid", mid);
		return "redirect:/ch10/boardList";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("mid");
		return "redirect:/ch10/boardList";
	}
	
	@GetMapping("/join")
	public String joinForm() {
		return "ch10/joinForm";
	}
	
	@PostMapping("/join")
	public String join(Ch10Member member) {
		service.join(member);
		return "redirect:/ch10/loginForm";
	}
	
	@RequestMapping("/checkMid")
	public void checkMid(String mid, HttpServletResponse response) throws Exception {
		boolean result = service.checkMid(mid);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter pw = response.getWriter();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", result);
		pw.print(jsonObject.toString());
		pw.flush();
		pw.close();
	}
	
	@RequestMapping("/boardDetail")
	public String boardDetail(int bno, Model model) {
		service.increaseHitcount(bno);
		Ch10Board board = service.getBoard(bno);
		model.addAttribute("board", board);
		return "ch10/boardDetail";
	}
	
	@GetMapping("/updateBoard")
	public String updateBoardFrom(int bno, Model model) {
		Ch10Board board = service.getBoard(bno);
		model.addAttribute("board", board);
		return "ch10/updateBoardForm";
	}
	
	@PostMapping("/updateBoard") 
	public String updateBoard(Ch10Board board, HttpSession session) {
		service.updateBoard(board);
		int pageNo = (Integer) session.getAttribute("pageNo");
		return "redirect:/ch10/boardList?pageNo=" + pageNo;
	}
	
	@RequestMapping("/deleteBoard")
	public String deleteBoard(int bno, HttpSession session) {
		service.deleteBoard(bno);
		int pageNo = (Integer) session.getAttribute("pageNo");
		return "redirect:/ch10/boardList?pageNo=" + pageNo;
	}
}
