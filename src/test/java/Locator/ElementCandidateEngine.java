package Locator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ElementCandidateEngine {

    /**
     * Finds elements in the CURRENT DOM that most closely
     * match the OLD element profile.
     */
    public List<ElementCandidate> findCandidates(
            ElementProfile oldProfile,
            String currentHtml) {

        List<ElementCandidate> candidates =
                new ArrayList<>();

        if (oldProfile == null
                || currentHtml == null
                || currentHtml.isBlank()) {

            return candidates;
        }

        Document document =
                Jsoup.parse(currentHtml);

        List<Element> elements =
                getMeaningfulElements(document);

        for (Element element : elements) {

            int score =
                    calculateScore(
                            oldProfile,
                            element
                    );

            if (score > 0) {

                candidates.add(
                        new ElementCandidate(
                                element,
                                score
                        )
                );
            }
        }

        candidates.sort(
                Comparator
                        .comparingInt(
                                ElementCandidate::getScore
                        )
                        .reversed()
        );

        return candidates;
    }

    /**
     * We focus on elements that can reasonably be
     * interacted with or identified by a locator.
     *
     * Hidden inputs are deliberately ignored.
     */
    private List<Element> getMeaningfulElements(
            Document document) {

        List<Element> elements =
                new ArrayList<>();

        String[] selectors = {
                "input",
                "button",
                "textarea",
                "select",
                "a"
        };

        for (String selector : selectors) {

            for (Element element :
                    document.select(selector)) {

                if ("hidden".equalsIgnoreCase(
                        element.attr("type"))) {

                    continue;
                }

                elements.add(element);
            }
        }

        return elements;
    }

    private int calculateScore(
            ElementProfile oldProfile,
            Element currentElement) {

        int score = 0;

        /*
         * TAG
         */
        if (same(
                oldProfile.getTagName(),
                currentElement.tagName())) {

            score += 20;
        }

        /*
         * NAME
         */
        if (same(
                oldProfile.getName(),
                currentElement.attr("name"))) {

            score += 30;
        }

        /*
         * PLACEHOLDER
         */
        if (same(
                oldProfile.getPlaceholder(),
                currentElement.attr("placeholder"))) {

            score += 25;
        }

        /*
         * TYPE
         */
        if (same(
                oldProfile.getType(),
                currentElement.attr("type"))) {

            score += 15;
        }

        /*
         * ID
         */
        if (same(
                oldProfile.getId(),
                currentElement.attr("id"))) {

            score += 10;
        }

        /*
         * ROLE
         */
        if (same(
                oldProfile.getRole(),
                currentElement.attr("role"))) {

            score += 15;
        }

        /*
         * ARIA LABEL
         */
        if (same(
                oldProfile.getAriaLabel(),
                currentElement.attr("aria-label"))) {

            score += 25;
        }

        /*
         * TEXT
         */
        if (sameText(
                oldProfile.getText(),
                currentElement.text())) {

            score += 20;
        }

        /*
         * CLASS
         *
         * Low weight intentionally.
         * Classes are often changed by the application.
         */
        if (classOverlap(
                oldProfile.getClassName(),
                currentElement.attr("class"))) {

            score += 5;
        }

        return score;
    }

    private boolean same(
            String oldValue,
            String currentValue) {

        if (isBlank(oldValue)
                || isBlank(currentValue)) {

            return false;
        }

        return oldValue
                .trim()
                .equalsIgnoreCase(
                        currentValue.trim()
                );
    }

    private boolean sameText(
            String oldText,
            String currentText) {

        if (isBlank(oldText)
                || isBlank(currentText)) {

            return false;
        }

        return normalize(oldText)
                .equalsIgnoreCase(
                        normalize(currentText)
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
                oldClasses
                        .trim()
                        .split("\\s+");

        String[] currentTokens =
                currentClasses
                        .trim()
                        .split("\\s+");

        for (String oldToken : oldTokens) {

            for (String currentToken : currentTokens) {

                if (oldToken.equalsIgnoreCase(
                        currentToken)) {

                    return true;
                }
            }
        }

        return false;
    }

    private String normalize(
            String value) {

        return value
                .trim()
                .replaceAll("\\s+", " ");
    }

    private boolean isBlank(
            String value) {

        return value == null
                || value.isBlank();
    }
}