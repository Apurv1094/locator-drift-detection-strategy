package Locator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class DomComparator {

    public DomComparisonResult compare(
            String oldHtml,
            String currentHtml) {

        Document oldDocument = Jsoup.parse(oldHtml);
        Document currentDocument = Jsoup.parse(currentHtml);

        List<Element> oldElements = meaningfulElements(oldDocument);
        List<Element> currentElements = meaningfulElements(currentDocument);

        DomComparisonResult result = new DomComparisonResult();

        result.setOldElementCount(oldElements.size());
        result.setCurrentElementCount(currentElements.size());

        boolean[] currentMatched =
                new boolean[currentElements.size()];

        for (Element oldElement : oldElements) {

            int bestIndex = -1;
            int bestScore = 0;

            for (int i = 0; i < currentElements.size(); i++) {

                if (currentMatched[i]) {
                    continue;
                }

                int score =
                        similarity(
                                oldElement,
                                currentElements.get(i)
                        );

                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }

            if (bestIndex == -1 || bestScore < 45) {

                result.setRemovedCount(
                        result.getRemovedCount() + 1
                );

                result.addChange(
                        "REMOVED: " +
                                describe(oldElement)
                );

                continue;
            }

            Element currentElement =
                    currentElements.get(bestIndex);

            currentMatched[bestIndex] = true;

            if (isEquivalent(oldElement, currentElement)) {

                result.setUnchangedCount(
                        result.getUnchangedCount() + 1
                );

            } else {

                result.setChangedCount(
                        result.getChangedCount() + 1
                );

                result.addChange(
                        buildChange(
                                oldElement,
                                currentElement,
                                bestScore
                        )
                );
            }
        }

        for (int i = 0; i < currentElements.size(); i++) {

            if (!currentMatched[i]) {

                result.setAddedCount(
                        result.getAddedCount() + 1
                );

                result.addChange(
                        "ADDED: " +
                                describe(currentElements.get(i))
                );
            }
        }

        return result;
    }

    private List<Element> meaningfulElements(Document document) {

        Elements elements =
                document.select(
                        "input, button, textarea, select, " +
                                "a, img, label, [role]"
                );

        List<Element> result = new ArrayList<>();

        for (Element element : elements) {

            if ("input".equals(element.tagName())
                    && "hidden".equalsIgnoreCase(
                    element.attr("type"))) {

                continue;
            }

            result.add(element);
        }

        return result;
    }

    private int similarity(
            Element oldElement,
            Element currentElement) {

        int score = 0;

        if (oldElement.tagName()
                .equalsIgnoreCase(currentElement.tagName())) {

            score += 20;
        }

        score += attributeSimilarity(
                oldElement,
                currentElement,
                "id",
                25
        );

        score += attributeSimilarity(
                oldElement,
                currentElement,
                "name",
                20
        );

        score += attributeSimilarity(
                oldElement,
                currentElement,
                "placeholder",
                20
        );

        score += attributeSimilarity(
                oldElement,
                currentElement,
                "type",
                10
        );

        score += attributeSimilarity(
                oldElement,
                currentElement,
                "aria-label",
                15
        );

        score += attributeSimilarity(
                oldElement,
                currentElement,
                "role",
                15
        );

        String oldText =
                normalize(oldElement.text());

        String currentText =
                normalize(currentElement.text());

        if (!oldText.isEmpty()
                && oldText.equals(currentText)) {

            score += 20;
        }

        return Math.min(score, 100);
    }

    private int attributeSimilarity(
            Element oldElement,
            Element currentElement,
            String attribute,
            int weight) {

        String oldValue =
                normalize(oldElement.attr(attribute));

        String currentValue =
                normalize(currentElement.attr(attribute));

        if (oldValue.isEmpty()
                || currentValue.isEmpty()) {

            return 0;
        }

        if (oldValue.equals(currentValue)) {
            return weight;
        }

        if (similar(oldValue, currentValue)) {
            return weight / 2;
        }

        return 0;
    }

    private boolean similar(
            String first,
            String second) {

        if (first.equals(second)) {
            return true;
        }

        if (first.contains(second)
                || second.contains(first)) {

            return true;
        }

        return levenshteinSimilarity(
                first,
                second
        ) >= 0.70;
    }

    private double levenshteinSimilarity(
            String first,
            String second) {

        int distance =
                levenshtein(first, second);

        int max =
                Math.max(first.length(), second.length());

        if (max == 0) {
            return 1.0;
        }

        return 1.0 -
                ((double) distance / max);
    }

    private int levenshtein(
            String first,
            String second) {

        int[][] matrix =
                new int[first.length() + 1]
                        [second.length() + 1];

        for (int i = 0; i <= first.length(); i++) {
            matrix[i][0] = i;
        }

        for (int j = 0; j <= second.length(); j++) {
            matrix[0][j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {

            for (int j = 1; j <= second.length(); j++) {

                int cost =
                        first.charAt(i - 1)
                                == second.charAt(j - 1)
                                ? 0
                                : 1;

                matrix[i][j] =
                        Math.min(
                                Math.min(
                                        matrix[i - 1][j] + 1,
                                        matrix[i][j - 1] + 1
                                ),
                                matrix[i - 1][j - 1] + cost
                        );
            }
        }

        return matrix[first.length()][second.length()];
    }

    private boolean isEquivalent(
            Element oldElement,
            Element currentElement) {

        return oldElement.tagName()
                .equalsIgnoreCase(
                        currentElement.tagName()
                )
                && normalize(
                oldElement.attr("name")
        ).equals(
                normalize(
                        currentElement.attr("name")
                )
        )
                && normalize(
                oldElement.attr("placeholder")
        ).equals(
                normalize(
                        currentElement.attr("placeholder")
                )
        )
                && normalize(
                oldElement.text()
        ).equals(
                normalize(
                        currentElement.text()
                )
        );
    }

    private String buildChange(
            Element oldElement,
            Element currentElement,
            int score) {

        return "CHANGED: "
                + describe(oldElement)
                + " -> "
                + describe(currentElement)
                + " | Match score="
                + score;
    }

    private String describe(Element element) {

        StringBuilder builder =
                new StringBuilder();

        builder.append("<")
                .append(element.tagName());

        appendAttribute(
                builder,
                element,
                "id"
        );

        appendAttribute(
                builder,
                element,
                "name"
        );

        appendAttribute(
                builder,
                element,
                "type"
        );

        appendAttribute(
                builder,
                element,
                "placeholder"
        );

        appendAttribute(
                builder,
                element,
                "class"
        );

        builder.append(">");

        String text =
                normalize(element.text());

        if (!text.isEmpty()) {

            builder.append(" text='")
                    .append(text)
                    .append("'");
        }

        builder.append("</")
                .append(element.tagName())
                .append(">");

        return builder.toString();
    }

    private void appendAttribute(
            StringBuilder builder,
            Element element,
            String attribute) {

        String value =
                element.attr(attribute);

        if (!value.isEmpty()) {

            builder.append(" ")
                    .append(attribute)
                    .append("='")
                    .append(value)
                    .append("'");
        }
    }

    private String normalize(String value) {

        return value == null
                ? ""
                : value.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }
}