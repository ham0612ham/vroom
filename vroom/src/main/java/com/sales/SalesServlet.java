package com.sales;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.MyServlet;

public class SalesServlet extends MyServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		
		if(uri.indexOf("")!=-1) {
		} else if (uri.indexOf("")!=-1) {
		} else if (uri.indexOf("")!=-1) {
		} else if (uri.indexOf("")!=-1) {
		}
		
	}
	
	
}
