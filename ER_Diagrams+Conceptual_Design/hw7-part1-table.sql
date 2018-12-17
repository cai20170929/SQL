.table
PRAGMA foreign_keys=ON;

DROP TABLE IF EXISTS  NonProfessionalDriver;
DROP TABLE IF EXISTS  Truck;
DROP TABLE IF EXISTS  ProfessionalDriver;
DROP TABLE IF EXISTS  Drives;
DROP TABLE IF EXISTS  Car;
DROP TABLE IF EXISTS  Vehicle;
DROP TABLE IF EXISTS  Driver;
DROP TABLE IF EXISTS  InsuranceCo;
DROP TABLE IF EXISTS  Person;

.table

create table Person(
    ssn int primary key,
    name varchar(100)
);

create table InsuranceCo(
    name varchar(100) primary key,
    phone int
);

create table Driver(
    driverID int,
    ssn int primary key,
    foreign key (ssn) references Person(ssn)
);

create table Vehicle(
    licensePlate varchar(100) primary key,
    year int,
    maxLiability real,
    name varchar(100) references InsuranceCo(name),
    ssn int references Person(ssn)
);

create table Car(
    make varchar(100),
    licensePlate varchar(100) primary key,
    foreign key (licensePlate) references Vehicle(licensePlate)
);

create table Drives(
    licensePlate varchar(100),
    ssn int,
    primary key (licensePlate, ssn),
    foreign key (licensePlate) references Vehicle(licensePlate),
    foreign key (ssn) references Person(ssn)
);

create table ProfessionalDriver(
    medicalHistory varchar(100),
    ssn int primary key,
    foreign key (ssn) references Person(ssn)
);

create table Truck(
    capacity int,
    ssn int references Person(ssn),
    licensePlate varchar(100) primary key,
    foreign key (licensePlate) references Vehicle(licensePlate)
);

create table NonProfessionalDriver(
    ssn int primary key,
    foreign key (ssn) references Person(ssn)
);

.table
