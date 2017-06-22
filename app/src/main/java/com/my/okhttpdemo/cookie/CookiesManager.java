package com.my.okhttpdemo.cookie;

import com.my.okhttpdemo.BaseApplication;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by YJH on 2017/3/15 14:07.
 */

public class CookiesManager implements CookieJar {

    private PersistentCookieStore cookieStore = new PersistentCookieStore(BaseApplication.getAppContext());

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                cookieStore.add(url, cookie);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
}
