package test;

public class Match {

    private final Team team1;
    private final Team team2;

    public Match(Team team1, Team team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    public int getTeam1SkillSum() {
        return team1.getPlayers().stream()
                .map(Player::getSkill)
                .reduce(0, Integer::sum);
    }

    public int getTeam2SkillSum() {
        return team2.getPlayers().stream()
                .map(Player::getSkill)
                .reduce(0, Integer::sum);
    }
}
