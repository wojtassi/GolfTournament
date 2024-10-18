package org.example;

import org.example.controller.TournamentController;
import org.example.listeners.SubParListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TournamentControllerTest {

    @Test
    public void testTournamentCreation() {
        // Arrange
        TournamentController tournamentController = new TournamentController();
        String tournamentName = "Test Tournament";
        List<Integer> parsPerHole = List.of(4,5,3,4,5,4,4,3,4,4,4,4,4,5,4,3,5,3);
        SubParListenerTest subParListerTest = new SubParListenerTest();

        // Act
        tournamentController.initializeTournament(tournamentName, parsPerHole);
        tournamentController.subscribeToLeaderBoardEvents(subParListerTest);
        List<String> headerHole = new ArrayList<>();
        headerHole.add("Hole");
        headerHole.addAll(IntStream.rangeClosed(1, 18).boxed().map(String::valueOf).collect(Collectors.toUnmodifiableList()));
        headerHole.add("Final Score");
        List<String> headerPar = new ArrayList<>();
        headerPar.add("Par");
        headerPar.addAll(parsPerHole.stream().map(String::valueOf).collect(Collectors.toUnmodifiableList()));
        List<List<String>> expectedResults = new ArrayList<>(List.of(headerHole, headerPar));
        Assertions.assertEquals(expectedResults, tournamentController.getLeaderBoard());

        tournamentController.addGolferScoreForHole("player1", 1, 5);
        tournamentController.addGolferScoreForHole("player2", 1, 7);
        tournamentController.addGolferScoreForHole("player3", 1, 4);
        tournamentController.addGolferScoreForHole("player4", 1, 6);

        expectedResults.add(List.of("player3","0","","","","","","","","","","","","","","","","",""));
        expectedResults.add(List.of("player1","1","","","","","","","","","","","","","","","","",""));
        expectedResults.add(List.of("player4","2","","","","","","","","","","","","","","","","",""));
        expectedResults.add(List.of("player2","3","","","","","","","","","","","","","","","","",""));
        Assertions.assertEquals(expectedResults, tournamentController.getLeaderBoard());
        tournamentController.addGolferScoreForHole("player1", 2, 5);
        tournamentController.addGolferScoreForHole("player2", 2, 7);
        tournamentController.addGolferScoreForHole("player3", 2, 4);
        tournamentController.addGolferScoreForHole("player4", 2, 6);
        expectedResults.set(2, new ArrayList<>(List.of("player3","0","-1","","","","","","","","","","","","","","","","")));
        expectedResults.set(3, new ArrayList<>(List.of("player1","1","0","","","","","","","","","","","","","","","","")));
        expectedResults.set(4, new ArrayList<>(List.of("player4","2","1","","","","","","","","","","","","","","","","")));
        expectedResults.set(5, new ArrayList<>(List.of("player2","3","2","","","","","","","","","","","","","","","","")));
        Assertions.assertEquals(expectedResults, tournamentController.getLeaderBoard());
        Assertions.assertEquals("player3\t\t Hole: 2\t\tScore: -1 Scored Sub Par", subParListerTest.getEventsReceived().get(0));
        subParListerTest.reset();
        //Too much work to write out all the holes so instead we will verify them by computing them.
        expectedResults.set(2, new ArrayList<>(List.of("player1","1","0","","","","","","","","","","","","","","","","")));
        expectedResults.set(3, new ArrayList<>(List.of("player2","3","2","","","","","","","","","","","","","","","","")));
        expectedResults.set(4, new ArrayList<>(List.of("player3","0","-1","","","","","","","","","","","","","","","","")));
        expectedResults.set(5, new ArrayList<>(List.of("player4","2","1","","","","","","","","","","","","","","","","")));

        for (int i = 3; i <= 18; i++) {
            expectedResults.get(2).set(i, String.valueOf(addGolferScoreForHole(tournamentController, parsPerHole.get(i-1), "player1", i)));
            expectedResults.get(3).set(i, String.valueOf(addGolferScoreForHole(tournamentController, parsPerHole.get(i-1), "player2", i)));
            expectedResults.get(4).set(i, String.valueOf(addGolferScoreForHole(tournamentController, parsPerHole.get(i-1), "player3", i)));
            expectedResults.get(5).set(i, String.valueOf(addGolferScoreForHole(tournamentController, parsPerHole.get(i-1), "player4", i)));

        }
        //Now add final score
        expectedResults.get(2).add(Integer.toString(expectedResults.get(2).subList(1, expectedResults.get(2).size()).stream().mapToInt(v -> Integer.parseInt(v)).sum()));
        expectedResults.get(3).add(Integer.toString(expectedResults.get(3).subList(1, expectedResults.get(3).size()).stream().mapToInt(v -> Integer.parseInt(v)).sum()));
        expectedResults.get(4).add(Integer.toString(expectedResults.get(4).subList(1, expectedResults.get(4).size()).stream().mapToInt(v -> Integer.parseInt(v)).sum()));
        expectedResults.get(5).add(Integer.toString(expectedResults.get(5).subList(1, expectedResults.get(5).size()).stream().mapToInt(v -> Integer.parseInt(v)).sum()));
        List<List<String>> finalExpectedResult = expectedResults.subList(0,2);
        expectedResults.subList(2,6).stream().sorted(Comparator.comparingInt(l -> Integer.parseInt(l.get(l.size() - 1)))).forEach(finalExpectedResult::add);

        Assertions.assertEquals(finalExpectedResult, tournamentController.getLeaderBoard());
        //Assert that we have sub par events (birdies)
        Assertions.assertTrue(subParListerTest.getEventsReceived().size() > 0);
        //verify that we have final score once all games are played
        tournamentController.getLeaderBoard().forEach(g -> System.out.println(g));
    }

    @Test
    public void testTooManyHoles() {
        TournamentController tournamentController = new TournamentController();
        String tournamentName = "Test Tournament";
        List<Integer> parsPerHole = IntStream.rangeClosed(1, 19).boxed().collect(Collectors.toUnmodifiableList());
        Assertions.assertThrows(IllegalArgumentException.class, () -> tournamentController.initializeTournament(tournamentName, parsPerHole));
    }

    private int addGolferScoreForHole(TournamentController tournamentController, int parsPerHole, String player1, int hole) {
        int score = ThreadLocalRandom.current().nextInt(parsPerHole - 2, parsPerHole + 5);
        tournamentController.addGolferScoreForHole(player1, hole, score);
        return score - parsPerHole;
    }

    public class SubParListenerTest implements SubParListener {
        List<String> eventsReceived = new ArrayList<>();

        @Override
        public void subParPlay(String golferName, int hole, int score) {
            eventsReceived.add(golferName + "\t\t Hole: " + hole + "\t\tScore: " + score + " Scored Sub Par");
        }

        public void reset() {
            eventsReceived.clear();
        }

        public List<String> getEventsReceived() {
            return eventsReceived;
        }
    }
}
