package org.example.controller;

import org.example.LeaderBoard;
import org.example.Tournament;
import org.example.listeners.SubParListener;

import java.util.List;

public class TournamentController {

    Tournament tournament;
    LeaderBoard leaderBoard;

    public TournamentController() {

    }

    public void initializeTournament(String name, List<Integer> holePars) {
        tournament = new Tournament(name, holePars);
        leaderBoard = new LeaderBoard(tournament);
    }

    public void addGolferScoreForHole(String golferName, int hole, int score) {
        tournament.addGolferScoreForHole(golferName, hole, score);
    }

    public List<List<String>> getLeaderBoard() {
        return leaderBoard.getLeaderBoard();
    }

    public void subscribeToLeaderBoardEvents(SubParListener subParListener) {
        tournament.addSubParListener(subParListener);
    }
}
