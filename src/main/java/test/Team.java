package test;

import java.util.List;

public class Team {

    private final List<Player> players;

    public Team(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
