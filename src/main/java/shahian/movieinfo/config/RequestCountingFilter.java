package shahian.movieinfo.config;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class RequestCountingFilter implements Filter {

	private final AtomicLong requestCount = new AtomicLong(0);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (httpRequest.getRequestURI().startsWith("/api/") && !httpRequest.getRequestURI().startsWith("/api/v1/stats")) {
			requestCount.incrementAndGet();
		}
		chain.doFilter(request, response);
	}

	public long getRequestCount() {
		return requestCount.get();
	}
}