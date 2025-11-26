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

        String json = String.format(
                "{\"correo\":\"%s\", \"contraseña\":\"%s\"}",
                correo, contraseña
        );

        URL url = new URL("http://localhost:8080/Crypto/login");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }

        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );

        StringBuilder responseStr = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            responseStr.append(line);
        }

        // El backend devuelve "usuario": {cedula, nombre, correo...}
        String response = responseStr.toString();

        // Extraer la cédula del usuario logueado
        long cedula = extraerCedula(response);

        // Guardar la cédula en la sesión
        HttpSession session = req.getSession();
        session.setAttribute("cedulaUsuario", cedula);

        // Redirigir al menú de billeteras
        resp.sendRedirect("Billetera.html");
    }

    private long extraerCedula(String json) {
        json = json.replace(" ", "");

        int idx = json.indexOf("\"cedula\":");
        if (idx == -1) return -1;

        String sub = json.substring(idx + 9);
        StringBuilder num = new StringBuilder();

        for (char c : sub.toCharArray()) {
            if (Character.isDigit(c)) num.append(c);
            else break;
        }
        return Long.parseLong(num.toString());
    }
}

