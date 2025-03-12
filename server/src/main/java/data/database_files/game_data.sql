CREATE TABLE IF NOT EXISTS game_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    gameID INT UNIQUE,
    whiteUsername VARCHAR(100),
    blackUsername VARCHAR(100),
    gameName VARCHAR(100),
    game JSON
    );