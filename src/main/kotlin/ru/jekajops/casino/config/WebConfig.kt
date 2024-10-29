package ru.jekajops.casino.config

import org.springframework.web.reactive.config.WebFluxConfigurer

//import jakarta.servlet.FilterChain
//import jakarta.servlet.ServletRequest
//import jakarta.servlet.ServletResponse
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.server.WebFilter

@Configuration
@EnableWebFlux
class WebConfig
    : WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:8080",
                "0.0.0.0",
                "http://temka-jekajops.amvera.io",
                "https://appery.io"
            )
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/resources/**")
            .addResourceLocations("/resources/")
    }

//    override fun filter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
//        val response = res as HttpServletResponse
//        val request = req as HttpServletRequest
//        println("WebConfig; " + request.requestURI)
//        response.setHeader("Access-Control-Allow-Origin", "*")
//        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
//        response.setHeader("Access-Control-Allow-Headers",
//            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,observe")
//        response.setHeader("Access-Control-Max-Age", "3600")
//        response.setHeader("Access-Control-Allow-Credentials", "true")
//        response.setHeader("Access-Control-Expose-Headers", "Authorization")
//        response.addHeader("Access-Control-Expose-Headers", "responseType")
//        response.addHeader("Access-Control-Expose-Headers", "observe")
//        println("Request Method: " + request.method)
//        if (!request.method.equals("OPTIONS", ignoreCase = true)) {
//            try {
//                chain.doFilter(req, res)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        } else {
//            println("Pre-flight")
//            response.setHeader("Access-Control-Allow-Origin", "*")
//            response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,PUT")
//            response.setHeader("Access-Control-Max-Age", "3600")
//            response.setHeader("Access-Control-Allow-Headers",
//                "Access-Control-Expose-Headers" + "Authorization, content-type," +
//                        "USERID" + "ROLE" +
//                        "access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with,responseType,observe")
//            response.status = HttpServletResponse.SC_OK
//        }
//    }
}