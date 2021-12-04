package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Assume that we have normalized skill values that belongs to range 0..1000
 */
class MatchMakerTest {

    private final Random random = new Random(System.currentTimeMillis());
    private final int playersDownThreshold = 1000;
    private final int playersUpThreshold = 2000;

    private int playersAvailableNumber;
    private MatchMaker target;
    private Set<Player> players;

    @BeforeEach
    void setUp() {
        playersAvailableNumber = playersDownThreshold + random.nextInt(playersUpThreshold - playersDownThreshold);

        generatePlayers();

        target = MatchMaker.from(players);
    }

    private void generatePlayers() {
        players = Stream.iterate(0, i -> i < playersAvailableNumber, i -> i + 1)
                .map(this::generatePlayer)
                .collect(Collectors.toSet());
    }

    /*
    For the sake of simplicity we take just integer for player id (not hexadecimal value like 0x123A89)
     */
    private Player generatePlayer(int id) {
        return new Player(id, random.nextInt(1000));
    }

    @Test
    void compose() {
        Optional<Match> optionalMatch = target.pull();

        assertTrue(optionalMatch.isPresent());

        Match match = optionalMatch.get();

        verifyDeltaBetweenTeams(match);
        verifyDeltaBetweenPlayers(match);
    }

    private void verifyDeltaBetweenTeams(Match match) {
        int teamsDelta = Math.abs(match.getTeam1SkillSum() - match.getTeam2SkillSum());
        assertTrue(teamsDelta < Constants.THRESHOLD);
    }

    private void verifyDeltaBetweenPlayers(Match match) {
        List<Player> players = Stream.of(match.getTeam1().getPlayers(), match.getTeam2().getPlayers())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        for (int i = 0; i < players.size() - 1; i++) {
            for (int j = i + 1; j < players.size(); j++) {
                int delta = Math.abs(players.get(i).getSkill() - players.get(j).getSkill());
                assertTrue(delta < Constants.THRESHOLD);
            }
        }
    }
}