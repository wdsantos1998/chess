CREATE TABLE IF NOT EXISTS auth_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    authToken VARCHAR(100),
    username VARCHAR(100)
    );