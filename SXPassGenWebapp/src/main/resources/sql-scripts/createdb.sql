CREATE SCHEMA SX;
SET SCHEMA SX;

CREATE TABLE app_role (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  description varchar(255) DEFAULT NULL,
  role_name varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE app_user (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  username varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE app_setting (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  value varchar(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user_role (
  user_id bigint(20) NOT NULL,
  role_id bigint(20) NOT NULL,
  CONSTRAINT FK859n2jvi8ivhui0rl0esws6o FOREIGN KEY (user_id) REFERENCES app_user (id),
  CONSTRAINT FKa68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES app_role (id)
);

CREATE TABLE notification (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  user_id bigint(20) NOT NULL,
  message varchar(200) NOT NULL,
  severity varchar(20) NOT NULL,
  CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES app_user (id),
  PRIMARY KEY (id)
);

CREATE TABLE application (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  application varchar(200) NOT NULL,
  gmt_offset bigint(20) NOT NULL,
  key varchar(200) NOT NULL,
  seed varchar(200),
  type varchar(10) NOT NULL,
  user_name varchar(200) NOT NULL,
  PRIMARY KEY (id)
);

INSERT INTO app_role (id, role_name, description) VALUES (1, 'STANDARD_USER', 'Standard User - Has no admin rights');
INSERT INTO app_role (id, role_name, description) VALUES (2, 'ADMIN_USER', 'Admin User - Has permission to perform admin tasks');

INSERT INTO app_user (id, first_name, last_name, password, username) VALUES (1, 'Admin', 'Admin', '$2a$10$qtH0F1m488673KwgAfFXEOWxsoZSeHqqlB/8BTt3a6gsI5c2mdlfe', 'admin');

INSERT INTO user_role(user_id, role_id) VALUES(1,2);
