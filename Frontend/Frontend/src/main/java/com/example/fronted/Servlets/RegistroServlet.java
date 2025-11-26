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

        String jsonInput = String.format(
                "{\"cedula\":%s,\"nombre\":\"%s\",\"correo\":\"%s\",\"contraseña\":\"%s\"}",
                cedula, nombre, correo, contraseña
        );

        URL url = new URL("http://localhost:8080/Crypto/CrearUsuario");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonInput.getBytes());
        }

        int status = connection.getResponseCode();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (status == 201 || status == 200) {
            out.println("<html>");
            out.println("<head>");
            out.println("<meta http-equiv='refresh' content='2; URL=index.html'>");
            out.println("<style>");
            out.println("body { font-family: Arial; background:#f0fff0; text-align:center; padding:40px; }");
            out.println(".box { background:white; padding:30px; border-radius:10px; display:inline-block; "
                    + "box-shadow:0 4px 10px rgba(0,0,0,0.1); }");
            out.println("h1 { color:green; }");
            out.println("button { padding:12px 25px; margin-top:20px; font-size:16px; "
                    + "background:#4CAF50; color:white; border:none; border-radius:8px; cursor:pointer; }");
            out.println("button:hover { opacity:.9; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='box'>");
            out.println("<h1>Usuario registrado correctamente</h1>");
            out.println("<p>Bienvenido, " + nombre + "</p>");
            out.println("<p>Serás redirigido al inicio en 2 segundos...</p>");
            out.println("<button onclick=\"window.location.href='index.html'\">Volver al inicio</button>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } else {

            BufferedReader errorBR = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream())
            );
            StringBuilder errorResp = new StringBuilder();
            String line;
            while ((line = errorBR.readLine()) != null) errorResp.append(line);

            out.println("<html>");
            out.println("<head>");
            out.println("<style>");
            out.println("body { font-family: Arial; background:#fff0f0; text-align:center; padding:40px; }");
            out.println(".box { background:white; padding:30px; border-radius:10px; display:inline-block; "
                    + "box-shadow:0 4px 10px rgba(0,0,0,0.1); }");
            out.println("h1 { color:red; }");
            out.println("button { padding:12px 25px; margin-top:20px; font-size:16px; "
                    + "background:#2196F3; color:white; border:none; border-radius:8px; cursor:pointer; }");
            out.println("button:hover { opacity:.9; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='box'>");
            out.println("<h1>Error al registrar usuario</h1>");
            out.println("<p>Detalles del error:</p>");
            out.println("<pre>" + errorResp + "</pre>");
            out.println("<button onclick=\"window.location.href='Registro.html'\">Intentar nuevamente</button>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
