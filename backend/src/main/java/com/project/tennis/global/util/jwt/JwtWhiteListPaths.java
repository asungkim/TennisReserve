package com.project.tennis.global.util.jwt;

import java.util.List;

public class JwtWhiteListPaths {

    public static final List<String> PATHS = List.of(
            "/api/members/login"
    );

    public static boolean isWhiteListed(String uri, String method) {
        return PATHS.contains(uri);
    }
}
