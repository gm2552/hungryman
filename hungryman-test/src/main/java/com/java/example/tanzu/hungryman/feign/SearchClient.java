package com.java.example.tanzu.hungryman.feign;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.java.example.tanzu.hungryman.model.Search;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "${hungryman.search.service.name:hungryman-search}", url = "${hungryman.search.service.url:}")
public interface SearchClient 
{
	@GetMapping("/search")
	public Flux<Search> getAllSearches();
	
	@PutMapping(value="/search", consumes = MediaType.APPLICATION_JSON_VALUE)  
	public Mono<Search> addSearch(@RequestBody Search search);
	
	@DeleteMapping("/search/{id}") 
	public Mono<Void> deleteSearch(@PathVariable("id") Long id);
}
