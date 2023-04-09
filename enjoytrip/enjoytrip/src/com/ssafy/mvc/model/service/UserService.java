package com.ssafy.mvc.model.service;

import java.sql.SQLException;

import com.ssafy.mvc.dto.User;
import com.ssafy.mvc.model.dao.UserDao;

public class UserService {
	private UserService() {}
	private static UserService instance = new UserService();
	public static UserService getInstance() {
		return instance;
	}
	
	private UserDao userDao = UserDao.getInstance();
	
	public User login(User loginInfo) throws SQLException {
		return userDao.login(loginInfo);
	}
	
	public boolean join(User user) throws SQLException{
		return userDao.join(user);
	}
	
	// 로그인, 회원가입 테스트!
	public static void main(String[] args) throws SQLException {
		//로그인 테스트
//		User user1 = new User("admin","admin","김관리","admin@naver.com");
//		User loginUser =  login(user1);
//		System.out.println(loginUser);
	
		// 회원가입 테스트
//		User user2 = new User("haribo","haribo","김하리보","haribo@naver.com");
//		join(user2);
	}
}
