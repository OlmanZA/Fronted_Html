package com.example.fronted.Configs;

import com.example.fronted.Servlets.*;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<RegistroServlet> registroServlet() {
        ServletRegistrationBean<RegistroServlet> bean = new ServletRegistrationBean<>(
                new RegistroServlet(), "/RegistroServlet"
        );
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public ServletRegistrationBean<LoginServlet> loginServlet() {
        ServletRegistrationBean<LoginServlet> bean = new ServletRegistrationBean<>(
                new LoginServlet(), "/LoginServlet"
        );
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public ServletRegistrationBean<DashboardServlet> dashboardServlet() {
        ServletRegistrationBean<DashboardServlet> bean = new ServletRegistrationBean<>(
                new DashboardServlet(), "/DashboardServlet"
        );
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public ServletRegistrationBean<CrearBilleteraServlet> crearBilleteraServlet() {
        ServletRegistrationBean<CrearBilleteraServlet> bean = new ServletRegistrationBean<>(
                new CrearBilleteraServlet(), "/CrearBilleteraServlet"
        );
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public ServletRegistrationBean<MostrarBilleterasServlet> mostrarBilleterasServlet() {
        ServletRegistrationBean<MostrarBilleterasServlet> bean = new ServletRegistrationBean<>(
                new MostrarBilleterasServlet(), "/MostrarBilleterasServlet"
        );
        bean.setLoadOnStartup(1);
        return bean;
    }

    @Bean
    public ServletRegistrationBean<LogoutServlet> logoutServlet() {
        ServletRegistrationBean<LogoutServlet> bean = new ServletRegistrationBean<>(
                new LogoutServlet(), "/LogoutServlet"
        );
        bean.setLoadOnStartup(1);
        return bean;
    }
}