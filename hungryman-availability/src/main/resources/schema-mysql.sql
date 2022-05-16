CREATE TABLE IF NOT EXISTS availability ( id bigint(20) NOT NULL AUTO_INCREMENT,  searchName VARCHAR(255) NOT NULL, diningName VARCHAR(255) NOT NULL,
    address VARCHAR(255), locality VARCHAR(255), region VARCHAR(255), postalCode VARCHAR(255), phoneNumber VARCHAR(255), reservationURL VARCHAR(1024),
    PRIMARY KEY (`id`), 
    UNIQUE KEY `avail_search_dining_name` (`searchName`, `diningName`));
    
CREATE TABLE IF NOT EXISTS availability_window ( id bigint(20) NOT NULL AUTO_INCREMENT, availabilityId bigint(20) NOT NULL, startTime bigint(20) NOT NULL, 
    endTime bigint(20) NOT NULL,  
    PRIMARY KEY (`id`));