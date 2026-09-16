package Locator;

public class CandidateResult {

    private final String locator;
    private final boolean matches;
    private final int elementCount;
    private final int score;

    public CandidateResult(
            String locator,
            boolean matches,
            int elementCount,
            int score) {

        this.locator = locator;
        this.matches = matches;
        this.elementCount = elementCount;
        this.score = score;
    }

    public String getLocator() {
        return locator;
    }

    public boolean isMatches() {
        return matches;
    }

    public int getElementCount() {
        return elementCount;
    }

    public int getScore() {
        return score;
    }
}

