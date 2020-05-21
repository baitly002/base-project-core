package org.basecode.common.config.web;

import org.basecode.common.criterion.exception.BusinessException;
import org.basecode.common.criterion.model.Token;
import org.basecode.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


@Component
public class ApiRequestInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {

        // 检查用户传递的 token是否合法
        Token tokenInfo = this.getUserToKen(request);
        if (StringUtils.isBlank(tokenInfo.getToken())) {
            // 返回登录
            System.out.println("没有传入对应的身份信息，返回登录");
            throw new BusinessException(403, "no authorize", "没有传入对应的身份信息");
        }
        try {
            String username = redisTemplate.opsForValue().get(tokenInfo.getToken());
            if (username != null) {
                System.out.println("校验成功");
                return true;
            } else {
                System.out.println("无效token，返回登录");
                throw new BusinessException(403, "no authorize", "无效token");
            }
        } catch (Exception e) {
            System.out.println("校验失败,信息匹配错误，返回登录");
            throw new BusinessException(403, "no authorize", "校验失败,信息匹配错误");
        }

    }

    /**
     * 在cookie中获取用户传递的token
     */
    private Token getUserToKen(HttpServletRequest request) {
        Token info = new Token();
        String token = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(token)) {
            info.setToken(token.replace("Bearer ", ""));
        }
        return info;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView mv)
            throws Exception {

    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行 （主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex)
            throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse response)
            throws IOException, UnsupportedEncodingException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
//            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
