package com.example.demo.service;



import com.example.demo.exception.MediaTypeNotAcceptableException;
import com.example.demo.exception.UserNotFoundException;

import com.example.demo.model.Branch;
import com.example.demo.model.Repository;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;


@Service
public class GitHubAPIService {

//    @Value("${github.token}")
//    private String token;
    private String token = "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";
    private final WebClient webClient;

    public GitHubAPIService(WebClient webClient) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    public Flux<Repository> getUserRepositories(String username, String acceptHeader) {
        System.out.println("Header w getUserRepositories method: " + acceptHeader);
        return webClient
                .get()
                .uri("/users/{username}/repos", username)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()

                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                        Mono.error(() -> new UserNotFoundException(HttpStatus.NOT_FOUND.value(), "USER NOT FOUND")))

                .bodyToFlux(Repository.class)
                .filter(repository -> !repository.isFork())
                .flatMap(repository -> getBranchesOfRepository(username, repository.getName())
                        .map(branches -> {
                            repository.setBranches(branches);
                            return repository;
                        })
                        .defaultIfEmpty(repository));
    }

    public Mono<List<Branch>> getBranchesOfRepository(String username, String repository) {
        Mono<List<Branch>> branches = webClient.get()
                .uri("/repos/{username}/{repository}/branches", username, repository)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Branch.class)
                .collectList();
        return branches;
    }

}