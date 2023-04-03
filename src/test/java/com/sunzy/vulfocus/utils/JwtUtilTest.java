package com.sunzy.vulfocus.utils;

import com.auth0.jwt.interfaces.Claim;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {




    @Test
    void verifyToken() {
//        String data = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1N1cGVydXNlciI6IjAiLCJhdmF0YXIiOiJodHRwOi8vd3d3LmJhaW1hb2h1aS5uZXQvaG9tZS9pbWFnZS9pY29uLWFucXVhbi1sb2dvLnBuZyIsImV4cCI6MTY4MTA5MzUxNiwiaWF0IjoxNjgwNDg4NzE2LCJ1c2VybmFtZSI6InNzcyJ9.Idqno9dsdUPnE4QR0omxKXAJvxrkIzMTeDymkSoHXTU";
        String data = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc1N1cGVydXNlciI6ZmFsc2UsImF2YXRhciI6Imh0dHA6Ly93d3cuYmFpbWFvaHVpLm5ldC9ob21lL2ltYWdlL2ljb24tYW5xdWFuLWxvZ28ucG5nIiwiZXhwIjoxNjgxMDkzNTkyLCJpYXQiOjE2ODA0ODg3OTIsInVzZXJuYW1lIjoic3NzIn0.UMMSQVprlyV_AcPh7LOGRjLUxnUanlKZ4l1Irs8nrdw";

        Map<String, Claim> map = JwtUtil.verifyToken(data);
        System.out.println(map.get("username").asString());
        System.out.println(map.get("isSuperuser").asBoolean());
//        assert map != null;
    }
}