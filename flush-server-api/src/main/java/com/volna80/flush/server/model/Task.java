package com.volna80.flush.server.model;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class Task implements IMessage {

    private final String goal;

    public Task(String goal) {
        this.goal = goal;
    }

    public String getGoal() {
        return goal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return goal.equals(task.goal);

    }

    @Override
    public int hashCode() {
        return goal.hashCode();
    }

    @Override
    public String toString() {
        return "Task{" + goal + '}';
    }
}
