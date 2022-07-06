package com.java.example.tanzu.hungryman.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.java.example.tanzu.hungryman.model.Availability;
import com.java.example.tanzu.hungryman.repository.AvailabilityRepository;
import com.java.example.tanzu.hungryman.repository.AvailabilityWindowRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("availability")
@Slf4j
public class AvailabilityResource 
{
	protected AvailabilityRepository availRepo;
	
	protected AvailabilityWindowRepository availWindowRepo;
	
	/*
	 * Using setters for unit testing purposes
	 */
	@Autowired
	public void setAvailabilityRepository(AvailabilityRepository availRepo)
	{
		this.availRepo = availRepo;
	}
	
	@Autowired
	public void setAvailabilityWindowRepository(AvailabilityWindowRepository availWindowRepo)
	{
		this.availWindowRepo = availWindowRepo;
	}
	
	@GetMapping
	public Flux<Availability> getAllAvailabilty()
	{
		return getAvailabilityFromFlux(availRepo.findAll());
	}
	
	@GetMapping("{searchName}")
	public Flux<com.java.example.tanzu.hungryman.model.Availability> getSearchAvailabilty(@PathVariable("searchName") String searchName)
	{
		return getAvailabilityFromFlux(availRepo.findBySearchName(searchName));
	}
	
	protected Flux<Availability> getAvailabilityFromFlux(Flux<com.java.example.tanzu.hungryman.entity.Availability> flux)
	{
		return flux.flatMap(avail -> 
		   {
			   final var retAvail = new Availability();
			   retAvail.setSearchName(avail.getSearchName());
			   retAvail.setDiningName(avail.getDiningName());
			   retAvail.setAddress(avail.getAddress());
			   retAvail.setLocality(avail.getLocality());
			   retAvail.setPhoneNumber(avail.getPhoneNumber());
			   retAvail.setPostalCode(avail.getPostalCode());
			   retAvail.setRegion(avail.getRegion());
			   retAvail.setReservationURL(avail.getReservationURL());
			   
			   return availWindowRepo.findByAvailabilityId(avail.getId())
				  .switchIfEmpty(Mono.just(new com.java.example.tanzu.hungryman.entity.AvailabilityWindow(0L, 0L, 0L)))
				  .map(win ->
			      {
			    	 if (win.getId() != null)
			    	 {
				    	 var retWin = new Availability.AvailabilityWindow();
				    	 retWin.setStartTime(win.getStartTime());
				    	 retWin.setEndTime(win.getEndTime());
				    	 
				    	 retAvail.getAvailabilityWindows().add(retWin);
			    	 }
			    	 
				     return retAvail;
			      });
		   })
	 	   .onErrorResume(e -> { 
	 	    	log.error("Error getting availability.", e);
	 	    	return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
	 	   });
	}
}
