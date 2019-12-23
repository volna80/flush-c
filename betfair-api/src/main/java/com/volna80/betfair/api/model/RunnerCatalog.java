package com.volna80.betfair.api.model;

import java.util.Map;

/**
 * (c) All rights reserved
 *
 * @author nikolay.volnov@gmail.com
 */
public class RunnerCatalog implements Comparable<RunnerCatalog> {

    /**
     * The unique id for the selection.
     */
    private long selectionId;

    /**
     * The name of the runner
     */
    private String runnerName;

    private double handicap = Double.NaN;

    /**
     * The sort priority of this runner
     */
    private int sortPriority;

    /**
     * Metadata associated with the runner
     */
    private Map<String, String> metadata;

    public long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(long selectionId) {
        this.selectionId = selectionId;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public double getHandicap() {
        return handicap;
    }

    public void setHandicap(double handicap) {
        this.handicap = handicap;
    }

    public int getSortPriority() {
        return sortPriority;
    }

    public void setSortPriority(int sortPriority) {
        this.sortPriority = sortPriority;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public int compareTo(RunnerCatalog o) {
        return sortPriority - o.sortPriority;
    }

    @Override
    public String toString() {
        return "RunnerCatalog{" +
                "selectionId=" + selectionId +
                ", runnerName='" + runnerName + '\'' +
                ", handicap=" + handicap +
                ", sortPriority=" + sortPriority +
                ", metadata=" + metadata +
                '}';
    }
}
