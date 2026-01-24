package util;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {
    private static final Gson gson = new Gson();

    private ResponseUtil() {
    }

    public static void setErrorMessage(HttpServletResponse resp, Integer status, String message) throws IOException {
        var error = new ErrorResponse(message);
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(error));

    }

    private record ErrorResponse(String message) {
    }
}
