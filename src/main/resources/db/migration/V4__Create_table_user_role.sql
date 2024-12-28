DROP TABLE IF EXISTS goit.user_role;

CREATE TABLE goit.user_role
(
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES userinfo(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);