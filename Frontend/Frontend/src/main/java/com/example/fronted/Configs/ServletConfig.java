package com.example.fronted.Configs;

import com.example.fronted.Servlets.LoginServlet;
import com.example.fronted.Servlets.RegistroServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<RegistroServlet> registroServlet() {
        return new ServletRegistrationBean<>(new RegistroServlet(), "/RegistroServlet");
    }

    @Bean
    public ServletRegistrationBean<LoginServlet> loginServlet() {
        return new ServletRegistrationBean<>(new LoginServlet(), "/LoginServlet");
    }
}
