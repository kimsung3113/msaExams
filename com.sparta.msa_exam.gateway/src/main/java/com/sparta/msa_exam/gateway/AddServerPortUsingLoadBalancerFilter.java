package com.sparta.msa_exam.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "AddServerPortUsingLoadBalancerFilter")
@Component
public class AddServerPortUsingLoadBalancerFilter implements GlobalFilter {

    private final LoadBalancerClient loadBalancerClient;

    public AddServerPortUsingLoadBalancerFilter(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//            String serviceId = "product-service";
//
//            // 로드밸런싱된 인스턴스 선택
//            ServiceInstance instance = loadBalancerClient.choose(serviceId);
//            if (instance != null) {
//                String serverPort = String.valueOf(instance.getPort());
//
//                // 응답 헤더에 추가
//                exchange.getResponse().getHeaders().add("Server-Port", serverPort);
//            }else{
//                log.info("AddServerPortUsingLoadBalancerFilter : No product-service instance available");
//            }

        String serviceId = exchange.getRequest().getURI().getHost();

        log.info("Host name : " + serviceId); // localhost다..

        // product-service인 경우, 로드밸런싱을 통해 서버 포트를 가져옴
        if ("product-service".equals(serviceId)) {
            return getProductServicePort()
                    .doOnNext(serverPort -> {
                        exchange.getResponse().getHeaders().add("Server-Port", serverPort);
                    })
                    .then(chain.filter(exchange));
        }


        return chain.filter(exchange);
    }

    private Mono<String> getProductServicePort() {
        // 로드밸런싱을 통해 product-service의 서버 포트 가져오기
        return Mono.fromCallable(() -> {
            ServiceInstance instance = loadBalancerClient.choose("product-service");
            if (instance != null) {
                log.info("Get product service port : " + instance.getPort());
                return String.valueOf(instance.getPort());
            }
            return "Unknown";  // product-service의 포트가 없으면 "Unknown" 반환
        });
    }
}
