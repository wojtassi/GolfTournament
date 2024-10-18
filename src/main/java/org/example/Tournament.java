package org.example;

import org.example.listeners.SubParListener;
import org.example.model.GolfCourse;
import org.example.model.GolferScore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Tournament {
    private String name;

    final GolfCourse golfCourse;
    final ConcurrentHashMap<String, GolferScore> golferScores = new ConcurrentHashMap<>();

    int holesPlayed = 0;

    final List<SubParListener> subParListeners = new ArrayList<>();

    public Tournament(String name, List<Integer> holePars) {
        this.name = name;
        this.golfCourse = new GolfCourse(holePars);
    }

    public String getName() {
        return name;
    }

    public void addGolferScoreForHole(String golferName, int hole, int score) {
        golferScores.compute(golferName, (n, g) -> {
            if (g == null) {
                g = new GolferScore(n, golfCourse.getTotalHoles());
            }
            int scoreForHole = calculateScoreForHole(hole, score);
            if (scoreForHole < 0) {
                subParListeners.forEach(l -> l.subParPlay(golferName, hole, scoreForHole));
            }
            g.addScore(hole, calculateScoreForHole(hole, score));
            return g;
        });
        holesPlayed = Math.max(holesPlayed, hole);
    }

    private int calculateScoreForHole(int holeNumber, int strokes) {
        int par = golfCourse.getParForHole(holeNumber);
        int score = strokes - par;
        return score;
    }

    public List<GolferScore> getGolfersByScore() {
        return golferScores.values().stream().sorted().toList();
    }

    public void addSubParListener(SubParListener subParListener) {
        subParListeners.add(subParListener);
    }

    public GolfCourse getGolfCourse() {
        return golfCourse;
    }
}
