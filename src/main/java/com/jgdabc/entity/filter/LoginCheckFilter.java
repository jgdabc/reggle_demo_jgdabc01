package com.jgdabc.entity.filter;

import com.alibaba.fastjson.JSON;
import com.jgdabc.common.BaseContext;
import com.jgdabc.common.R_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//判断用户是否登录，没有登录就退回到登录界面
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
//    路径匹配器，支持通配符
    public  static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
//        判断用户是否登录
//        定义不需要处理的路径包含请求的静态资源
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login",//移动端登录
                "/backend/plugins/axios/axios.min.map"

        };
        boolean check = check(urls, requestURI);//判断是否相同，如果和放行路径相同，就不会进行处理
//        如果不需要处理就放行
        if(check)
        {
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
//        判断登录状态，如果已经登录，则直接放行
        Object employee = request.getSession().getAttribute("employee");
        if (employee!=null)
        {
            long id = Thread.currentThread().getId();
            log.info("当前线程的id为{}",id);
            log.info("用户已经登录，用户id为{}",employee);
            BaseContext.setCurrentId((Long)employee);
            filterChain.doFilter(request,response);
            return;
        }
//        移动端登录验证
        Long  user = (Long) request.getSession().getAttribute("user");
        if (user!=null)
        {
//            long userid = Thread.currentThread().getId();
//            log.info("当前线程的id为{}",userid);
//            log.info("用户已经登录，用户id为{}",userid);
            BaseContext.setCurrentId(user);
//            request.getSession().setAttribute("user",user);
            filterChain.doFilter(request,response);
            return;
        }



        log.info("用户未登录");
//        如果未登录则返回未登录结果
        response.getWriter().write(JSON.toJSONString(R_.error("NOTLOGIN")));
        log.info("拦截到请求：{}",request.getRequestURI());
        return;

    }



//    检查本次请求是否需要放行
    public  boolean check(String[] urls,String requestURI)
    {
        for(String url:urls)
        {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match==true)
            {
                return  true;
            }

        }
        return false;
    }
}
