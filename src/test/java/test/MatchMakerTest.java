package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
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
    For the sake of simplicity we take just int for player id (not hexadecimal value like 0x123A89)
     */
    private Player generatePlayer(int id) {
        return new Player(id, random.nextInt(1000));
    }

    @Test
    void compose() {
        Optional<Match> optionalMatch = target.pull();
        assertTrue(optionalMatch.isPresent());

        Match match = optionalMatch.get();

        int teamsDelta = Math.abs(match.getTeam1SkillSum() - match.getTeam2SkillSum());

        assertTrue(teamsDelta < 20);
    }
}