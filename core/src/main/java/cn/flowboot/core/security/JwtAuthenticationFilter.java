package cn.flowboot.core.security;



import cn.flowboot.common.croe.domain.AjaxResult;
import cn.flowboot.common.croe.domain.user.LoginUser;
import cn.flowboot.common.croe.properties.JwtProperties;
import cn.flowboot.common.utils.SecurityUtils;
import cn.flowboot.core.service.TokenService;
import cn.flowboot.core.utils.JwtUtil;

import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/09/24
 */
@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private TokenService tokenService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (tokenService.checkIgnore(request)) {
            log.debug("{} 放行",request.getServletPath());
            chain.doFilter(request, response);
            return;
        }
        LoginUser loginUser = null;
        try {
            loginUser = tokenService.getUser(request);
        } catch (JwtException jwtException){
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(JSONUtil.toJsonStr(AjaxResult.error(jwtException.getMessage())).getBytes("UTF-8"));

            outputStream.flush();
            outputStream.close();
            return;
        }

        //用户操作但当前上下文权限不存在
        if (ObjectUtils.isNotEmpty(loginUser) && ObjectUtils.isEmpty(SecurityUtils.getAuthentication())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        chain.doFilter(request, response);
    }
}
