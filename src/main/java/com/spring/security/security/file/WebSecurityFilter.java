package com.spring.security.security.file;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.spring.security.security.security.WebSecurityMetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.FilterInvocation;

/**
 * 创建用户：杨辽
 * 创建时间：2016-09-30 15:36:00
 * 描    述：自己实现的过滤用户请求类，也可以直接使用 FilterSecurityInterceptor
 * <p>
 * AbstractSecurityInterceptor有三个派生类：
 * FilterSecurityInterceptor，负责处理FilterInvocation，实现对URL资源的拦截。
 * MethodSecurityInterceptor，负责处理MethodInvocation，实现对方法调用的拦截。
 * AspectJSecurityInterceptor，负责处理JoinPoint，主要是用于对切面方法(AOP)调用的拦截。
 * <p>
 * 还可以直接使用注解对Action方法进行拦截，例如在方法上加：
 * -@PreAuthorize("hasRole('ROLE_SUPER')")
 */
public class WebSecurityFilter extends AbstractSecurityInterceptor implements Filter {


    @Autowired
    private WebSecurityMetadataSource securityMetadataSource;

    @Autowired
    private AuthenticationManager webAuthenticationManager;

    @Autowired
    private WebAccessDecisionManager accessDecisionManager;

    @PostConstruct
    public void init() {
        super.setAuthenticationManager(webAuthenticationManager);
        super.setAccessDecisionManager(accessDecisionManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);

        logger.debug("请求URL:" + fi.getRequestUrl());

        //在执行doFilter之前，进行权限的检查
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.afterInvocation(token, null);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }

    public WebSecurityMetadataSource getSecurityMetadataSource() {
        return securityMetadataSource;
    }

    public void setSecurityMetadataSource(WebSecurityMetadataSource securityMetadataSource) {
        this.securityMetadataSource = securityMetadataSource;
    }

}

