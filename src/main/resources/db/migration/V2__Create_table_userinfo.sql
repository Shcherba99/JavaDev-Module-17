DROP TABLE IF EXISTS goit.userinfo;

CREATE TABLE goit.userinfo
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOL DEFAULT true
);

