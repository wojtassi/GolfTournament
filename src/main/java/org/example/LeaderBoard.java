package org.example;

import org.example.model.GolferScore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeaderBoard {

    private final Tournament tournament;

    public LeaderBoard(Tournament tournament) {
        this.tournament = tournament;
    }

    public List<List<String>> getLeaderBoard() {
        List<List<String>> leaderBoard = new ArrayList<>();
        List<String> headerHole = new ArrayList<>();
        headerHole.add("Hole");
        headerHole.addAll(IntStream.rangeClosed(1, 18).boxed().map(String::valueOf).collect(Collectors.toUnmodifiableList()));
        headerHole.add("Final Score");
        leaderBoard.add(headerHole);
        List<String> headerPar = new ArrayList<>();
        headerPar.add("Par");
        headerPar.addAll(tournament.getGolfCourse().getHolePars().stream().map(Object::toString).toList());
        leaderBoard.add(headerPar);
        for (GolferScore golferScore : tournament.getGolfersByScore()) {
            List<String> row = new ArrayList<>();
            row.add(golferScore.getName());
            row.addAll(golferScore.getScorePerHole().stream().map(s -> s == null ? "" : s).map(Object::toString).toList());
            if (golferScore.getHolesCompleted() >= 18)
                row.add(String.valueOf(golferScore.getScore()));
            leaderBoard.add(row);
        }
        return leaderBoard;
    }
}
