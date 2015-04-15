package taskmanager6000.client.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter( filterName  = "Utf8EncodingFilter",
			urlPatterns = {"/*"})
public class Utf8EncodingFilter implements Filter {

	 public void init(FilterConfig config) throws ServletException {}


	 public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		next.doFilter(request, response);
	 }

	 public void destroy(){}
	}