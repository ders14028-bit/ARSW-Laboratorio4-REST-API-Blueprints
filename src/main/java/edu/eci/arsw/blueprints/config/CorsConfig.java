package edu.eci.arsw.blueprints.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Habilita CORS para que el front (React + Vite, en http://localhost:5173)
 * pueda hacer peticiones REST a esta API (http://localhost:8080) durante
 * desarrollo local.
 *
 * Sin esto, el navegador bloquea las peticiones fetch del front con un
 * error de CORS antes de que lleguen al controlador.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}