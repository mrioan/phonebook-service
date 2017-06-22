CREATE TABLE IF NOT EXISTS USER (
  ID bigint identity primary key AUTO_INCREMENT,
  USERNAME VARCHAR(50) unique not null,
  PASSWORD VARCHAR(256) not null,
  ENABLED BOOL default true
);

CREATE TABLE IF NOT EXISTS CONTACT (
  USER_ID bigint not null,
  ID bigint identity primary key AUTO_INCREMENT,
  FIRST_NAME varchar(50) not null,
  LAST_NAME varchar(50),
  FOREIGN KEY (USER_ID) REFERENCES USER(ID)
);

create table IF NOT EXISTS PHONE (
  ID bigint identity primary key AUTO_INCREMENT,
  TYPE varchar(50),
  NUMBER varchar(50),
  CONTACT_ID bigint not null,
  FOREIGN KEY (CONTACT_ID) REFERENCES CONTACT(ID)
);

