package com.example.fronted.Servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        System.out.println("=== DASHBOARD SERVLET LLAMADO ===");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            System.out.println("No hay sesión - Redirigiendo a login");
            resp.sendRedirect("/login.html");
            return;
        }

        String correo = (String) session.getAttribute("usuario");
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Dashboard</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        out.println(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
        out.println(".welcome { background: #4CAF50; color: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; }");
        out.println(".options { display: flex; gap: 20px; margin: 20px 0; }");
        out.println(".option-card { flex: 1; background: #2196F3; color: white; padding: 20px; border-radius: 5px; text-align: center; cursor: pointer; text-decoration: none; display: block; }");
        out.println(".option-card:hover { background: #1976D2; }");
        out.println(".logout { text-align: right; margin-top: 20px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<div class='welcome'>");
        out.println("<h1>Bienvenido, " + correo + "</h1>");
        out.println("</div>");
        out.println("<div class='options'>");
        out.println("<a href='/crear-billetera.html' class='option-card'>");
        out.println("<h2>➕ Crear Billetera</h2>");
        out.println("<p>Crear una nueva billetera</p>");
        out.println("</a>");
        out.println("<a href='/MostrarBilleterasServlet' class='option-card'>");
        out.println("<h2>👛 Mostrar Billeteras</h2>");
        out.println("<p>Ver todas tus billeteras</p>");
        out.println("</a>");
        out.println("</div>");
        out.println("<div class='logout'>");
        out.println("<a href='/LogoutServlet' style='color: #f44336; text-decoration: none;'>Cerrar Sesión</a>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}