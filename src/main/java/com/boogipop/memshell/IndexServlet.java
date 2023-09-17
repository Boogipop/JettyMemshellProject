package com.boogipop.memshell;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //String message = "jetty index servlet test";
        //String id      = req.getParameter("id");
        //
        //StringBuilder sb = new StringBuilder();
        //sb.append(message);
        //if (id != null && !id.isEmpty()) {
        //    sb.append("\nid: ").append(id);
        //}
        //
        //resp.getWriter().println(sb);
        new EvilCustomizer();
    }
}