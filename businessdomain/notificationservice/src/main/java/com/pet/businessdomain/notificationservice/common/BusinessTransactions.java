package com.pet.businessdomain.notificationservice.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.pet.businessdomain.notificationservice.dto.PetDto;
import com.pet.businessdomain.notificationservice.dto.UserDto;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.pet.businessdomain.notificationservice.repository.NotificationRepository;

@Service
public class BusinessTransactions {
    @Autowired
    private NotificationRepository userRepository;

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

    public PetDto getPet(Long id, Long uid) {
        try {
            WebClient client = webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(this.client))
                    .baseUrl("http://BUSINESSDOMAIN-PETSERVICE/api/pet")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            return client.get()
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
                    .bodyToMono(PetDto.class)
                    .block(); // IMPORTANTE: ejecutar la llamada
        } catch (Exception e) {
            System.err.println("Error fetching pet: " + e.getMessage());
            return null; // El método devuelve PetDto, no una lista
        }
    }


    public List<UserDto> getUser(Long id) {
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
                    .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {})
                    .blockOptional()
                    .orElseGet(List::of);
        } catch (Exception e) {
            System.err.println("Error fetching followers: " + e.getMessage());
            return List.of();
        }
    }
}
