package cn.flowboot.core.config;

import cn.flowboot.common.croe.domain.AjaxResult;
import cn.flowboot.common.croe.properties.JwtProperties;
import cn.flowboot.common.utils.RedisCache;
import cn.flowboot.core.security.*;
import cn.flowboot.core.service.AuthenticationService;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import javax.servlet.ServletOutputStream;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/09/24
 */
@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final ObjectMapper objectMapper;
    @Qualifier("consumerUserDetailsService")
    private final UserDetailsService consumerUserDetailsService;
    @Qualifier("consumerUserDetailsPasswordService")
    private final UserDetailsPasswordService consumerUserDetailsPasswordService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final AuthenticationService authenticationService;
    private final LoginFailureHandler loginFailureHandler;
    private final RedisCache redisCache;


    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return jwtAuthenticationFilter;
    }

    /**
     * ??????????????????????????? // ?????????
     * @return
     */
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        //???????????????????????????,??????????????? ????????? //
        firewall.setAllowUrlEncodedDoubleSlash(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests(req-> req
                .antMatchers(jwtProperties.getIgnoreAuth()).permitAll()
                .anyRequest()
                .authenticated())
                //??????session
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ???????????????
                .exceptionHandling(exc -> exc.authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler))
                // ???????????????????????????
                .addFilterBefore(jwtAuthenticationFilter(), RestAuthenticationFilter.class)
                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(logout->logout.logoutSuccessHandler(getLogoutSuccessHandler()))
                .csrf(csrf -> csrf.disable())
                //.httpBasic(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions().disable()); //??????options??????
    }



    @Override
    public void configure(WebSecurity web) {
        //????????????????????????????????????
        web.ignoring().antMatchers(jwtProperties.getIgnoreFilter());
    }


    /**
     * ??????????????????????????????
     * @return
     * @throws Exception
     */
    private RestAuthenticationFilter restAuthenticationFilter() throws Exception {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper,redisCache);
        //?????????????????????
        filter.setAuthenticationSuccessHandler(getAuthenticationSuccessHandler());
        //?????????????????????
        filter.setAuthenticationFailureHandler(loginFailureHandler);
        //?????????????????????
        filter.setAuthenticationManager(authenticationManager());

        //filter.setFilterProcessesUrl("/login");
        return filter;
    }


    private AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return (req, resp, auth) -> {
            //??????json???????????????
            ObjectMapper objectMapper = new ObjectMapper();
            resp.setStatus(HttpStatus.OK.value());
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print(objectMapper.writeValueAsString(authenticationService.getResult((AbstractAuthenticationToken) auth))); //?????????????????????

        };
    }

    private LogoutSuccessHandler getLogoutSuccessHandler() {
        return (req, resp, auth) -> {
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(req, resp, auth);
            }

            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ServletOutputStream outputStream = resp.getOutputStream();

            resp.setHeader(jwtProperties.getHeader(), "");

            outputStream.write(JSONUtil.toJsonStr(AjaxResult.success()).getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        };
    }

    /**
     * ????????????
     * @param auth
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /**
     * ?????????????????????bean
     */
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(){
        val daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(consumerUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsPasswordService(consumerUserDetailsPasswordService);
        return daoAuthenticationProvider;
    }
}
