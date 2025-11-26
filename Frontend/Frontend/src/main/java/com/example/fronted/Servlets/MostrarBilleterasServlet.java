package com.example.fronted.Servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/MostrarBilleterasServlet")
public class MostrarBilleterasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        System.out.println("=== MOSTRAR BILLETERAS ===");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect("/login.html");
            return;
        }

        String correo = (String) session.getAttribute("usuario");
        String cedula = (String) session.getAttribute("cedula");

        System.out.println("Usuario en sesión: " + correo);
        System.out.println("Cédula en sesión: " + cedula);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (cedula == null || cedula.isEmpty() || cedula.equals("0")) {
            out.println("<h1>Error: No se pudo obtener la cédula</h1>");
            out.println("<a href='/DashboardServlet'>Volver al Dashboard</a>");
            return;
        }

        try {
            // Obtener billeteras del backend
            URL url = new URL("http://localhost:8080/Crypto/BilleterasUsuario/" + cedula);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code billeteras: " + responseCode);

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

            System.out.println("Respuesta billeteras: " + response.toString());

            // Generar HTML
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Mis Billeteras</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
            out.println(".container { max-width: 1000px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
            out.println(".header { background: #2196F3; color: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; }");
            out.println(".billetera { border: 1px solid #ddd; padding: 20px; margin: 15px 0; border-radius: 8px; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            out.println(".billetera-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }");
            out.println(".billetera-nombre { font-size: 1.4em; font-weight: bold; color: #2196F3; margin: 0; }");
            out.println(".billetera-numero { background: #f0f0f0; padding: 5px 10px; border-radius: 15px; font-size: 0.9em; }");
            out.println(".billetera-info { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 15px; }");
            out.println(".info-item { padding: 8px; background: #f9f9f9; border-radius: 5px; }");
            out.println(".info-label { font-weight: bold; color: #666; }");
            out.println(".back-link { display: inline-block; margin-top: 20px; padding: 10px 15px; background: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }");
            out.println(".empty { text-align: center; padding: 40px; color: #666; }");
            out.println(".success { color: green; }");
            out.println(".error { color: red; }");
            out.println(".billeteras-count { background: #4CAF50; color: white; padding: 5px 10px; border-radius: 15px; font-size: 0.9em; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<div class='header'>");
            out.println("<h1>👛 Mis Billeteras</h1>");
            out.println("<p>Usuario: " + correo + " | Cédula: " + cedula + "</p>");
            out.println("</div>");

            if (responseCode == 200) {
                String jsonResponse = response.toString();

                if (jsonResponse.equals("[]") || jsonResponse.contains("No se encontraron billeteras")) {
                    out.println("<div class='empty'>");
                    out.println("<h2>No tienes billeteras creadas</h2>");
                    out.println("<p>Crea tu primera billetera para comenzar</p>");
                    out.println("<a href='/crear-billetera.html' class='back-link'>Crear Primera Billetera</a>");
                    out.println("</div>");
                } else {
                    // Parsear y mostrar las billeteras de forma bonita
                    mostrarBilleterasFormateadas(out, jsonResponse);
                }
            } else if (responseCode == 204) {
                out.println("<div class='empty'>");
                out.println("<h2>No tienes billeteras creadas</h2>");
                out.println("<a href='/crear-billetera.html' class='back-link'>Crear Primera Billetera</a>");
                out.println("</div>");
            } else {
                out.println("<h2 class='error'>Error al cargar billeteras</h2>");
                out.println("<p><strong>Código:</strong> " + responseCode + "</p>");
                out.println("<p><strong>Mensaje:</strong> " + response.toString() + "</p>");
            }

            out.println("<a href='/DashboardServlet' class='back-link'>← Volver al Dashboard</a>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

            conn.disconnect();

        } catch (Exception e) {
            out.println("<h1 class='error'>Error de conexión</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<a href='/DashboardServlet'>Volver al Dashboard</a>");
            e.printStackTrace();
        }
    }

    // Método para mostrar las billeteras formateadas
    private void mostrarBilleterasFormateadas(PrintWriter out, String jsonResponse) {
        try {
            // Contar billeteras
            int count = contarBilleteras(jsonResponse);
            out.println("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;'>");
            out.println("<h2>Tus Billeteras</h2>");
            out.println("<span class='billeteras-count'>" + count + " billetera" + (count != 1 ? "s" : "") + "</span>");
            out.println("</div>");

            // Parsear manualmente el JSON array
            String cleanJson = jsonResponse.substring(1, jsonResponse.length() - 1); // Remover [ y ]
            String[] billeteras = cleanJson.split("\\},\\{");

            for (int i = 0; i < billeteras.length; i++) {
                String billeteraStr = billeteras[i];
                if (i == 0) billeteraStr = billeteraStr + "}";
                else if (i == billeteras.length - 1) billeteraStr = "{" + billeteraStr;
                else billeteraStr = "{" + billeteraStr + "}";

                out.println("<div class='billetera'>");
                out.println("<div class='billetera-header'>");

                // Extraer nombre
                String nombre = extraerValor(billeteraStr, "nombre");
                out.println("<h3 class='billetera-nombre'>💰 " + nombre + "</h3>");

                // Extraer número de billetera
                String numero = extraerValor(billeteraStr, "numeroBilletera");
                out.println("<span class='billetera-numero'>#" + numero + "</span>");

                out.println("</div>");

                out.println("<div class='billetera-info'>");
                out.println("<div class='info-item'>");
                out.println("<div class='info-label'>Usuario</div>");
                String usuarioNombre = extraerValorDeObjeto(billeteraStr, "usuario", "nombre");
                out.println("<div>" + usuarioNombre + "</div>");
                out.println("</div>");

                out.println("<div class='info-item'>");
                out.println("<div class='info-label'>Correo</div>");
                String usuarioCorreo = extraerValorDeObjeto(billeteraStr, "usuario", "correo");
                out.println("<div>" + usuarioCorreo + "</div>");
                out.println("</div>");

                out.println("<div class='info-item'>");
                out.println("<div class='info-label'>Cédula</div>");
                String usuarioCedula = extraerValorDeObjeto(billeteraStr, "usuario", "cedula");
                out.println("<div>" + usuarioCedula + "</div>");
                out.println("</div>");

                out.println("<div class='info-item'>");
                out.println("<div class='info-label'>Estado</div>");
                out.println("<div style='color: #4CAF50;'>🟢 Activa</div>");
                out.println("</div>");
                out.println("</div>");

                out.println("</div>");
            }

        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("<p>Error mostrando billeteras: " + e.getMessage() + "</p>");
            out.println("<pre>" + jsonResponse + "</pre>");
            out.println("</div>");
        }
    }

    private int contarBilleteras(String jsonResponse) {
        if (jsonResponse.equals("[]")) return 0;
        String cleanJson = jsonResponse.substring(1, jsonResponse.length() - 1);
        String[] billeteras = cleanJson.split("\\},\\{");
        return billeteras.length;
    }

    private String extraerValor(String json, String clave) {
        try {
            String search = "\"" + clave + "\":";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            String valor = json.substring(start, end).trim();
            valor = valor.replace("\"", "");
            return valor;
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String extraerValorDeObjeto(String json, String objeto, String clave) {
        try {
            String search = "\"" + objeto + "\":";
            int startObj = json.indexOf(search) + search.length();
            int endObj = json.indexOf("}", startObj) + 1;
            String objetoJson = json.substring(startObj, endObj);
            return extraerValor(objetoJson, clave);
        } catch (Exception e) {
            return "N/A";
        }
    }
}