CREATE TABLE IF NOT EXISTS search ( id bigint(20) NOT NULL AUTO_INCREMENT,  name VARCHAR(255) NOT NULL, startTime bigint(20) NOT NULL, endTime bigint(20) NOT NULL, 
    diningTypes VARCHAR(1000), diningNames VARCHAR(1000), postalCode VARCHAR(12) NOT NULL, 
    radius smallint DEFAULT NULL, continousSearch bit(1), requestSubject VARCHAR(255) NOT NULL, 
    PRIMARY KEY (`id`),
    UNIQUE KEY `dining_search_name` (`name`, `requestSubject`));
