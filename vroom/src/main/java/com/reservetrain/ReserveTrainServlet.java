package com.reservetrain;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.member.SessionInfo;
import com.util.MyServlet;

@WebServlet("/reservetrain/*")
public class ReserveTrainServlet extends MyServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		
		if(uri.indexOf("traininsertDepList.do")!=-1) {
			insertDepList(req, resp);
		} else if (uri.indexOf("traininsertDesList.do")!=-1) {
			insertDesList(req, resp);
		} else if (uri.indexOf("trainsteptwo_ok.do")!=-1) {
			beforeMoveToStepTwo(req, resp);
		} else if (uri.indexOf("trainsteptwo.do")!=-1) {
			moveToStepTwo(req, resp);
		} else if (uri.indexOf("reloadsteptwolist.do")!=-1) {
			reploadStepTwoListHTML(req, resp);
		} else if (uri.indexOf("trainChoiceSeats.do")!=-1) {
			moveToChoiceSeats(req, resp);
		} else if (uri.indexOf("choiceSeatsList.do")!=-1) {
			choiceSeatsListHTML(req, resp);
		} else if (uri.indexOf("beforePay.do")!=-1) {
			beforePay(req, resp);
		} else if (uri.indexOf("beforePayment.do")!=-1) {
			beforePayment(req, resp);
		} else if (uri.indexOf("insertPayInfo.do")!=-1) {
			insertPayInfo(req, resp);
		} else if (uri.indexOf("completereserve.do")!=-1) {
			completereserve(req, resp);
		}
	}
	
	protected void insertDepList(HttpServletRequest req, HttpServletResponse resp) {
		ReserveTrainDAO dao = new ReserveTrainDAO();
		try {
			List<ReserveListDetailDTO> depList = dao.getDepStationList();
			
			JSONObject job = new JSONObject();
			JSONArray jarr = new JSONArray();
			for(ReserveListDetailDTO dto : depList) {
				JSONObject jo = new JSONObject();
				
				jo.put("stationCode", dto.gettStationCode());
				jo.put("stationName", dto.gettStationName());
				
				jarr.put(jo);
			}
			job.put("list", jarr);
			
			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			
			out.print(job.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void insertDesList(HttpServletRequest req, HttpServletResponse resp) {
		ReserveTrainDAO dao = new ReserveTrainDAO();
		try {
			int deptStationCode = Integer.parseInt(req.getParameter("deptStationCode"));
			List<ReserveListDetailDTO> depList = dao.getDesStationList(deptStationCode);
			
			JSONObject job = new JSONObject();
			JSONArray jarr = new JSONArray();
			for(ReserveListDetailDTO dto : depList) {
				JSONObject jo = new JSONObject();
				
				jo.put("stationCode", dto.gettStationCode());
				jo.put("stationName", dto.gettStationName());
				
				jarr.put(jo);
			}
			job.put("list", jarr);
			
			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			
			out.print(job.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void beforeMoveToStepTwo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		ReserveTrainSessionInfo reserveInfo = new ReserveTrainSessionInfo();
		
		reserveInfo.setCycle(req.getParameter("cycle"));
		reserveInfo.setAdultCount(Integer.parseInt(req.getParameter("adultCount")));
		reserveInfo.setChildCount(Integer.parseInt(req.getParameter("childCount")));
		reserveInfo.setDeptStationCode(Integer.parseInt(req.getParameter("deptStationCode")));
		reserveInfo.setDestStationCode(Integer.parseInt(req.getParameter("destStationCode")));
		reserveInfo.setDeptStationName(req.getParameter("deptStationName"));
		reserveInfo.setDestStationName(req.getParameter("destStationName"));
		reserveInfo.settBoardDate1(req.getParameter("tBoardDate1"));
		reserveInfo.settBoardDate2(req.getParameter("tBoardDate2"));
		reserveInfo.setStaDate(req.getParameter("staDate"));
		reserveInfo.setEndDate(req.getParameter("endDate"));
		reserveInfo.setGrade(req.getParameter("grade"));
		reserveInfo.setStaEnd(1);
		
		session.setAttribute("reserveTrainInfo", reserveInfo);
		session.setAttribute("reserve", "??????");
		if(info == null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
		} else {
			moveToStepTwo(req, resp);
		}
	}
	
	protected void moveToStepTwo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		ReserveTrainSessionInfo reserveInfo = (ReserveTrainSessionInfo)session.getAttribute("reserveTrainInfo");
		session.setAttribute("reservetraininfo", reserveInfo);
		ReserveTrainSessionInfo reserveInfo2 = (ReserveTrainSessionInfo)session.getAttribute("reservetraininfo");
		session.removeAttribute("reserveTrainInfo");
		
		String cp = req.getContextPath();
		String reserve = (String)session.getAttribute("reserve");
		if(!reserve.equals("??????")) {
			resp.sendRedirect(cp + "/");
			return;
		}
		
		try {
			String cycle = reserveInfo2.getCycle();
			String staDate = reserveInfo2.getStaDate();
			String endDate = reserveInfo2.getEndDate();
			String deptStationName = reserveInfo2.getDeptStationName();
			String destStationName = reserveInfo2.getDestStationName();
			req.setAttribute("cycle", cycle);
			req.setAttribute("staDate", staDate);
			req.setAttribute("endDate", endDate);
			req.setAttribute("deptStationName", deptStationName);
			req.setAttribute("destStationName", destStationName);
			forward(req, resp, "/WEB-INF/views/reservetrain/trainsteptwo.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void reploadStepTwoListHTML(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		String cp = req.getContextPath();
		ReserveTrainDAO dao = new ReserveTrainDAO();
		List<ReserveListDetailDTO> list = new ArrayList<>(); // ????????? ???
		List<String> tStaTimeList = new ArrayList<>(); // ???????????? ?????????(????????? ??????) - ?????????
		List<String> tendTimeList = new ArrayList<>(); // ???????????? ?????????(????????? ??????) - ?????????
		List<Integer> tNumIdList = new ArrayList<>(); // ???????????? ????????? - ?????????
		List<Integer> tOperCodeList = new ArrayList<>(); // ???????????? ????????? - ?????????
		
		
		ReserveTrainSessionInfo reserveInfo = (ReserveTrainSessionInfo)session.getAttribute("reservetraininfo");
		String reserve = (String)session.getAttribute("reserve");
		if(!reserve.equals("??????")) {
			resp.sendRedirect(cp + "/");
			return;
		}
		
		try {
			// ?????? / ??????, ?????????, ????????????, ????????????, ??? ????????????, ?????????, ????????????, ??????, ?????????/?????????, ??????/??????, ????????? ???(?????? / ??????), ?????? ??????
			String cycle = reserveInfo.getCycle(); // half / full
			int deptStationCode = reserveInfo.getDeptStationCode(); // ????????? ??????
			int destStationCode = reserveInfo.getDestStationCode(); // ????????? ??????
			String deptStationName = reserveInfo.getDeptStationName(); // ????????? ??????
			String destStationName = reserveInfo.getDestStationName(); // ????????? ??????
			List<Integer> tRouteDetailCode = dao.getTRouteDetailCode(deptStationCode, destStationCode);
			int deptRouteDetailCode = tRouteDetailCode.get(0); // ?????? ??????????????????
			int destRouteDetailCode = tRouteDetailCode.get(1); // ?????? ??????????????????
			String tDiscern = deptRouteDetailCode > destRouteDetailCode ? "??????" : "??????"; // ?????? / ??????
			String tDiscern2 = deptRouteDetailCode > destRouteDetailCode ? "??????" : "??????"; // ?????? / ??????
			String staDate = reserveInfo.getStaDate();
			String endDate = null;
			if(cycle.equals("full")) {
				endDate = reserveInfo.getEndDate();
			}
			int tTotalTime = dao.getTTakeTime(deptStationCode, destStationCode, tDiscern); // ??? ????????????
			
			String tTotalTimeString;
			String a = (tTotalTime/60)+"";
			String b = (tTotalTime%60)+"";
			if(Integer.parseInt(a)<10) {
				a = "0"+a;
			}
			String hORf = req.getParameter("hORf"); // ????????? null
			tTotalTimeString = a+":"+b;
			if(hORf==null||hORf.equals("1")) {
				tStaTimeList = dao.getTStaTime(deptStationCode, destStationCode, tDiscern); // ???????????? ?????????(????????? ??????) - ?????????
				tendTimeList = dao.getTEndTime(deptStationCode, destStationCode, tDiscern); // ???????????? ?????????(????????? ??????) - ?????????
				tNumIdList = dao.getTNumId(deptStationCode, destStationCode, tDiscern); // ???????????? ????????? - ?????????
				tOperCodeList = dao.getTOperCode(deptStationCode, destStationCode, tDiscern); // ???????????? ????????? - ?????????
				for(int i=0; i<tStaTimeList.size(); i++) {
					ReserveListDetailDTO dto = new ReserveListDetailDTO();
					dto.settStaTime(tStaTimeList.get(i));
					dto.setTendTime(tendTimeList.get(i));
					dto.settNumId(tNumIdList.get(i));
					dto.settOperCode(tOperCodeList.get(i));
					
					list.add(dto);
				}
			} else if(hORf.equals("2")) {
				tStaTimeList = dao.getTStaTime(destStationCode, deptStationCode, tDiscern2); // ???????????? ?????????(????????? ??????) - ?????????
				tendTimeList = dao.getTEndTime(destStationCode, deptStationCode, tDiscern2); // ???????????? ?????????(????????? ??????) - ?????????
				tNumIdList = dao.getTNumId(destStationCode, deptStationCode, tDiscern2); // ???????????? ????????? - ?????????
				tOperCodeList = dao.getTOperCode(destStationCode, deptStationCode, tDiscern2); // ???????????? ????????? - ?????????
				tDiscern = tDiscern.equals("??????") ? "??????" : "??????";
				for(int i=0; i<tStaTimeList.size(); i++ ) {
					ReserveListDetailDTO dto = new ReserveListDetailDTO();
					dto.settStaTime(tStaTimeList.get(i));
					dto.setTendTime(tendTimeList.get(i));
					dto.settNumId(tNumIdList.get(i));
					dto.settOperCode(tOperCodeList.get(i));
					
					list.add(dto);
				}
			}
			req.setAttribute("cycle", cycle);
			req.setAttribute("deptStationName", deptStationName);
			req.setAttribute("destStationName", destStationName);
			req.setAttribute("tDiscern", tDiscern);
			req.setAttribute("staDate", staDate);
			req.setAttribute("endDate", endDate);
			req.setAttribute("tTotalTimeString",tTotalTimeString);
			req.setAttribute("tStaTimeList", tStaTimeList);
			req.setAttribute("tendTimeList", tendTimeList);
			req.setAttribute("tNumIdList", tNumIdList);
			
			req.setAttribute("list", list);
			forward(req, resp, "/WEB-INF/views/reservetrain/trainsteptwoList.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendError(400);
	}
	
	protected void moveToChoiceSeats(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		ReserveTrainSessionInfo reserveInfo = (ReserveTrainSessionInfo)session.getAttribute("reservetraininfo");
		ReserveTrainDAO dao = new ReserveTrainDAO();
		String cp = req.getContextPath();
		String reserve = (String)session.getAttribute("reserve");
		if(!reserve.equals("??????")) {
			resp.sendRedirect(cp + "/");
			return;
		}
		try {
			if(reserveInfo.getCycle().equals("full")) {
				int deptStationCode = reserveInfo.getDeptStationCode();
				int destStationCode = reserveInfo.getDestStationCode();
				req.setAttribute("staDate", req.getParameter("staDate"));
				req.setAttribute("endDate", req.getParameter("endDate"));
				req.setAttribute("deptStaDateTime", req.getParameter("deptStaDateTime"));
				req.setAttribute("deptEndDateTime", req.getParameter("deptEndDateTime"));
				req.setAttribute("destStaDateTime", req.getParameter("destStaDateTime"));
				req.setAttribute("destEndDateTime", req.getParameter("destEndDateTime"));
				req.setAttribute("deptOperCode", req.getParameter("deptOperCode"));
				req.setAttribute("destOperCode", req.getParameter("destOperCode"));
				req.setAttribute("statDiscern", req.getParameter("statDiscern"));
				req.setAttribute("endtDiscern", req.getParameter("endtDiscern"));
				List<Integer> tRouteDetailCodeList = dao.getTRouteDetailCode(deptStationCode, destStationCode);
				int deptOperCode = Integer.parseInt(req.getParameter("deptOperCode"));
				int depstatDetailCode = dao.getTDetailCode(deptOperCode, tRouteDetailCodeList.get(0));
				int desstatDetailCode = dao.getTDetailCode(deptOperCode, tRouteDetailCodeList.get(1));
				req.setAttribute("depstatDetailCode", depstatDetailCode);
				req.setAttribute("desstatDetailCode", desstatDetailCode);
				List<Integer> tRouteDetailCodeList2 = dao.getTRouteDetailCode(deptStationCode, destStationCode);
				int destOperCode = Integer.parseInt(req.getParameter("destOperCode"));
				int dependtDetailCode = dao.getTDetailCode(destOperCode, tRouteDetailCodeList2.get(0));
				int desendtDetailCode = dao.getTDetailCode(destOperCode, tRouteDetailCodeList2.get(1));
				req.setAttribute("dependtDetailCode", dependtDetailCode);
				req.setAttribute("desendtDetailCode", desendtDetailCode);
				req.setAttribute("cycle", reserveInfo.getCycle());
				req.setAttribute("grade", reserveInfo.getGrade());
				req.setAttribute("count", reserveInfo.getAdultCount()+reserveInfo.getChildCount());
				
			} else if(reserveInfo.getCycle().equals("half")) {
				int deptStationCode = reserveInfo.getDeptStationCode();
				int destStationCode = reserveInfo.getDestStationCode();
				req.setAttribute("staDate", req.getParameter("staDate"));
				req.setAttribute("tStaTime", req.getParameter("tStaTime"));
				req.setAttribute("tEndTime", req.getParameter("tEndTime"));
				req.setAttribute("tOperCode", req.getParameter("tOperCode"));
				req.setAttribute("tDiscern", req.getParameter("tDiscern"));
				List<Integer> tRouteDetailCodeList = dao.getTRouteDetailCode(deptStationCode, destStationCode);
				int tOperCode = Integer.parseInt(req.getParameter("tOperCode"));
				int statDetailCode = dao.getTDetailCode(tOperCode, tRouteDetailCodeList.get(0));// ?????? ??????????????????
				int endtDetailCode = dao.getTDetailCode(tOperCode, tRouteDetailCodeList.get(1));// ?????? ??????????????????
				req.setAttribute("statDetailCode", statDetailCode);
				req.setAttribute("endtDetailCode", endtDetailCode);
				req.setAttribute("cycle", reserveInfo.getCycle());
				req.setAttribute("grade", reserveInfo.getGrade());
				req.setAttribute("count", reserveInfo.getAdultCount()+reserveInfo.getChildCount());
			}
			
			forward(req, resp, "/WEB-INF/views/reservetrain/choiceseats.jsp");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resp.sendError(400);
	}
	
	protected void choiceSeatsListHTML(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ????????? ???????????? : ????????????, ??????????????????, ??????, ????????????, ??????????????????, ??????????????????, ??????
		ReserveTrainDAO dao = new ReserveTrainDAO();
		HttpSession session = req.getSession();
		String cp = req.getContextPath();
		String reserve = (String)session.getAttribute("reserve");
		if(!reserve.equals("??????")) {
			resp.sendRedirect(cp + "/");
			return;
		}
		try {
			String hORf = req.getParameter("hORf");
			String cycle = req.getParameter("cycle");
			String grade = req.getParameter("grade");
			String staDate = req.getParameter("staDate");
			staDate = staDate.substring(0, staDate.length()-2);
			String[] sta = staDate.split("[.]");
			if(Integer.parseInt(sta[1])<10){
				sta[1] = "0"+sta[1];
			}
			if(Integer.parseInt(sta[2])<10){
				sta[2] = "0"+sta[2];
			}
			staDate = sta[0]+"-"+sta[1]+"-"+sta[2];
			int count = Integer.parseInt(req.getParameter("count"));
			
			if(cycle.equals("half")) {
				int tOperCode = Integer.parseInt(req.getParameter("tOperCode"));
				int statDetailCode = Integer.parseInt(req.getParameter("statDetailCode"));
				int endtDetailCode = Integer.parseInt(req.getParameter("endtDetailCode"));
				int tNumId = dao.getTNumId(tOperCode); // ????????????
				List<HochaDTO> tHoNumList = dao.getTHochaList(tOperCode, grade, tNumId); // ???????????? List
				List<String> hochaList = new ArrayList<>(); // ???????????? ?????????
				
				for(HochaDTO dto : tHoNumList) {
					hochaList.add(dto.gettHoNum());
				}
				
				List<HochaDTO> reservedSeatsList = dao.getReservedSeatsList(hochaList, statDetailCode, endtDetailCode, staDate); // ?????? ???????????? ????????? ?????? ?????????
				
				String tHoNum = req.getParameter("tHoNum");
				String gradetHoNum = null;
				// ??????????????? ???????????? ????????? ??????, ???????????? ?????? ???????????? ????????? ??????????????? ?????? ?????? ????????? ??????????????? ??????
				if(tHoNum==null) {
					for(int i=0; i<reservedSeatsList.size(); i++) {
						List<String> tSeatNumList = reservedSeatsList.get(i).gettSeatNumList();
						int leftSeat = tHoNumList.get(i).getHoNum()-tSeatNumList.size();
						String honum = reservedSeatsList.get(i).gettHoNum();
						if(leftSeat > count) {
							tHoNum = honum;
							gradetHoNum = dao.getGrade(honum);
							break;
						}
					}
				} else {
					gradetHoNum = dao.getGrade(tHoNum) ;
				}
				
				if(gradetHoNum==null&&grade.equals("premium")) {
					gradetHoNum = "premium";
				}
				
				List<String> reservedSeats = new ArrayList<>();
				reservedSeats = dao.getReservedSeats(tHoNum, statDetailCode, endtDetailCode, staDate); // ?????? ????????? ????????? ?????? ?????????
				String[] reservedSeatsArr = reservedSeats.toArray(new String[0]);
				
				List<HochaDTO> selectHochaList = new ArrayList<>();
				for(int i=0; i<reservedSeatsList.size(); i++) {
					
					HochaDTO dto = new HochaDTO();
					List<String> tSeatNumList = reservedSeatsList.get(i).gettSeatNumList();
					int leftSeats = tHoNumList.get(i).getHoNum()-tSeatNumList.size();
					if(leftSeats > count) {
						dto.setHoNum(tHoNumList.get(i).getHoNum()); // ?????????
						dto.settHoNum(reservedSeatsList.get(i).gettHoNum()); // ?????? ??????
						String hochaName = reservedSeatsList.get(i).gettHoNum();
						int num = Integer.parseInt(hochaName.substring(4));
						dto.setNum(num); // ??? ????????????(???????????? ???????????? ??????)
						dto.setLeftSeats(leftSeats);
						selectHochaList.add(dto);
					}
				}
				
				JSONObject job = new JSONObject();
				JSONArray jarr = new JSONArray();
				for(HochaDTO dto : selectHochaList) {
					JSONObject jo = new JSONObject();
					
					jo.put("tHoNum", dto.gettHoNum());
					jo.put("hoNum", dto.getHoNum());
					jo.put("num", dto.getNum());
					jo.put("leftSeats", dto.getLeftSeats());
					
					jarr.put(jo);
				}
				job.put("list", jarr);
				job.put("grade", grade);
				job.put("reservedSeatsArr", reservedSeatsArr);
				job.put("gradetHoNum", gradetHoNum);
				
				resp.setContentType("text/html;charset=utf-8");
				PrintWriter out = resp.getWriter();
				
				out.print(job.toString());
				
			} else if(cycle.equals("full")) {
				if(hORf.equals("1")) {
					int deptOperCode = Integer.parseInt(req.getParameter("deptOperCode"));
					int depstatDetailCode = Integer.parseInt(req.getParameter("depstatDetailCode"));
					int desstatDetailCode = Integer.parseInt(req.getParameter("desstatDetailCode"));
					int tNumId = dao.getTNumId(deptOperCode);
					List<HochaDTO> tHoNumList = dao.getTHochaList(deptOperCode, grade, tNumId);
					List<String> hochaList = new ArrayList<>();
					
					for(HochaDTO dto : tHoNumList) {
						hochaList.add(dto.gettHoNum());
					}
					
					List<HochaDTO> reservedSeatsList = dao.getReservedSeatsList(hochaList, depstatDetailCode, desstatDetailCode, staDate);
					
					String tHoNum = req.getParameter("tHoNum");
					String gradetHoNum = null;
					// ??????????????? ???????????? ????????? ??????, ???????????? ?????? ???????????? ????????? ??????????????? ?????? ?????? ????????? ??????????????? ??????
					if(tHoNum==null) {
						for(int i=0; i<reservedSeatsList.size(); i++) {
							List<String> tSeatNumList = reservedSeatsList.get(i).gettSeatNumList();
							int leftSeat = tHoNumList.get(i).getHoNum()-tSeatNumList.size();
							String honum = reservedSeatsList.get(i).gettHoNum();
							if(leftSeat > count) {
								tHoNum = honum;
								gradetHoNum = dao.getGrade(honum);
								break;
							}
						}
					} else {
						gradetHoNum = dao.getGrade(tHoNum) ;
					}
					
					if(gradetHoNum==null&&grade.equals("premium")) {
						gradetHoNum = "premium";
					}
					
					List<String> reservedSeats = new ArrayList<>();
					reservedSeats = dao.getReservedSeats(tHoNum, depstatDetailCode, desstatDetailCode, staDate); // ?????? ????????? ????????? ?????? ?????????
					String[] reservedSeatsArr = reservedSeats.toArray(new String[0]);
					
					List<HochaDTO> selectHochaList = new ArrayList<>();
					for(int i=0; i<reservedSeatsList.size(); i++) {
						HochaDTO dto = new HochaDTO();
						List<String> tSeatNumList = reservedSeatsList.get(i).gettSeatNumList();
						int leftSeats = tHoNumList.get(i).getHoNum()-tSeatNumList.size();
						if(leftSeats > count) {
							dto.setHoNum(tHoNumList.get(i).getHoNum()); // ?????????
							dto.settHoNum(reservedSeatsList.get(i).gettHoNum()); // ?????? ??????
							String hochaName = reservedSeatsList.get(i).gettHoNum();
							int num = Integer.parseInt(hochaName.substring(4));
							dto.setNum(num); // ??? ????????????(???????????? ???????????? ??????)
							dto.setLeftSeats(leftSeats);
							selectHochaList.add(dto);
						}
					}
					
					JSONObject job = new JSONObject();
					JSONArray jarr = new JSONArray();
					for(HochaDTO dto : selectHochaList) {
						JSONObject jo = new JSONObject();
						
						jo.put("tHoNum", dto.gettHoNum());
						jo.put("hoNum", dto.getHoNum());
						jo.put("num", dto.getNum());
						jo.put("leftSeats", dto.getLeftSeats());
						
						jarr.put(jo);
					}
					job.put("list", jarr);
					job.put("grade", grade);
					job.put("reservedSeatsArr", reservedSeatsArr);
					job.put("gradetHoNum", gradetHoNum);
					
					resp.setContentType("text/html;charset=utf-8");
					PrintWriter out = resp.getWriter();
					
					out.print(job.toString());
					
				} else if(hORf.equals("2")) {
					String endDate = req.getParameter("endDate");
					endDate = endDate.substring(0, endDate.length()-2);
					String[] end = endDate.split("[.]");
					if(Integer.parseInt(end[1])<10){
						end[1] = "0"+end[1];
					}
					if(Integer.parseInt(end[2])<10){
						end[2] = "0"+end[2];
					}
					endDate = end[0]+"-"+end[1]+"-"+end[2];
					
					int destOperCode = Integer.parseInt(req.getParameter("destOperCode"));
					int dependtDetailCode = Integer.parseInt(req.getParameter("dependtDetailCode"));
					int desendtDetailCode = Integer.parseInt(req.getParameter("desendtDetailCode"));
					int tNumId = dao.getTNumId(destOperCode);
					List<HochaDTO> tHoNumList = dao.getTHochaList(destOperCode, grade, tNumId);
					List<String> hochaList = new ArrayList<>();
					
					for(HochaDTO dto : tHoNumList) {
						hochaList.add(dto.gettHoNum());
					}
					
					List<HochaDTO> reservedSeatsList = dao.getReservedSeatsList(hochaList, dependtDetailCode, desendtDetailCode, endDate);
					
					String tHoNum = req.getParameter("tHoNum");
					String gradetHoNum = null;
					// ??????????????? ???????????? ????????? ??????, ???????????? ?????? ???????????? ????????? ??????????????? ?????? ?????? ????????? ??????????????? ??????
					if(tHoNum==null) {
						for(int i=0; i<reservedSeatsList.size(); i++) {
							List<String> tSeatNumList = reservedSeatsList.get(i).gettSeatNumList();
							int leftSeat = tHoNumList.get(i).getHoNum()-tSeatNumList.size();
							String honum = reservedSeatsList.get(i).gettHoNum();
							if(leftSeat > count) {
								tHoNum = honum;
								gradetHoNum = dao.getGrade(honum);
								break;
							}
						}
					} else {
						gradetHoNum = dao.getGrade(tHoNum) ;
					}
					
					if(gradetHoNum==null&&grade.equals("premium")) {
						gradetHoNum = "premium";
					}
					
					List<String> reservedSeats = new ArrayList<>();
					reservedSeats = dao.getReservedSeats(tHoNum, dependtDetailCode, desendtDetailCode, endDate);
					String[] reservedSeatsArr = reservedSeats.toArray(new String[0]);
					
					List<HochaDTO> selectHochaList = new ArrayList<>();
					for(int i=0; i<reservedSeatsList.size(); i++) {
						HochaDTO dto = new HochaDTO();
						List<String> tSeatNumList = reservedSeatsList.get(i).gettSeatNumList();
						int leftSeats = tHoNumList.get(i).getHoNum()-tSeatNumList.size();
						if(leftSeats > count) {
							dto.setHoNum(tHoNumList.get(i).getHoNum()); // ?????????
							dto.settHoNum(reservedSeatsList.get(i).gettHoNum()); // ?????? ??????
							String hochaName = reservedSeatsList.get(i).gettHoNum();
							int num = Integer.parseInt(hochaName.substring(4));
							dto.setNum(num); // ??? ????????????(???????????? ???????????? ??????)
							dto.setLeftSeats(leftSeats);
							selectHochaList.add(dto);
						}
					}
					
					JSONObject job = new JSONObject();
					JSONArray jarr = new JSONArray();
					for(HochaDTO dto : selectHochaList) {
						JSONObject jo = new JSONObject();
						
						jo.put("tHoNum", dto.gettHoNum());
						jo.put("hoNum", dto.getHoNum());
						jo.put("num", dto.getNum());
						jo.put("leftSeats", dto.getLeftSeats());
						
						jarr.put(jo);
					}
					job.put("list", jarr);
					job.put("grade", grade);
					job.put("reservedSeatsArr", reservedSeatsArr);
					job.put("gradetHoNum", gradetHoNum);
					
					resp.setContentType("text/html;charset=utf-8");
					PrintWriter out = resp.getWriter();
					
					out.print(job.toString());
				}
				
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void beforePay(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		ReserveTrainSessionInfo reserveInfo = (ReserveTrainSessionInfo)session.getAttribute("reservetraininfo");
		ReserveTrainDAO dao = new ReserveTrainDAO();
		String cp = req.getContextPath();
		String reserve = (String)session.getAttribute("reserve");
		if(!reserve.equals("??????")) {
			resp.sendRedirect(cp + "/");
			return;
		}
		try {
			String cycle = reserveInfo.getCycle();
			int adultCount = reserveInfo.getAdultCount();
			int childCount = reserveInfo.getChildCount();
			req.setAttribute("cycle", cycle);
			if(cycle.equals("half")) {
				String selTHoNum = req.getParameter("selTHoNum");
				int staTNumId = Integer.parseInt(selTHoNum.substring(0, 3));
				req.setAttribute("statDiscern", req.getParameter("tDiscern"));
				req.setAttribute("staDate", req.getParameter("staDate"));
				req.setAttribute("deptStaDateTime", req.getParameter("tStaTime"));
				req.setAttribute("deptEndDateTime", req.getParameter("tEndTime"));
				req.setAttribute("depstatDetailCode", req.getParameter("statDetailCode"));
				req.setAttribute("desstatDetailCode", req.getParameter("endtDetailCode"));
				req.setAttribute("staSeats", req.getParameter("selSeats"));
				req.setAttribute("staTHoNum", req.getParameter("selTHoNum"));
				req.setAttribute("staTNumId", staTNumId);
				req.setAttribute("staTNum", Integer.parseInt(selTHoNum.substring(4)));
				req.setAttribute("staGrade", req.getParameter("selGrade"));
				req.setAttribute("statStaionName", reserveInfo.getDeptStationName());
				req.setAttribute("endtStaionName", reserveInfo.getDestStationName());
				String selGrade = req.getParameter("selGrade");
				
				// ?????? ??????
				List<Integer> tRouteDetailCodeList = dao.getTRouteDetailCode(reserveInfo.getDeptStationCode(), reserveInfo.getDestStationCode());
				int distance = dao.getTDistance(tRouteDetailCodeList.get(0), tRouteDetailCodeList.get(1));
				List<Integer> costList = dao.getTCostList(staTNumId);
				
				int adultCost = 0;
				int childCost = 0;
				if(selGrade.equals("premium")) {
					adultCost = distance*adultCount*costList.get(1);
					childCost = distance*childCount*costList.get(1)*costList.get(2)/100;
				} else {
					adultCost = distance*adultCount*costList.get(0);
					childCost = distance*childCount*costList.get(0)*costList.get(2)/100;
				}
				int totalCost = adultCost + childCost;
				DecimalFormat formatter = new DecimalFormat("###,###");
				req.setAttribute("staadultCost", formatter.format(adultCost));
				req.setAttribute("stachildCost", formatter.format(childCost));
				req.setAttribute("statotalCost", formatter.format(totalCost));
				req.setAttribute("adultCount", adultCount);
				req.setAttribute("childCount", childCount);
				
				forward(req, resp, "/WEB-INF/views/reservetrain/trainPay.jsp");
				return;
			}else if(cycle.equals("full")) {
				String staTHoNum = req.getParameter("staTHoNum");
				int staTNumId = Integer.parseInt(staTHoNum.substring(0, 3));
				String endTHoNum = req.getParameter("endTHoNum");
				int endTNumId = Integer.parseInt(endTHoNum.substring(0, 3));
				req.setAttribute("staTNum", Integer.parseInt(staTHoNum.substring(4)));
				req.setAttribute("endTNum", Integer.parseInt(endTHoNum.substring(4)));
				req.setAttribute("statDiscern", req.getParameter("statDiscern"));
				req.setAttribute("endtDiscern", req.getParameter("endtDiscern"));
				req.setAttribute("staDate", req.getParameter("staDate"));
				req.setAttribute("endDate", req.getParameter("endDate"));
				req.setAttribute("deptStaDateTime", req.getParameter("deptStaDateTime"));
				req.setAttribute("deptEndDateTime", req.getParameter("deptEndDateTime"));
				req.setAttribute("destStaDateTime", req.getParameter("destStaDateTime"));
				req.setAttribute("destEndDateTime", req.getParameter("destEndDateTime"));
				req.setAttribute("depstatDetailCode", req.getParameter("depstatDetailCode"));
				req.setAttribute("desstatDetailCode", req.getParameter("desstatDetailCode"));
				req.setAttribute("dependtDetailCode", req.getParameter("dependtDetailCode"));
				req.setAttribute("desendtDetailCode", req.getParameter("desendtDetailCode"));
				req.setAttribute("staSeats", req.getParameter("staSeats"));
				req.setAttribute("endSeats", req.getParameter("endSeats"));
				req.setAttribute("staTHoNum", req.getParameter("staTHoNum"));
				req.setAttribute("endTHoNum", req.getParameter("endTHoNum"));
				req.setAttribute("staTNumId", staTNumId);
				req.setAttribute("endTNumId", endTNumId);
				req.setAttribute("statStaionName", reserveInfo.getDeptStationName());
				req.setAttribute("endtStaionName", reserveInfo.getDestStationName());
				req.setAttribute("staGrade", req.getParameter("staGrade"));
				req.setAttribute("endGrade", req.getParameter("endGrade"));
				String staGrade = req.getParameter("staGrade");
				String endGrade = req.getParameter("endGrade");
				
				// ?????? ??????
				List<Integer> tRouteDetailCodeList = dao.getTRouteDetailCode(reserveInfo.getDeptStationCode(), reserveInfo.getDestStationCode());
				int distance = dao.getTDistance(tRouteDetailCodeList.get(0), tRouteDetailCodeList.get(1));
				List<Integer> costList = dao.getTCostList(staTNumId);
				List<Integer> costList2 = dao.getTCostList(endTNumId);
				
				int staadultCost = 0;
				int stachildCost = 0;
				int staDisCost = 0;
				if(staGrade.equals("premium")) {
					staadultCost = distance*adultCount*costList.get(1);
					stachildCost = distance*childCount*costList.get(1)*costList.get(2)/100;
				} else {
					staadultCost = distance*adultCount*costList.get(0);
					stachildCost = distance*childCount*costList.get(0)*costList.get(2)/100;
				}
				int statotalCost = staadultCost + stachildCost;
				
				int endadultCost = 0;
				int endchildCost = 0;
				if(endGrade.equals("premium")) {
					endadultCost = distance*adultCount*costList2.get(1);
					endchildCost = distance*childCount*costList2.get(1)*costList.get(2)/100;
				} else {
					endadultCost = distance*adultCount*costList2.get(0);
					endchildCost = distance*childCount*costList2.get(0)*costList.get(2)/100;
				}
				int endtotalCost = endadultCost + endchildCost;
				
				DecimalFormat formatter = new DecimalFormat("###,###");
				req.setAttribute("staadultCost", formatter.format(staadultCost));
				req.setAttribute("stachildCost", formatter.format(stachildCost));
				req.setAttribute("endadultCost", formatter.format(endadultCost));
				req.setAttribute("endchildCost", formatter.format(endchildCost));
				req.setAttribute("statotalCost", formatter.format(statotalCost));
				req.setAttribute("endtotalCost", formatter.format(endtotalCost));
				req.setAttribute("staDisCost", formatter.format(staDisCost));
				req.setAttribute("adultCount", adultCount);
				req.setAttribute("childCount", childCount);
				
				
				forward(req, resp, "/WEB-INF/views/reservetrain/trainPay.jsp");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void beforePayment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		ReserveTrainSessionInfo reserveInfo = (ReserveTrainSessionInfo)session.getAttribute("reservetraininfo");
		try {
			String cycle = reserveInfo.getCycle();
			
			req.setAttribute("statDiscern", req.getParameter("statDiscern"));
			req.setAttribute("endtDiscern", req.getParameter("endtDiscern"));
			req.setAttribute("adultCount", req.getParameter("adultCount"));
			req.setAttribute("childCount", req.getParameter("childCount"));
			req.setAttribute("staDate", req.getParameter("staDate"));
			req.setAttribute("endDate", req.getParameter("endDate"));
			req.setAttribute("deptStaDateTime", req.getParameter("deptStaDateTime"));
			req.setAttribute("deptEndDateTime", req.getParameter("deptEndDateTime"));
			req.setAttribute("destStaDateTime", req.getParameter("destStaDateTime"));
			req.setAttribute("destEndDateTime", req.getParameter("destEndDateTime"));
			req.setAttribute("depstatDetailCode", req.getParameter("depstatDetailCode"));
			req.setAttribute("desstatDetailCode", req.getParameter("desstatDetailCode"));
			req.setAttribute("dependtDetailCode", req.getParameter("dependtDetailCode"));
			req.setAttribute("desendtDetailCode", req.getParameter("desendtDetailCode"));
			req.setAttribute("staSeats", req.getParameter("staSeats"));
			req.setAttribute("endSeats", req.getParameter("endSeats"));
			req.setAttribute("staTHoNum", req.getParameter("staTHoNum"));
			req.setAttribute("endTHoNum", req.getParameter("endTHoNum"));
			req.setAttribute("staGrade", req.getParameter("staGrade"));
			req.setAttribute("endGrade", req.getParameter("endGrade"));
			req.setAttribute("staadultCost", req.getParameter("staadultCost"));
			req.setAttribute("stachildCost", req.getParameter("stachildCost"));
			req.setAttribute("endadultCost", req.getParameter("endadultCost"));
			req.setAttribute("endchildCost", req.getParameter("endchildCost"));
			req.setAttribute("statotalCost", req.getParameter("statotalCost"));
			req.setAttribute("endtotalCost", req.getParameter("endtotalCost"));
			req.setAttribute("cycle", cycle);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		forward(req, resp, "/WEB-INF/views/reservetrain/passengerInfo.jsp");
		return;
	}
	protected void insertPayInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		ReserveTrainSessionInfo reserveInfo = (ReserveTrainSessionInfo)session.getAttribute("reservetraininfo");
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		ReserveTrainDAO dao = new ReserveTrainDAO();
		String cycle = reserveInfo.getCycle();
		PaymentDTO dto = new PaymentDTO();
		PaymentDTO dto2 = new PaymentDTO();
		String cp = req.getContextPath();
		String reserve = (String)session.getAttribute("reserve");
		if(!reserve.equals("??????")) {
			resp.sendRedirect(cp + "/");
			return;
		}
		
		try {
			int cusNum;
			// ??????????????? cusNum??? 0 ??????
			if(info==null) {
				cusNum = 0;
				dto.setName(req.getParameter("name"));
				dto.setTel(req.getParameter("tel"));
				dto.setEmail(req.getParameter("email"));
				req.setAttribute("tel", req.getParameter("tel"));
				req.setAttribute("email", req.getParameter("email"));
			} else {
				cusNum = info.getCusNum();
				req.setAttribute("tel", req.getParameter("tel"));
				req.setAttribute("email", req.getParameter("email"));
			}
			
			dto.setCusNum(cusNum);
			dto.settTotNum(Integer.parseInt(req.getParameter("adultCount"))+Integer.parseInt(req.getParameter("childCount")));
			dto.settTotPrice(Integer.parseInt(req.getParameter("statotalCost").replace(",", "")));
			dto.settPayPrice(Integer.parseInt(req.getParameter("statotalCost").replace(",", "")));
			dto.settDetailCodeSta(Integer.parseInt(req.getParameter("depstatDetailCode")));
			dto.settDetailCodeEnd(Integer.parseInt(req.getParameter("desstatDetailCode")));
			dto.settHoNum(req.getParameter("staTHoNum"));
			
			String staDate = req.getParameter("staDate");
			staDate = staDate.substring(0, staDate.length()-2);
			String[] sta = staDate.split("[.]");
			if(Integer.parseInt(sta[1])<10){
				sta[1] = "0"+sta[1];
			}
			if(Integer.parseInt(sta[2])<10){
				sta[2] = "0"+sta[2];
			}
			staDate = sta[0]+"-"+sta[1]+"-"+sta[2];
			
			dto.settBoardDate(staDate);
			String tSeat = req.getParameter("staGrade").equals("premium") ? "??????" : "??????";
			dto.settSeat(tSeat);
			List<Integer> feeList = new ArrayList<>();
			List<String> passengerList = new ArrayList<>();
			List<String> tSeatNum = new ArrayList<>();
			int count = Integer.parseInt(req.getParameter("adultCount")) + Integer.parseInt(req.getParameter("childCount"));
			int adultCount = Integer.parseInt(req.getParameter("adultCount"));
			int childCount = Integer.parseInt(req.getParameter("childCount"));
			int adultCost = Integer.parseInt(req.getParameter("staadultCost").replace(",", ""));
			int childCost = Integer.parseInt(req.getParameter("stachildCost").replace(",", ""));
			int ac;
			int cc;
			if(adultCount==0) {
				ac = 0;
			} else {
				ac = adultCost/adultCount;
			}
			if(childCount==0) {
				cc = 0;
			} else {
				cc = childCost/childCount;
			}
			
			String staSeats = req.getParameter("staSeats");
			String[] seatsArr = staSeats.split(",");
			
			for(int i=0; i<count; i++) {
				if(adultCount>0) {
					passengerList.add("??????");
					feeList.add(ac);
					tSeatNum.add(seatsArr[i]);
					adultCount--;
					continue;
				} else if(childCount>0) {
					passengerList.add("??????");
					tSeatNum.add(seatsArr[i]);
					feeList.add(cc);
					childCount--;
				}
			}
			dto.settPassenger(passengerList);
			dto.settFee(feeList);
			dto.settSeatNum(tSeatNum);
			
			// ?????? ?????????
			String tTkNumList;
			if (cycle.equals("half")) {
				tTkNumList = dao.halfInsertPayInfo(dto);
				if (tTkNumList != null) {
					String reserveNum = tTkNumList;
					req.setAttribute("reserveNum", reserveNum);
					forward(req, resp, "/WEB-INF/views/reservetrain/mailsend.jsp");
					return;
				}
			}
			if(cycle.equals("full")) {
				// ??????????????? ?????? 2?????? ??????
				dto2.settTotNum(Integer.parseInt(req.getParameter("adultCount"))+Integer.parseInt(req.getParameter("childCount")));
				dto2.settTotPrice(Integer.parseInt(req.getParameter("endtotalCost").replace(",", "")));
				dto2.settPayPrice(Integer.parseInt(req.getParameter("endtotalCost").replace(",", "")));
				dto2.settDetailCodeSta(Integer.parseInt(req.getParameter("dependtDetailCode")));
				dto2.settDetailCodeEnd(Integer.parseInt(req.getParameter("desendtDetailCode")));
				dto2.settHoNum(req.getParameter("endTHoNum"));
				
				String endDate = req.getParameter("endDate");
				endDate = endDate.substring(0, endDate.length()-2);
				String[] end = endDate.split("[.]");
				if(Integer.parseInt(end[1])<10){
					end[1] = "0"+end[1];
				}
				if(Integer.parseInt(end[2])<10){
					end[2] = "0"+end[2];
				}
				endDate = end[0]+"-"+end[1]+"-"+end[2];
				
				dto2.settBoardDate(endDate);
				String tSeat2 = req.getParameter("endGrade").equals("premium") ? "??????" : "??????";
				dto2.settSeat(tSeat2);
				List<Integer> feeList2 = new ArrayList<>();
				List<String> passengerList2 = new ArrayList<>();
				List<String> tSeatNum2 = new ArrayList<>();
				int count2 = Integer.parseInt(req.getParameter("adultCount")) + Integer.parseInt(req.getParameter("childCount"));
				int adultCount2 = Integer.parseInt(req.getParameter("adultCount"));
				int childCount2 = Integer.parseInt(req.getParameter("childCount"));
				int adultCost2 = Integer.parseInt(req.getParameter("endadultCost").replace(",", ""));
				int childCost2 = Integer.parseInt(req.getParameter("endchildCost").replace(",", ""));
				int ac2;
				int cc2;
				if(adultCount2==0) {
					ac2 = 0;
				} else {
					ac2 = adultCost2/adultCount2;
				}
				if(childCount2==0) {
					cc2 = 0;
				} else {
					cc2 = childCost2/childCount2;
				}
				
				String endSeats = req.getParameter("endSeats");
				String[] seatsArr2 = endSeats.split(",");
				
				for(int i=0; i<count2; i++) {
					if(adultCount2>0) {
						passengerList2.add("??????");
						feeList2.add(ac2);
						tSeatNum2.add(seatsArr2[i]);
						adultCount2--;
						continue;
					} else if(childCount2>0) {
						passengerList2.add("??????");
						tSeatNum2.add(seatsArr2[i]);
						feeList2.add(cc2);
						childCount2--;
					}
				}
				dto2.settPassenger(passengerList2);
				dto2.settFee(feeList2);
				dto2.settSeatNum(tSeatNum2);
				
				// ?????? ?????????
				List<String> tTkNumList2 = new ArrayList<>();
				if(cycle.equals("full")) {
					tTkNumList2 = dao.fullInsertPayInfo(dto, dto2);
					if(tTkNumList2.size() > 0) {
						String reserveNum = tTkNumList2.get(0) + ", " + tTkNumList2.get(1);
						req.setAttribute("reserveNum", reserveNum);
						forward(req, resp, "/WEB-INF/views/reservetrain/mailsend.jsp");
						return;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void completereserve(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		forward(req, resp, "/WEB-INF/views/mail/complete.jsp");
		return;
	}
}