package jsfas.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jsfas.db.main.persistence.service.RedisService;
import jsfas.redis.domain.Token;

/**
 * @author iswill
 * @since 13/1/2021
 */
//public class TokenAuthenticationFilter extends OncePerRequestFilter {
public class TokenAuthenticationFilter extends OncePerRequestFilter {
	private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";
	
	private RedisService redisService;
	private static final Logger log = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		log.debug("TokenAuthenticationFilter");
		
		try {
			if (checkToken(request, response)) {
				String tokenStr = StringUtils.defaultString(request.getHeader(HEADER)).replace(PREFIX, "");
				log.debug("token: {}", tokenStr);
				
				Token token = (Token) redisService.findById("TOKEN:" + tokenStr);
				setUpSpringAuthentication(token);
				
				//extend expire time
				redisService.save("TOKEN:" + token.getId(), token);
				
				log.debug("setUpSpringAuthentication pass");
			} else {
				SecurityContextHolder.clearContext();
			}
			
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.debug("Auth Error: {}", e);
			SecurityContextHolder.clearContext();
			//response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			chain.doFilter(request, response);
		}
	}	

	private void setUpSpringAuthentication(Token token) {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(token, null, null);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	private boolean checkToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
			return false;
		return true;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public void setRedisService(RedisService redisService) {
		this.redisService = redisService;
	}

}
