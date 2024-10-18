package org.example.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class GolferScore implements Comparable<GolferScore> {
    private String name;
    private int score;
    final private List<Integer> scorePerHole;
    private int holesCompleted;

    public GolferScore(String name, int numberOfHoles) {
        this.name = name;
        this.score = 0;
        scorePerHole = Collections.synchronizedList(new ArrayList<>(numberOfHoles));
        for(int i = 0; i < numberOfHoles; i++) {
            scorePerHole.add(null);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return scorePerHole.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }

    public List<Integer> getScorePerHole() {
        return scorePerHole;
    }

    public int getHolesCompleted() {
        return holesCompleted;
    }

    public void addScore(int hole, int score) {
        scorePerHole.set(hole - 1, score);
        this.score = scorePerHole.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        holesCompleted = Math.max(holesCompleted, hole);

    }

    @Override
    public int compareTo(GolferScore o) {
        return score - o.getScore();
    }
}
