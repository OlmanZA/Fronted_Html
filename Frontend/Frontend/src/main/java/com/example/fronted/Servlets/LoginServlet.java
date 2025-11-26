package com.example.fronted.Servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String correo = req.getParameter("correo");
        String contraseña = req.getParameter("contraseña");

        PrintWriter out = resp.getWriter();

        if (correo == null || contraseña == null || correo.isEmpty() || contraseña.isEmpty()) {
            out.println("<h1>Error: Campos requeridos</h1>");
            out.println("<a href='/login.html'>Volver</a>");
            return;
        }

        String jsonInput = String.format(
                "{\"correo\":\"%s\",\"contraseña\":\"%s\"}", correo, contraseña
        );

        try {
            URL url = new URL("http://localhost:8080/Crypto/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try(OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();

            InputStream inputStream = (responseCode == 200) ?
                    conn.getInputStream() : conn.getErrorStream();

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            if (responseCode == 200 && response.toString().contains("\"success\":true")) {
                // Login exitoso - extraer cédula del JSON de respuesta
                String cedula = extraerCedulaDeRespuesta(response.toString());

                HttpSession session = req.getSession();
                session.setAttribute("usuario", correo);
                session.setAttribute("cedula", cedula); // ← GUARDAR CÉDULA EN SESIÓN
                session.setMaxInactiveInterval(30 * 60);

                System.out.println("✅ Login exitoso - Usuario: " + correo + ", Cédula: " + cedula);
                resp.sendRedirect("/DashboardServlet");
            } else {
                out.println("<h1>Credenciales incorrectas</h1>");
                out.println("<a href='/login.html'>Volver a intentar</a>");
            }

            conn.disconnect();

        } catch (Exception e) {
            out.println("<h1>Error de conexión</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<a href='/login.html'>Volver</a>");
        }
    }

    private String extraerCedulaDeRespuesta(String jsonResponse) {
        try {
            System.out.println("📨 Respuesta login: " + jsonResponse);

            // Buscar la cédula en el JSON
            if (jsonResponse.contains("\"cedula\"")) {
                int startIndex = jsonResponse.indexOf("\"cedula\":") + 9; // +9 para pasar "cedula":
                int endIndex = jsonResponse.indexOf(",", startIndex);
                if (endIndex == -1) endIndex = jsonResponse.indexOf("}", startIndex);

                String cedula = jsonResponse.substring(startIndex, endIndex).trim();
                cedula = cedula.replace("\"", "").replace(":", ""); // Remover comillas y puntos
                cedula = cedula.trim();

                System.out.println("🆔 Cédula extraída: " + cedula);
                return cedula;
            }
        } catch (Exception e) {
            System.out.println("❌ Error extrayendo cédula: " + e.getMessage());
        }
        return "0";
    }
}