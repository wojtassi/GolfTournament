package org.example.model;

import java.util.Collections;
import java.util.List;

public class GolfCourse {
    private final List<Integer> holePars;

    public GolfCourse(List<Integer> holePars) {
        validateCourse(holePars);
        this.holePars = Collections.unmodifiableList(holePars);
    }

    public void validateCourse(List<Integer> holePars) {
        if (holePars == null || holePars.isEmpty()) {
            throw new IllegalArgumentException("Hole pars cannot be null or empty");
        }
        if (holePars.stream().anyMatch(par -> par < 1)) {
            throw new IllegalArgumentException("Hole pars must be greater than 0");
        }
        if (holePars.stream().anyMatch(par -> par > 10)) {
            throw new IllegalArgumentException("This is a serious tournament");
        }
    }

    public Integer getParForHole(int holeNumber) {
        if (holeNumber < 1 || holeNumber > holePars.size()) {
            throw new IllegalArgumentException("Invalid hole number");
        }
        return holePars.get(holeNumber - 1);
    }

    public int getTotalHoles() {
        return holePars.size();
    }

    public List<Integer> getHolePars() {
        return holePars;
    }
}
