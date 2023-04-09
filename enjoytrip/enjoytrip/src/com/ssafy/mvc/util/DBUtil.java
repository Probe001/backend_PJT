package com.ssafy.mvc.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	// 싱글톤 패턴
	// 생성자 막기
	private DBUtil() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 객체 생성하기
	private static DBUtil dbUtil = new DBUtil();

	// 해당 객체의 주소를 반환
	public static DBUtil getInstance() {
		return dbUtil;
	}

	public Connection getConnection() throws SQLException {
		String user = "ssafy";
		String pwd = "ssafy";
		String url = "jdbc:mysql://localhost:3306/enjoytrip";
		return DriverManager.getConnection(url, user, pwd);
	}
	
	public void close(AutoCloseable ...autoCloseables ) {
		for(AutoCloseable obj : autoCloseables) {
			try {
				obj.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
