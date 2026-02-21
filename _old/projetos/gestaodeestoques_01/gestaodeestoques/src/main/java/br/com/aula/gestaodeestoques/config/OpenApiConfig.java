package br.com.aula.gestaodeestoques.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de Gestão de Estoques",
        version = "v1.0",
        description = "API RESTful para gerenciar produtos, categorias e fornecedores."
    )
)
@SecurityScheme(
    name = "bearerAuth", // Nome de referência para o esquema de segurança
    description = "Token JWT de autenticação",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {}