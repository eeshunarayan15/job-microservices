package com.micro.apigateway;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeHints.GatewayRuntimeHints.class)
public class NativeHints {
    
    static class GatewayRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Register Netty resources
            hints.resources().registerPattern("META-INF/native-image/**");
            hints.resources().registerPattern("META-INF/services/**");
            
            // Register reflection for Gateway components
            registerGatewayClasses(hints, classLoader);
            registerNettyClasses(hints, classLoader);
            registerKubernetesClasses(hints, classLoader);
        }
        
        private void registerGatewayClasses(RuntimeHints hints, ClassLoader classLoader) {
            try {
                hints.reflection().registerType(
                    Class.forName("org.springframework.cloud.gateway.filter.GatewayFilterChain"),
                    hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_METHODS)
                );
                hints.reflection().registerType(
                    Class.forName("org.springframework.cloud.gateway.handler.predicate.PredicateDefinition"),
                    hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
                                            MemberCategory.INVOKE_DECLARED_METHODS)
                );
            } catch (ClassNotFoundException e) {
                // Classes not available, skip
            }
        }
        
        private void registerNettyClasses(RuntimeHints hints, ClassLoader classLoader) {
            try {
                hints.reflection().registerType(
                    Class.forName("reactor.netty.http.server.HttpServerConfig"),
                    hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                            MemberCategory.INVOKE_DECLARED_METHODS,
                                            MemberCategory.DECLARED_FIELDS)
                );
            } catch (ClassNotFoundException e) {
                // Class not available, skip
            }
        }
        
        private void registerKubernetesClasses(RuntimeHints hints, ClassLoader classLoader) {
            try {
                hints.reflection().registerType(
                    Class.forName("io.fabric8.kubernetes.client.Config"),
                    hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                            MemberCategory.DECLARED_FIELDS,
                                            MemberCategory.INVOKE_DECLARED_METHODS)
                );
            } catch (ClassNotFoundException e) {
                // Class not available, skip
            }
        }
    }
}
