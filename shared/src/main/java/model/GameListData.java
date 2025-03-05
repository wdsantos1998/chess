package model;

public record GameListData(int gameID, String whiteUsername, String blackUsername, String gameName) {

    public GameListData withWhiteUsername(String newWhiteUsername) {
        return new GameListData(this.gameID, newWhiteUsername, this.blackUsername, this.gameName);
    }

    public GameListData withBlackUsername(String newBlackUsername) {
        return new GameListData(this.gameID, this.whiteUsername, newBlackUsername, this.gameName);
    }
}
