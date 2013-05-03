package ru.cardio.web.filters;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import ru.cardio.core.managers.TokenManagerLocal;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class TokenFilter implements Filter {

    private static final String TOKEN_PARAM_NAME = "token";

    @EJB
    private TokenManagerLocal tokenMan;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(true);
        
        String tokenString = req.getParameter(TOKEN_PARAM_NAME);
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
