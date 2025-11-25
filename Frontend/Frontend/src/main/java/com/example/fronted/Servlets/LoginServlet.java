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

        String correo = req.getParameter("correo");
        String contraseña = req.getParameter("contraseña");

        String jsonInput = String.format(
                "{\"correo\":\"%s\",\"contraseña\":\"%s\"}", correo, contraseña
        );

        URL url = new URL("http://localhost:8080/Crypto/login");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try(OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        if (response.toString().contains("\"exito\":true")) {
            out.println("<h1>Bienvenido " + correo + "</h1>");
        } else {
            out.println("<h1>Credenciales incorrectas</h1>");
        }
    }
}
