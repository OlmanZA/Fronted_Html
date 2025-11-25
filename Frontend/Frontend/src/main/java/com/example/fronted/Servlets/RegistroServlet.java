package com.example.fronted.Servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/RegistroServlet")
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String cedula = req.getParameter("cedula");
        String nombre = req.getParameter("nombre");
        String correo = req.getParameter("correo");
        String contraseña = req.getParameter("contraseña");

        // JSON COMPLETO que el backend espera
        String jsonInput = String.format(
                "{\"cedula\":%s,\"nombre\":\"%s\",\"correo\":\"%s\",\"contraseña\":\"%s\"}",
                cedula,
                nombre,
                correo,
                contraseña
        );

        URL url = new URL("http://localhost:8080/Crypto/CrearUsuario");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try(OutputStream os = connection.getOutputStream()) {
            os.write(jsonInput.getBytes());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<h1>Usuario registrado correctamente</h1>");
        out.println("<p>Bienvenido, " + nombre + "</p>");
    }
}
