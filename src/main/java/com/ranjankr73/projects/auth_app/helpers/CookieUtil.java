package com.ranjankr73.projects.auth_app.helpers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void attachRefreshToken(HttpServletResponse response, String refreshToken, int maxAge){
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(maxAge);
//        cookie.setDomain(".projects.ranjankr73.com");

        response.addCookie(cookie);
    }


}
