package com.homework.finsta.util;

/**
 * Created by kbw815 on 7/25/15.
 */
public class Const {
    public final static String CLIENT_ID = "b5e6207af2b5435dadef7572b98e61f0";
    public final static String CLIENT_SECRET = "49212635f6ce49c6a50ee79727927a6b";
    public final static String REDIRECT_URI = "http://localhost:3030";
    public final static String AUTHORIZATION_CODE = "authorization_code";

    public final static String FIELD_CLIENT_ID = "client_id";
    public final static String FIELD_CLIENT_SECRET = "client_secret";
    public final static String FIELD_GRANT_TYPE = "grant_type";
    public final static String FIELD_REDIRECT_URI = "redirect_uri";
    public final static String FIELD_CODE = "code";

    public final static String FIELD_ACCESS_TOKEN = "access_token";
    public final static String FIELD_USER = "user";
    public final static String FIELD_USERNAME = "user_name";
    public final static String FIELD_ID = "id";
    public final static String FIELD_BIO = "bio";
    public final static String FIELD_WEBSITE ="website";
    public final static String FIELD_FULL_NAME = "full_name";
    public final static String FIELD_PROFILE_PICTURE = "profile_picture";
    public final static String FIELD_COUNT = "count";
    public final static String FIELD_MIN_ID = "min_id";
    public final static String FIELD_MAX_ID = "max_id";

    public final static String FIELD_TYPE = "type";
    public final static String FIELD_URL = "url";
    public final static String FIELD_PAGINATION = "pagination";
    public final static String FIELD_NEXT_MAX_ID = "next_max_id";
    public final static String FIELD_DATA = "data";
    public final static String FIELD_VIDEO = "video";
    public final static String FIELD_VIDEOS = "videos";
    public final static String FIELD_IMAGE = "image";
    public final static String FIELD_IMAGES = "images";
    public final static String FIELD_STANDARD_RESOLUTION = "standard_resolution";
    public final static String FIELD_WIDTH = "width";


    public final static String BASE_URL = "https://api.instagram.com/";
    public final static String BASE_URL2 = "https://instagram.com/";
    public final static String OAUTH_AUTHORIZE_URL = "oauth/authorize/";
    public final static String OAUTH_ACCESS_TOKEN_URL = "oauth/access_token/";
    public final static String FEED_URL = "v1/users/self/feed/";
    public final static String URL_LOG_OUT = "https://instagram.com/accounts/logout/";
    public final static String URL_AUTHORIZATION = BASE_URL + OAUTH_AUTHORIZE_URL + "?client_id="+ CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code";
    public final static String URL_AUTHORIZATION2 = BASE_URL2 + OAUTH_AUTHORIZE_URL + "?client_id="+ CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code";
}
