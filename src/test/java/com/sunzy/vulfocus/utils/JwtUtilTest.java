package com.sunzy.vulfocus.utils;

import com.auth0.jwt.interfaces.Claim;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void verifyToken() {

        String token  = "1eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwYXNzd29yZCI6IjN5emE1ZGJ1c245emQwNmV3OG9iQDM1OWUwYmMyYTAyNTYyZWZkNWMxMzZmYmI0ZmYxYWEwIiwiaXNTdXBlcnVzZXIiOmZhbHNlLCJhdmF0YXIiOiJodHRwOi8vd3d3LmJhaW1hb2h1aS5uZXQvaG9tZS9pbWFnZS9pY29uLWFucXVhbi1sb2dvLnBuZyIsImV4cCI6MTY4MDQ1MDg1MSwiaWF0IjoxNjgwNDQ5MDUxLCJ1c2VybmFtZSI6InNzcyJ9.5zGyMuvzx77P0Ac9NXFAeTm61NqtVJUE5Hc2tACGl7s";
        Map<String, Claim> map = JwtUtil.verifyToken(token);
//        System.out.println(map.get("password"));
        assert map != null;
    }
}