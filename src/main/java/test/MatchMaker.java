package test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class MatchMaker {

    private final Set<Player> players;

    private MatchMaker(Set<Player> players) {
        this.players = players;
    }

    public static MatchMaker from(Set<Player> players) {
        return new MatchMaker(players);
    }

    public Optional<Match> pull() {
        Map<Integer, List<Player>> skillToPlayersMapping = groupBySkill();

        List<List<Player>> descSortedPlayers = convertToListWithBiggestGroupsFirst(skillToPlayersMapping);

        Optional<List<Player>> optionalPlayersForMatch = searchPlayersForMatch(descSortedPlayers);

        return optionalPlayersForMatch.flatMap(this::getMatch);
    }

    private Map<Integer, List<Player>> groupBySkill() {
        return players.stream().collect(groupingBy(Player::getSkill));
    }

    private List<List<Player>> convertToListWithBiggestGroupsFirst(Map<Integer, List<Player>> skillToPlayersMapping) {
        return skillToPlayersMapping.values().stream()
                .sorted(Comparator.comparing(List::size, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    // O(n^2) computational complexity
    private Optional<List<Player>> searchPlayersForMatch(List<List<Player>> descSortedPlayersBySkill) {

        List<Player> result = new ArrayList<>(12);

        for (int i = 0; i < descSortedPlayersBySkill.size() - 1; i++) {
            List<Player> sameSkillPlayers = descSortedPlayersBySkill.get(i);
            Player player = sameSkillPlayers.get(0);

            result.addAll(sameSkillPlayers);

            if (result.size() < Constants.PLAYERS_AMOUNT) {
                for (int j = i + 1; j < descSortedPlayersBySkill.size(); j++) {
                    if (result.size() >= Constants.PLAYERS_AMOUNT) {
                        return Optional.of(removeOverage(result));
                    }

                    Player currentPlayer = descSortedPlayersBySkill.get(j).get(0);

                    if (Math.abs(player.getSkill() - currentPlayer.getSkill()) <= Constants.THRESHOLD) {
                        result.addAll(descSortedPlayersBySkill.get(j));
                    }
                }
            }

            if (result.size() < Constants.PLAYERS_AMOUNT) {
                result.clear();
            }

        }

        if (result.size() >= Constants.PLAYERS_AMOUNT) {
            return Optional.of(removeOverage(result));
        }

        return Optional.empty();
    }

    private List<Player> removeOverage(List<Player> list) {
        if (players.size() > Constants.PLAYERS_AMOUNT) {
            return list.subList(0, Constants.PLAYERS_AMOUNT);
        }
        return list;
    }

    private Optional<Match> getMatch(List<Player> players) {
        players.sort(Comparator.comparing(Player::getSkill));

        List<Player> teamA = new ArrayList<>(Constants.TEAM_SIZE);
        List<Player> teamB = new ArrayList<>(Constants.TEAM_SIZE);

        for (int i = 0; i < players.size(); i++) {
            if (isEven(i)) {
                teamA.add(players.get(i));
            } else {
                teamB.add(players.get(i));
            }
        }

        return Optional.of(new Match(new Team(teamA), new Team(teamB)));
    }

    private boolean isEven(int i) {
        return i % 2 == 0;
    }

}
