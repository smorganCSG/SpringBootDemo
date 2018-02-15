CREATE TABLE svd_user (
  id INT ,
  email VARCHAR(512) NOT NULL,
  password VARCHAR(512) NOT NULL,
  name VARCHAR(128),
  status VARCHAR(32) NOT NULL
);