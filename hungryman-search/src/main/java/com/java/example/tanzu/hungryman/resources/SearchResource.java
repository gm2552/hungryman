package com.java.example.tanzu.hungryman.resources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.java.example.tanzu.hungryman.entity.Search;
import com.java.example.tanzu.hungryman.repository.SearchRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("search")
@Slf4j
public class SearchResource 
{
	protected static final String UNKNOWN_REQUEST_SUBJECT_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
	
	protected static final String STREAM_BRIDGE_OUTPUT_CHANNLE = "submittedSearches-out-0";
	
	protected SearchRepository searchRepo;
	
	protected StreamBridge streamBridge;
	
	@Autowired
	public void setSearchRepo(SearchRepository searchRepo)
	{
		this.searchRepo = searchRepo;
	}
	
	@Autowired
	public void setStreamBridge(StreamBridge streamBridge)
	{
		this.streamBridge = streamBridge;
	}
	
	protected String getRequestSubject()
	{
		var auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null)
		{
			return auth.getPrincipal().toString();
		}
		
		return UNKNOWN_REQUEST_SUBJECT_ID;
	}
	
	@GetMapping
	public Flux<Search> getAllSearches()
	{
		final var reqSub = getRequestSubject();
		
		return searchRepo.findByRequestSubject(reqSub)
    	   .onErrorResume(e -> { 
    	    	log.error("Error getting searches.", e);
    	    	return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    	   });
	}
	
	@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)  
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Search> addSearch(@RequestBody Search search)
	{
		log.info("Adding new search {} in zip code {} and radius {}", search.getName(), search.getPostalCode(), search.getRadius());
	
		final var reqSub = getRequestSubject();
		
		final var curTime = System.currentTimeMillis();
		
		// If the requested end time has passed, then return a bad request
		if (search.getEndTime() < curTime)
		{
			log.error("The search end time has already passed.");
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
		}
		
		search.setRequestSubject(reqSub);
		
		return searchRepo.findByNameIgnoreCaseAndRequestSubject(search.getName(), reqSub)
		.switchIfEmpty(Mono.just(new Search()))
		.flatMap(foundSearch -> 
		{
			if (StringUtils.hasText(foundSearch.getName()))
			{
				log.error("Search name {} already exists.");
				return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT));
			}
				
			return searchRepo.save(search)
			           .doOnSuccess(addedSearch -> streamBridge.send(STREAM_BRIDGE_OUTPUT_CHANNLE, addedSearch))
			    	   .onErrorResume(e -> { 
			    	    	log.error("Error adding search.", e);
			    	    	return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
			    	   });
		});
    	   
	}
	
	@DeleteMapping("{id}") 
	public Mono<Void> deleteSearch(@PathVariable("id") Long id)
	{
		log.info("Deleting search id {}", id);
		
		return searchRepo.deleteById(id)
    	   .onErrorResume(e -> { 
    	    	log.error("Error deleting search.", e);
    	    	return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    	   });
	}
}
