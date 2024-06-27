package model2.mvcboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.JDBConnect;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.BoardPage;

public class ListController extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//DAO생성
		MVCBoardDAO dao = new MVCBoardDAO();
		
		//뷰에 전달할 매개변수 저자용 맵 생성
		Map<String, Object> map = new HashMap<String, Object>();
		String searchField = req.getParameter("searchField");
		String searchWord = req.getParameter("searchWord");
		if (searchWord != null) {
			//쿼리스트링으로 전달받은 매개변수 중 검색어가 있다면 map에 저장
			map.put("searchField", searchField);
			map.put("searchWord", searchWord);
		}
		int totalCount = dao.selectCount(map);//게시물개수
		
		/* 페이지 처리 start */
		ServletContext application = getServletContext();
		int pageSize = Integer.parseInt(application.getInitParameter("POSTS_PER_PAGE"));
		int blockPage = Integer.parseInt(application.getInitParameter("PAGES_PER_BLOCK"));
		
		//현재 페이지 확인
		int pageNum = 1;
		String pageTemp = req.getParameter("pageNum");
		if (pageTemp != null && !pageTemp.equals(""))
			pageNum = Integer.parseInt(pageTemp);//요청받은 페이지로 수정
		
		//목록에 출력할 게시물 범위 확인
		int start = (pageNum - 1) * pageSize;
		map.put("start", start);
		map.put("pageSize", pageSize);
		/* 페이지 처리 end */
		
		List<MVCBoardDTO> boardLists = dao.selectListPage(map);
		//게시물 목록 받기
		dao.close();//DB연결닫기
		
		//뷰에 전달할 매개변수 추가
		String pagingImg = BoardPage.pagingStr(totalCount, pageSize, blockPage, pageNum, "../mvcboard/list.do");
			//바로가기 영역 HTML 문자열
		map.put("pagingImg", pagingImg);
		map.put("totalCount", totalCount);
		map.put("pageSize", pageSize);
		map.put("pageNum", pageNum);
		
		//전달할 데이터를 request영역에 저장 후 List.jsp로 포워드
		req.setAttribute("boardLists", boardLists);
		req.setAttribute("map", map);
		req.getRequestDispatcher("/14MVCBoard/List.jsp").forward(req, resp);
	}
}