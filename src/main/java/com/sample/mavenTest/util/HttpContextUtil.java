package com.sample.mavenTest.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * 
 * on 2017/3/1/0001.
 */
public class HttpContextUtil {

    public final static ThreadLocal<HttpServletRequest> requestLocal = new ThreadLocal<>();

    public final static ThreadLocal<HttpServletResponse> responseLocal = new ThreadLocal<>();

    private final static Pattern IP_PATTERN = Pattern.compile("^\\d+.\\d+.\\d+.\\d+$");

    public static String getIp(){
        HttpServletRequest request = requestLocal.get();
        if(request == null){return null;}
        String userIp = null;
        String httpClientIp = request.getHeader("HTTP_CLIENT_IP");
        String httpForwardedFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        if(!StringUtils.isEmpty(httpClientIp)){
            userIp =  httpClientIp;
        }else if(!StringUtils.isEmpty(httpForwardedFor)){
            userIp = httpForwardedFor.split(",")[0];
        }else{
            userIp = request.getRemoteAddr();
        }
        if(IP_PATTERN.matcher(userIp).find()){
            return userIp;
        }else{
            return null;
        }
    }
}
