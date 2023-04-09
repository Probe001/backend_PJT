package com.ssafy.mvc.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ssafy.mvc.dto.User;
import com.ssafy.mvc.util.DBUtil;

public class UserDao {

	private UserDao() {}
	private static UserDao instance = new UserDao();
	public static UserDao getInstance() {
		return instance;
	}
	
	private DBUtil dbUtil = DBUtil.getInstance();
	
	/**
	 * 아이디와 비밀번호로
	 * 아이디, 이름, 이메일가져오기
	 * 
	 * @param loginInfo
	 * @return
	 * @throws SQLException
	 */
	public User login(User loginInfo) throws SQLException {
		System.out.println("UserDAo 로그인 메소드 호출");
		//1. 쿼리 작성
		String sql = "select id, name, email from user where id=? and pw=?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			//2. DB 연결
			conn = dbUtil.getConnection();
			
			//3. 쿼리 실행
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, loginInfo.getId());
			pstmt.setString(2, loginInfo.getPw());
			rs = pstmt.executeQuery();
			
			//4. 조회 결과 파싱
			if(rs.next()) {
				User result = new User();
				result.setId(rs.getString("id"));
				result.setName(rs.getString("name"));
				result.setEmail(rs.getString("email"));
				return result;
			}
			return null;
		} finally {
			//5. 자원 반납
			dbUtil.close(rs, pstmt, conn);
		}
	}
	
	/**
	 * 회원가입
	 * 아이디, 비밀번호, 이름, 이메일로
	 * @return 회원가입 성공여부
	 */
	public boolean join(User user) throws SQLException{
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			// 1. DB 연결
			conn = dbUtil.getConnection();
			
			// 2. 쿼리 작성
			String sql = "insert into user (id, pw, name, email) values(?, ?, ?, ?)";
		
			// 3. ?에 인자 저장
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getId());
			pstmt.setString(2, user.getPw());
			pstmt.setString(3, user.getName());
			pstmt.setString(4, user.getEmail());
			
			int result = pstmt.executeUpdate();
			System.out.println(result);
			
			if(result > 0) {
				return true;
			}else {
				return false;
			}
		}
		finally {
			// 자원 반납
			dbUtil.close(pstmt, conn);
		}
		
	}
	
}
