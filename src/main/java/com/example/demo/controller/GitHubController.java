package com.example.demo.controller;

import com.example.demo.model.Repository;
import com.example.demo.service.GitHubAPIService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/apiv1")
public class GitHubController {

    private GitHubAPIService gitHubAPIService;

    public GitHubController(GitHubAPIService gitHubAPIService) {
        this.gitHubAPIService = gitHubAPIService;
    }

    @GetMapping(value = "/repositories/{username}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Flux<Repository> getRepositories(@PathVariable String username, @RequestHeader("Accept") String acceptHeader) {
        return gitHubAPIService.getUserRepositories(username, acceptHeader);
    }
}

