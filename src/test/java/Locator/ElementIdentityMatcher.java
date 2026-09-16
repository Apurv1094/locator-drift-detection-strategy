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
     * Finds the current DOM element that most closely
     * matches the old element profile.
     */
    public MatchResult findBestMatch(
            ElementProfile oldProfile,
            List<Element> currentElements) {

        List<MatchResult> results = new ArrayList<>();

        for (Element current : currentElements) {

            int score = 0;
            List<String> signals = new ArrayList<>();

            // -------------------------------------------------
            // 1. TAG
            // -------------------------------------------------
            if (same(
                    oldProfile.getTagName(),
                    current.tagName())) {

                score += 20;
                signals.add("tag");
            }

            // -------------------------------------------------
            // 2. ID
            // -------------------------------------------------
            if (same(
                    oldProfile.getId(),
                    current.attr("id"))) {

                score += 20;
                signals.add("id");
            }

            // -------------------------------------------------
            // 3. NAME
            // -------------------------------------------------
            if (same(
                    oldProfile.getName(),
                    current.attr("name"))) {

                score += 20;
                signals.add("name");
            }

            // -------------------------------------------------
            // 4. TYPE
            // -------------------------------------------------
            if (same(
                    oldProfile.getType(),
                    current.attr("type"))) {

                score += 10;
                signals.add("type");
            }

            // -------------------------------------------------
            // 5. PLACEHOLDER
            // -------------------------------------------------
            if (same(
                    oldProfile.getPlaceholder(),
                    current.attr("placeholder"))) {

                score += 15;
                signals.add("placeholder");
            }

            // -------------------------------------------------
            // 6. ARIA LABEL
            // -------------------------------------------------
            if (same(
                    oldProfile.getAriaLabel(),
                    current.attr("aria-label"))) {

                score += 15;
                signals.add("aria-label");
            }

            // -------------------------------------------------
            // 7. ROLE
            // -------------------------------------------------
            if (same(
                    oldProfile.getRole(),
                    current.attr("role"))) {

                score += 10;
                signals.add("role");
            }

            // -------------------------------------------------
            // 8. TEXT
            // -------------------------------------------------
            if (same(
                    oldProfile.getText(),
                    current.text())) {

                score += 10;
                signals.add("text");
            }

            // -------------------------------------------------
            // 9. CLASS
            // -------------------------------------------------
            if (classOverlap(
                    oldProfile.getClassName(),
                    current.attr("class"))) {

                score += 10;
                signals.add("class");
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