package util;

import com.google.gson.Gson;
import dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {

    private static final Gson gson = new Gson();

    private ResponseUtil() {
    }

    public static void json(HttpServletResponse resp, int status, Object data) throws IOException {
        write(resp, status, data);
    }

    public static void error(HttpServletResponse resp, int status, String message) throws IOException {
        write(resp, status, new ErrorResponseDto(message));
    }

    private static void write(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(body));
    }
}
