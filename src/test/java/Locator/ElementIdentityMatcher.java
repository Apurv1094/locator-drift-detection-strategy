package Locator;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ElementIdentityMatcher {

    public static class MatchResult {

        private final Element element;
        private final int score;
        private final List<String> matchedSignals;

        public MatchResult(
                Element element,
                int score,
                List<String> matchedSignals) {

            this.element = element;
            this.score = score;
            this.matchedSignals = matchedSignals;
        }

        public Element getElement() {
            return element;
        }

        public int getScore() {
            return score;
        }

        public List<String> getMatchedSignals() {
            return matchedSignals;
        }
    }

    /**
     * Compare an old element profile against all current DOM elements
     * and return the highest scoring candidate.
     */
    public MatchResult findBestMatch(
            ElementProfile oldProfile,
            List<Element> currentElements) {

        if (oldProfile == null
                || currentElements == null
                || currentElements.isEmpty()) {

            return null;
        }

        List<MatchResult> results =
                new ArrayList<>();

        for (Element current : currentElements) {

            int score = 0;

            List<String> signals =
                    new ArrayList<>();

            // -------------------------------------------------
            // TAG
            // Strong structural signal
            // -------------------------------------------------
            if (same(
                    oldProfile.getTagName(),
                    current.tagName())) {

                score += 20;
                signals.add("TAG");
            }

            // -------------------------------------------------
            // ID
            // Strong signal when stable
            // -------------------------------------------------
            if (same(
                    oldProfile.getId(),
                    current.attr("id"))) {

                score += 20;
                signals.add("ID");
            }

            // -------------------------------------------------
            // NAME
            // -------------------------------------------------
            if (same(
                    oldProfile.getName(),
                    current.attr("name"))) {

                score += 20;
                signals.add("NAME");
            }

            // -------------------------------------------------
            // TYPE
            // -------------------------------------------------
            if (same(
                    oldProfile.getType(),
                    current.attr("type"))) {

                score += 10;
                signals.add("TYPE");
            }

            // -------------------------------------------------
            // PLACEHOLDER
            // -------------------------------------------------
            if (same(
                    oldProfile.getPlaceholder(),
                    current.attr("placeholder"))) {

                score += 15;
                signals.add("PLACEHOLDER");
            }

            // -------------------------------------------------
            // ARIA LABEL
            // -------------------------------------------------
            if (same(
                    oldProfile.getAriaLabel(),
                    current.attr("aria-label"))) {

                score += 15;
                signals.add("ARIA-LABEL");
            }

            // -------------------------------------------------
            // ROLE
            // -------------------------------------------------
            if (same(
                    oldProfile.getRole(),
                    current.attr("role"))) {

                score += 10;
                signals.add("ROLE");
            }

            // -------------------------------------------------
            // TEXT
            // -------------------------------------------------
            if (same(
                    oldProfile.getText(),
                    current.text())) {

                score += 10;
                signals.add("TEXT");
            }

            // -------------------------------------------------
            // CLASS
            // -------------------------------------------------
            if (classOverlap(
                    oldProfile.getClassName(),
                    current.attr("class"))) {

                score += 10;
                signals.add("CLASS");
            }

            results.add(
                    new MatchResult(
                            current,
                            score,
                            signals
                    )
            );
        }

        return results.stream()
                .max(
                        Comparator.comparingInt(
                                MatchResult::getScore
                        )
                )
                .orElse(null);
    }

    /**
     * Exact case-insensitive comparison.
     */
    private boolean same(
            String oldValue,
            String currentValue) {

        if (isBlank(oldValue)
                || isBlank(currentValue)) {

            return false;
        }

        return oldValue.trim()
                .equalsIgnoreCase(
                        currentValue.trim()
                );
    }

    /**
     * Checks whether at least one CSS class
     * is shared between old and current element.
     */
    private boolean classOverlap(
            String oldClasses,
            String currentClasses) {

        if (isBlank(oldClasses)
                || isBlank(currentClasses)) {

            return false;
        }

        String[] oldTokens =
                oldClasses.trim().split("\\s+");

        String[] currentTokens =
                currentClasses.trim().split("\\s+");

        for (String oldToken : oldTokens) {

            for (String currentToken : currentTokens) {

                if (oldToken.equalsIgnoreCase(currentToken)) {

                    return true;
                }
            }
        }

        return false;
    }

    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }
}
