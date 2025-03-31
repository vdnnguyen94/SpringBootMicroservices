CREATE DATABASE HotelDB;
USE HotelDB;

-- Hotel table
CREATE TABLE Hotel (
    hotelId VARCHAR(8) PRIMARY KEY,
    hotelName VARCHAR(50) NOT NULL,
    starRating INT CHECK (starRating BETWEEN 1 AND 5)
);

-- Staff table
CREATE TABLE Staff (
    staffId INT AUTO_INCREMENT PRIMARY KEY,
    staffName VARCHAR(50) NOT NULL,
    department ENUM('Reception', 'Cleaning', 'Management', 'Restaurant'),
    performanceRating INT CHECK (performanceRating BETWEEN 1 AND 5),
    hotelId VARCHAR(8),
    FOREIGN KEY (hotelId) REFERENCES Hotel(hotelId)
);

INSERT INTO Hotel (hotelId, hotelName, starRating) VALUES
('Axyz1001', 'Best Western', 3),
('Axyz1002', 'Holiday Inn', 4),
('Axyz1003', 'Hilton', 5);

INSERT INTO Staff (staffName, department, performanceRating, hotelId) VALUES
('Van Nguyen', 'Management', 3, 'Axyz1002'),
('Seyeon Jo', 'Reception', 3, 'Axyz1001');

Select * from Hotel;
Select * from Staff;

USE HotelDB;
CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    fullName VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL
);
