package com.ssafy.mvc.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ssafy.mvc.dto.Attraction;
import com.ssafy.mvc.util.DBUtil;

public class AttractionDao {
	private DBUtil dbUtil;
	private static AttractionDao attractionDao = new AttractionDao();

	private AttractionDao() {
		dbUtil = DBUtil.getInstance();
	}

	public static AttractionDao getInstance() {
		return attractionDao;
	}

	public List<Attraction> getAttractionList(String contentTypeId, String sidoCode, String gugunCode, String title)
			throws SQLException {
		//tour?param=map&contentTypeId=12&sidoCode=1&gugunCode=1&title=서울
		String sql = "select content_id, content_type_id, title, addr1, addr2, zipcode, tel, first_image, first_image2, readcount, sido_code, gugun_code, latitude, longitude from attraction_info";

		List<String> addSql = new ArrayList<String>();
		if (!contentTypeId.equals("")) {
			addSql.add(String.format("content_type_id=%s", contentTypeId));
		}
		if (!sidoCode.equals("")) {
			addSql.add(String.format("sido_code=%s", sidoCode));
		}
		if (!gugunCode.equals("")) {
			addSql.add(String.format("gugun_code=%s", gugunCode));
		}
		if (!title.equals("")) {
			addSql.add(String.format("title like '%%%s%%'", title));
		}
		if (addSql.size() > 0) {
			sql += " where " + addSql.stream().collect(Collectors.joining(" and "));
		}
		sql += ";";
		System.out.println(sql);
		List<Attraction> attractionList = new ArrayList<Attraction>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// DB와 연결
			conn = dbUtil.getConnection();
			// 쿼리 보내기
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			// 조회 결과 가공
			while (rs.next()) {
				Attraction attraction = new Attraction();
				attraction.setContentId(rs.getInt("content_id"));
				attraction.setContentTypeId(rs.getInt("content_type_id"));
				attraction.setTitle(rs.getString("title"));
				attraction.setAddr1(rs.getString("addr1"));
				attraction.setAddr2(rs.getString("addr2"));
				attraction.setZipcode(rs.getString("zipcode"));
				attraction.setTel(rs.getString("tel"));
				attraction.setFirstImage(rs.getString("first_image"));
				attraction.setFirstImage2(rs.getString("first_image2"));
				attraction.setReadcount(rs.getInt("readcount"));
				attraction.setSidoCode(rs.getInt("sido_code"));
				attraction.setGugunCode(rs.getInt("gugun_code"));
				attraction.setLatitude(rs.getDouble("latitude"));
				attraction.setLongitude(rs.getDouble("longitude"));
				attractionList.add(attraction);
				System.out.println(attraction);
			}

		} finally {
			// 자원 반납
			dbUtil.close(rs, pstmt, conn);
		}

		return attractionList;
	}

	public List<Attraction> getAttractionList(List<String> mapAreaInfo) throws SQLException {
		String sql = "select content_id, content_type_id, title, addr1, addr2, zipcode, tel, first_image, first_image2, readcount, sido_code, gugun_code, latitude, longitude" +
				" from attraction_info where latitude between ? and ? and longitude between ? and ?;";

		List<Attraction> attractionList = new ArrayList<Attraction>();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// DB와 연결
			conn = dbUtil.getConnection();
			// 쿼리 보내기
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mapAreaInfo.get(0));
			pstmt.setString(2, mapAreaInfo.get(2));
			pstmt.setString(3, mapAreaInfo.get(1));
			pstmt.setString(4, mapAreaInfo.get(3));
			rs = pstmt.executeQuery();
			// 조회 결과 가공
			while (rs.next()) {
				Attraction attraction = new Attraction();
				attraction.setContentId(rs.getInt("content_id"));
				attraction.setContentTypeId(rs.getInt("content_type_id"));
				attraction.setTitle(rs.getString("title"));
				attraction.setAddr1(rs.getString("addr1"));
				attraction.setAddr2(rs.getString("addr2"));
				attraction.setZipcode(rs.getString("zipcode"));
				attraction.setTel(rs.getString("tel"));
				attraction.setFirstImage(rs.getString("first_image"));
				attraction.setFirstImage2(rs.getString("first_image2"));
				attraction.setReadcount(rs.getInt("readcount"));
				attraction.setSidoCode(rs.getInt("sido_code"));
				attraction.setGugunCode(rs.getInt("gugun_code"));
				attraction.setLatitude(rs.getDouble("latitude"));
				attraction.setLongitude(rs.getDouble("longitude"));
				attractionList.add(attraction);
				System.out.println(attraction);
			}

		} finally {
			// 자원 반납
			dbUtil.close(rs, pstmt, conn);
		}

		return attractionList;
	}
}
