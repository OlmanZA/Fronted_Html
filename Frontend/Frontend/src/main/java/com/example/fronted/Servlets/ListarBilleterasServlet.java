package com.example.fronted.Servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@WebServlet("/ListarBilleterasServlet")
public class ListarBilleterasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // 1. Recibir cédula del formulario
        String cedulaStr = req.getParameter("cedula");

        if (cedulaStr == null || cedulaStr.isEmpty()) {
            resp.getWriter().println("Error: Debes enviar la cédula.");
            return;
        }

        long cedula = Long.parseLong(cedulaStr);

        // 2. Llamar al backend
        URL url = new URL("http://localhost:8080/Crypto/BilleterasUsuario/" + cedula);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        // 3. Parsear JSON (lista de billeteras)
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> billeteras = gson.fromJson(sb.toString(), listType);

        // 4. Construir la respuesta HTML
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<h1>Billeteras del usuario " + cedula + "</h1>");

        if (billeteras.isEmpty()) {
            out.println("<p>No se encontraron billeteras para este usuario.</p>");
        } else {
            out.println("<table border='1' cellpadding='10'>");
            out.println("<tr><th>ID</th><th>Nombre</th></tr>");

            for (Map<String, Object> b : billeteras) {
                long id = ((Number) b.get("numeroBilletera")).longValue();
                String nombre = (String) b.get("nombre");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + nombre + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
        }

        out.println("<br><button onclick=\"location.href='ListarBilleteras.html'\">Volver</button>");
    }
}
