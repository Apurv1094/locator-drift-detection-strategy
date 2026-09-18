package Locator;

import java.util.ArrayList;
import java.util.List;

public class DomComparisonResult {

    private int oldElementCount;
    private int currentElementCount;

    private int unchangedCount;
    private int changedCount;
    private int addedCount;
    private int removedCount;

    private final List<String> changes = new ArrayList<>();

    public int getOldElementCount() {
        return oldElementCount;
    }

    public void setOldElementCount(int oldElementCount) {
        this.oldElementCount = oldElementCount;
    }

    public int getCurrentElementCount() {
        return currentElementCount;
    }

    public void setCurrentElementCount(int currentElementCount) {
        this.currentElementCount = currentElementCount;
    }

    public int getUnchangedCount() {
        return unchangedCount;
    }

    public void setUnchangedCount(int unchangedCount) {
        this.unchangedCount = unchangedCount;
    }

    public int getChangedCount() {
        return changedCount;
    }

    public void setChangedCount(int changedCount) {
        this.changedCount = changedCount;
    }

    public int getAddedCount() {
        return addedCount;
    }

    public void setAddedCount(int addedCount) {
        this.addedCount = addedCount;
    }

    public int getRemovedCount() {
        return removedCount;
    }

    public void setRemovedCount(int removedCount) {
        this.removedCount = removedCount;
    }

    public List<String> getChanges() {
        return changes;
    }

    public void addChange(String change) {
        changes.add(change);
    }
}