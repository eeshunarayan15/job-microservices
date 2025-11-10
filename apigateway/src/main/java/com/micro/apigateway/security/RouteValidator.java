package com.micro.apigateway.security;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import  java.util.*;

@Component
public class RouteValidator {
    public  static List<String > openApiEndpoints=List.of(
            "/auth/api/v1/register",
            "/auth/api/v1/login",
            "/eureka",
            "/api-doc"
    );
    public Predicate<ServerHttpRequest> isSecured=
            request->openApiEndpoints.stream().noneMatch(uri->request.getURI().getPath().contains(uri));
}
