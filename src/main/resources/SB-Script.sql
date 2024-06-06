create table city_master (city_id integer not null, city_name varchar(225), state_id integer, primary key (city_id)) engine=InnoDB;

create table country_master (country_id integer not null, country_name varchar(225), primary key (cuntry_id)) engine=InnoDB;

create table state_master (state_id integer not null, state_name varchar(225), country_id integer, primary key (state_id)) engine=InnoDB;

create table user_dtls (user_id integer not null auto_increment, created_date date, email varchar (225), name varchar(225),phno bigint, pwd varchar(225),pwd_updated varchar(225), updated_date date, city_id integer, country_id integer, state_id integer, primary key(user_id)) engine=InnoDB;

alter table city_master add constraint FKfxtjuwt9iqx9n7xl6f8wl6uu4 foreign key (state_id) references state_master(state_id)


alter table state_master add constraint FKfxtjuwt9iqx9n7xl6f8wl6uu4 foreign key (country_id) references country_master(country_id)


alter table user_dtls add constraint FKfxtjuwt9iqx9n7xl6f8wl6uu4 foreign key (city_id) references city_master(city_id)


alter table user_dtls add constraint FKfxtjuwt9iqx9n7xl6f8wl6uu4 foreign key (country_id) references conutry_master(country_id)


alter table user_dtls add constraint FKfxtjuwt9iqx9n7xl6f8wl6uu4 foreign key (state_id) references state_master(state_id)


insert into country_master values(1, 'India');
insert into country_master values(2, 'USA');

insert into state_master values(1, 'AP', 1);
insert into state_master values(2, 'TG', 1);

insert into state_master values(3, 'RI', 2);
insert into state_master values(4, 'NJ', 2);

insert into city_master values(1, 'Guntur', 1);
insert into city_master values(2, 'Ongole', 1);

insert into city_master values(3, 'Hyderabad', 2);
insert into city_master values(4, 'Warangal', 2);

insert into city_master values(5, 'Provindence', 3);
insert into city_master values(6, 'New Port', 3);

insert into city_master values(7, 'Jersey', 4);
insert into city_master values(8, 'Newark', 4);