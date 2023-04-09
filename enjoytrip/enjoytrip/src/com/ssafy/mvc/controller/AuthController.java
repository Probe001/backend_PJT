package com.ssafy.mvc.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ssafy.mvc.dto.User;
import com.ssafy.mvc.model.service.UserService;

/**
 * 1. 로그인(실제 로그인은 post, 로그인페이지 이동은 get) root/auth?param=login &id=값&pw=값
 * 
 * 2. 로그아웃 root/auth?param=logout
 * 
 * 3. 회원가입(실제 회원가입은 post, 회원가입 페이지 이동은 get) root/auth?param=join
 * 
 */

@WebServlet("/auth")
public class AuthController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private UserService userService = UserService.getInstance();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String param = request.getParameter("param");
		String contextPath = request.getContextPath();

		try {
			if (param.equals("login")) {
				// 로그인 페이지 이동
				response.sendRedirect(contextPath + "/user/login.jsp");
			} else if (param.equals("logout")) {
				// 로그아웃
				logout(request, response);
			} else if (param.equals("join")) {
				// 회원가입 페이지 이동
				response.sendRedirect(contextPath + "/user/join.jsp");
			}
			else {
				System.out.println("존재하지 않는 url입니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(contextPath + "/error/error.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");

		String param = request.getParameter("param");
		
		try {
			if (param.equals("login")) {
				// 실제 로그인 시도
				login(request, response);
			} else if (param.equals("join")) {
				// 실제 회원가입 시도
				join(request, response);
			} else {
				doGet(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 로그아웃
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("로그아웃 요청 수신");

		// 1. 세션에서 정보 삭제
		HttpSession session = request.getSession();
		// 세션 아예 끊어버리기
		session.invalidate();

		// 2. 메인 페이지 반환
		response.sendRedirect(request.getContextPath());
	}

	/**
	 * 회원가입
	 * 
	 * @param request
	 * @param response
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private void join(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		System.out.println("회원가입 요청 수신");

		// 1. 아이디, 비밀번호, 이름, 이메일 정보 추출 & User 객체에 저장
		User user = new User();
		user.setId(request.getParameter("id"));
		user.setPw(request.getParameter("pw"));
		user.setName(request.getParameter("name"));
		user.setEmail(request.getParameter("email"));
		
		// 2. DB로 사용자 저장
		boolean result = userService.join(user);
		
		if(result) {
			// 회원가입 성공
			// 로그인 페이지로 이동
			response.setContentType("text/html; charset=utf-8");
			PrintWriter writer = response.getWriter();
			
			writer.println("<script>alert('회원가입 되었습니다.');</script>"); 
			writer.close();
			
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		
		}else {
			// 회원가입 실패
			// 다시 회원가입 페이지로
			response.setContentType("text/html; charset=utf-8");
			PrintWriter writer = response.getWriter();
			
			writer.println("<script>alert('이미 회원가입이 되어있습니다.');</script>"); 
			writer.close();
			
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		
		}
		
	}

	/**
	 * 로그인
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws SQLException
	 */
	private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		System.out.println("로그인 요청 수신");

		// 1. 아이디, 비밀번호 정보 추출 & User 객체에 저장
		User user = new User();
		user.setId(request.getParameter("id"));
		user.setPw(request.getParameter("pw"));

		// 2. DB에서 아이디 비밀번호 이용해서 사용자 조회(있는지)
		User loginUser = userService.login(user);
		System.out.println("로그인하려고 하는 user: " + loginUser);

		// 로그인 성공 시
		if (loginUser != null) {

			// 3. 사용자 정보를 저장(로그인했는지 안했는지)
			HttpSession session = request.getSession();
			session.setAttribute("userInfo", loginUser);

			// ID 저장 체크 박스가 체크된 경우
			if (request.getParameter("isRemember") != null) {
				// ID값이 저장된 쿠키를 응답에 추가 (ID 저장 기능)
				Cookie cookie = new Cookie("rememberId", loginUser.getId());
				// 쿠키의 유효기간 설정. 초 단위! 현재는 1시간
				cookie.setMaxAge(60 * 60);
				response.addCookie(cookie);
			}
			// ID 저장 체크 박스가 체크되어 있지 않은 경우
			else {
				Cookie cookie = new Cookie("rememberId", loginUser.getId());
				// 쿠키의 유효기간 0초로 설정
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}

			// 4. 로그인이 됐다면 메인페이지(index.jsp)로 이동
			response.setContentType("text/html; charset=utf-8");
			PrintWriter writer = response.getWriter();
			
			writer.println("<script>alert('로그인 되었습니다.');</script>"); 
			writer.close();
			
			response.sendRedirect(request.getContextPath());
		} else {
			// 로그인 실패
			PrintWriter writer = response.getWriter();
			
			writer.println("<script>alert('로그인 실패');</script>"); 
			writer.close();
			response.sendRedirect(request.getContextPath() + "/login.jsp");
		}

	}

}
