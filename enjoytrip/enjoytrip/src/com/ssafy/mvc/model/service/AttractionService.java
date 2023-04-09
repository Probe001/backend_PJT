package com.ssafy.mvc.model.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ssafy.mvc.dto.Attraction;
import com.ssafy.mvc.model.dao.AttractionDao;

public class AttractionService {
	private AttractionDao attractionDao;
	private static AttractionService attractionService = new AttractionService();
	private AttractionService() {
		attractionDao = AttractionDao.getInstance();
	}
	public static AttractionService getInstance() {
		return attractionService;
	}
	public List<Attraction> getAttractionList(String contentTypeId, String sidoCode, String gugunCode, String title)
			throws SQLException {
		return attractionDao.getAttractionList(contentTypeId, sidoCode, gugunCode, title);
	}
	public List<Attraction> getAttractionList(String mapArea)
			throws SQLException {
		//맵 영역 String 파싱
		JsonParser jsonParser = new JsonParser();
		JsonElement elements = jsonParser.parse(mapArea);
		List<String> mapAreaInfo = new ArrayList<>();
		mapAreaInfo.add(elements.getAsJsonObject().get("swLat").getAsString());
		mapAreaInfo.add(elements.getAsJsonObject().get("swLon").getAsString());
		mapAreaInfo.add(elements.getAsJsonObject().get("neLat").getAsString());
		mapAreaInfo.add(elements.getAsJsonObject().get("neLon").getAsString());
		return attractionDao.getAttractionList(mapAreaInfo);
	}
}
