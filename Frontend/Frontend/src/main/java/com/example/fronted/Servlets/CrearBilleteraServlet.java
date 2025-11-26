package com.example.fronted.Servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/CrearBilleteraServlet")
public class CrearBilleteraServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        System.out.println("🎯 CREAR BILLETERA SERVLET - INICIANDO");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect("/login.html");
            return;
        }

        String correo = (String) session.getAttribute("usuario");
        String cedula = (String) session.getAttribute("cedula");
        String nombreBilletera = req.getParameter("nombre");

        System.out.println("📧 Correo: " + correo);
        System.out.println("🆔 Cédula: " + cedula);
        System.out.println("🏷️ Nombre: " + nombreBilletera);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (nombreBilletera == null || nombreBilletera.isEmpty()) {
            out.println("<h1>Error: El nombre es requerido</h1>");
            out.println("<a href='/crear-billetera.html'>Volver</a>");
            return;
        }

        if (cedula == null || cedula.equals("0")) {
            out.println("<h1>Error: No se pudo obtener la cédula</h1>");
            out.println("<a href='/LogoutServlet'>Cerrar Sesión y volver a login</a>");
            return;
        }

        try {
            // FORMA MÁS SEGURA de crear el JSON - sin String.format
            String jsonBilletera = "{" +
                    "\"nombre\":\"" + nombreBilletera + "\"," +
                    "\"usuario\":{" +
                    "\"cedula\":" + cedula +  // ← SOLO UN :
                    "}" +
                    "}";

            System.out.println("📦 JSON a enviar: " + jsonBilletera);

            // Verificar que no tenga ::
            if (jsonBilletera.contains("::")) {
                System.out.println("❌ ERROR: JSON contiene ::");
                jsonBilletera = jsonBilletera.replace("::", ":");
                System.out.println("✅ JSON corregido: " + jsonBilletera);
            }

            URL url = new URL("http://localhost:8080/Crypto/crearBilletera");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            // Enviar datos
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBilletera.getBytes("UTF-8");
                os.write(input, 0, input.length);
                System.out.println("📤 Datos enviados: " + input.length + " bytes");
            }

            int responseCode = conn.getResponseCode();
            System.out.println("📡 Response Code: " + responseCode);

            StringBuilder response = new StringBuilder();
            InputStream inputStream;

            if (responseCode >= 200 && responseCode < 300) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            System.out.println("📨 Respuesta completa: " + response.toString());

            // Mostrar resultado
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Resultado</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
            out.println(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
            out.println(".success { color: green; }");
            out.println(".error { color: red; }");
            out.println(".debug { background: #f0f0f0; padding: 15px; border-radius: 5px; margin: 10px 0; font-family: monospace; }");
            out.println(".back-link { display: inline-block; margin-top: 20px; padding: 10px 15px; background: #2196F3; color: white; text-decoration: none; border-radius: 5px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");

            if (responseCode == 200 || responseCode == 201) {
                if (response.toString().contains("\"success\":true")) {
                    out.println("<h1 class='success'>¡Billetera creada exitosamente!</h1>");
                    out.println("<p><strong>Nombre:</strong> " + nombreBilletera + "</p>");
                    out.println("<p><strong>Usuario:</strong> " + correo + "</p>");
                    out.println("<p><strong>Cédula:</strong> " + cedula + "</p>");
                    out.println("<a href='/DashboardServlet' class='back-link'>Volver al Dashboard</a>");
                } else {
                    out.println("<h1 class='error'>Error inesperado</h1>");
                    out.println("<p>El backend respondió pero no indicó éxito</p>");
                    out.println("<div class='debug'>");
                    out.println("<strong>Respuesta:</strong> " + response.toString());
                    out.println("</div>");
                    out.println("<a href='/crear-billetera.html' class='back-link'>Volver</a>");
                }
            } else {
                out.println("<h1 class='error'>Error al crear billetera</h1>");
                out.println("<p><strong>Código:</strong> " + responseCode + "</p>");
                out.println("<p><strong>Mensaje:</strong> " + response.toString() + "</p>");

                out.println("<div class='debug'>");
                out.println("<h3>Información para debugging:</h3>");
                out.println("<p><strong>JSON enviado:</strong> " + jsonBilletera + "</p>");
                out.println("<p><strong>¿Contiene :: ?</strong> " + jsonBilletera.contains("::") + "</p>");
                out.println("</div>");

                out.println("<a href='/crear-billetera.html' class='back-link'>Volver a intentar</a>");
            }

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

            conn.disconnect();

        } catch (Exception e) {
            out.println("<h1 class='error'>Error de conexión</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<a href='/crear-billetera.html' class='back-link'>Volver</a>");
            e.printStackTrace();
        }
    }
}