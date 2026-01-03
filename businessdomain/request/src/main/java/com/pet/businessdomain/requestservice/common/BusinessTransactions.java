package com.pet.businessdomain.requestservice.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.pet.businessdomain.requestservice.dto.NotificationDto;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.pet.businessdomain.requestservice.repository.RequestRepository;

@Slf4j
@Service
public class BusinessTransactions {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;
    /*private final WebClient.Builder webClientBuilder;

    public CustomerRestController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }*/

    //webClient requires HttpClient library to work propertly
    HttpClient client = HttpClient.create()
            //Connection Timeout: is a period within which a connection between a client and a server must be established
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            //Response Timeout: The maximun time we wait to receive a response after sending a request
            .responseTimeout(Duration.ofSeconds(5))
            // Read and Write Timeout: A read timeout occurs when no data was read within a certain
            //period of time, while the write timeout when a write operation cannot finish at a specific time
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    public List<JsonNode> getUser(Long id) {
        try {
            WebClient build = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(client))
                    .baseUrl("http://BUSINESSDOMAIN-USERSERVICE/api/users")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            return build.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/pet/{id}")
                            .build(id))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new RuntimeException(
                                            "Error from Follower service: " + response.statusCode() + " - " + body
                                    )))
                    )
                    .bodyToMono(new ParameterizedTypeReference<List<JsonNode>>() {})
                    .blockOptional()
                    .orElseGet(List::of);
        } catch (Exception e) {
            System.err.println("Error fetching followers: " + e.getMessage());
            return List.of();
        }
    }

    public List<JsonNode> getPet(Long id, Long uid) {
        try {
            WebClient build = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(client))
                    .baseUrl("http://BUSINESSDOMAIN-PETSERVICE/api/pet")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            JsonNode pet = build.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/{id}/{uid}")
                            .build(id,uid))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new RuntimeException(
                                            "Error from Pet service: " + response.statusCode() + " - " + body
                                    )))
                    )
                    .bodyToMono(JsonNode.class)
                    .blockOptional()
                    .orElse(null);

            return pet != null ? List.of(pet) : List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    public Mono<NotificationDto> saveNotificationAsync(NotificationDto notificationDto) {
        WebClient client = webClientBuilder
                .baseUrl("http://BUSINESSDOMAIN-NOTIFICATIONSERVICE/api/notification")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.post()
                .uri("")
                .bodyValue(notificationDto)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "Error from Notification service: " + response.statusCode() + " - " + body
                                )))
                )
                .bodyToMono(NotificationDto.class)
                .doOnSuccess(dto -> log.info("Notificación guardada: {}", dto))
                .doOnError(error -> log.error("Error guardando notificación: {}", error.getMessage()));
    }

}
