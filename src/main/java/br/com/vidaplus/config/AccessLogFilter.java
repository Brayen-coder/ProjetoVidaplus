package br.com.vidaplus.config;

import br.com.vidaplus.model.AuditLog;
import br.com.vidaplus.repository.AuditLogRepository;
import br.com.vidaplus.model.UserAccount;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.Instant;

@Component
public class AccessLogFilter implements Filter {

    private final AuditLogRepository logs;

    public AccessLogFilter(AuditLogRepository logs){ this.logs = logs; }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            Authentication a = SecurityContextHolder.getContext().getAuthentication();
            String user = (a != null && a.getPrincipal() instanceof UserAccount u) ? u.getUsername() : "anonymous";
            logs.save(new AuditLog(user, req.getMethod(), req.getRequestURI(), Instant.now()));
        } catch (Exception ignored){}
        chain.doFilter(request, response);
    }
}
