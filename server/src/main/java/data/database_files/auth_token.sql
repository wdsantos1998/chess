CREATE TABLE IF NOT EXISTS auth_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    authToken VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci,
    username VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci
    );