package com.example.fronted.Servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/CrearBilleteraServlet")
public class CrearBilleteraServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        long cedula = (long) session.getAttribute("cedulaUsuario");

        String nombre = req.getParameter("nombre");

        // JSON EXACTO que tu backend espera
        String json = String.format(
                "{\"nombre\":\"%s\", \"usuario\": { \"cedula\": %s }}",
                nombre,
                cedula
        );

        URL url = new URL("http://localhost:8080/Crypto/crearBilletera");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }

        int status = con.getResponseCode();

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        if (status == 201) {
            out.println("<h1>Billetera creada con éxito</h1>");
        } else {
            out.println("<h1>Error al crear billetera</h1>");
        }

        out.println("<a href='Billetera.html'>Volver</a>");
    }
}
