package com.java.example.tanzu.hungryman.model;

import lombok.Data;

@Data
public class Search 
{
	private Long id;

	private String name;	
	
	private long startTime;
	
	private long endTime;
	
	private String diningTypes;
	
	private String diningNames;
	
	private String postalCode;
	
	private int radius;
	
	private boolean continousSearch;
	
	private String requestSubject;
}
