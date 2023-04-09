package com.ssafy.mvc.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.ssafy.mvc.dto.Attraction;
import com.ssafy.mvc.model.service.AttractionService;

//tour?param=map&contentType=12&sidoCode=1&gugunCode=1&searchKeyword=서울
@WebServlet("/tour")
public class AttractionController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AttractionService attractionService = AttractionService.getInstance();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contextPath = request.getContextPath();
        // 요청 경로 가져오기
        String param = request.getParameter("param");

        if (param.equals("map")) {
            getAttractionList(request, response);
        } else {
            response.sendRedirect(contextPath + "/error/error404.jsp");
        }
    }

    private void getAttractionList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("관광지 목록 조회 요청 수신");
        try {
            //파라미터가 없는 경우 처리
            String contentType = "";
            if (request.getParameter("contentType") != null) {
                contentType = request.getParameter("contentType");
            }
            String sidoCode = "";
            if (request.getParameter("sidoCode") != null) {
                sidoCode = request.getParameter("sidoCode");
            }
            String gugunCode = "";
            if (request.getParameter("gugunCode") != null) {
                gugunCode = request.getParameter("gugunCode");
            }
            String searchKeyword = "";
            if (request.getParameter("searchKeyword") != null) {
                searchKeyword = request.getParameter("searchKeyword");
            }
            List<Attraction> attractionList = attractionService.getAttractionList(contentType, sidoCode, gugunCode,
                    searchKeyword);
            for (Attraction attraction : attractionList) {
                System.out.println(attraction);
            }
            //관광지 조회 객체를  json으로 변환
            Gson gson = new Gson();
            String jsonAttractionList = gson.toJson(attractionList);
            System.out.println(jsonAttractionList);

            //해당 JSON문자열을 클라이언트에게 반환
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().append(jsonAttractionList);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        System.out.println("tour post 요청 수신");
        //body 파싱
        String mapArea = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        //{"centerLat":37.4979863944204,"centerLon":127.02770621297996,"swLat":37.49170890455001,"swLon":127.0130490995973,"neLat":37.50426205779809,"neLon":127.04236577927011}
        try {
            List<Attraction> attractionList = attractionService.getAttractionList(mapArea);

            Gson gson = new Gson();
            String jsonAttractionList = gson.toJson(attractionList);
            System.out.println(jsonAttractionList);

            //해당 JSON문자열을 클라이언트에게 반환
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(jsonAttractionList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        doGet(request, response);
    }
}
