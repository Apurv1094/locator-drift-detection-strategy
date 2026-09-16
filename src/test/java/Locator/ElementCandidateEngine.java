package Locator;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ElementCandidateEngine {

    private final DomAnalyzer domAnalyzer;

    public ElementCandidateEngine(DomAnalyzer domAnalyzer) {
        this.domAnalyzer = domAnalyzer;
    }

    public List<ElementCandidate> findCandidates(String variableName) {

        List<ElementCandidate> candidates = new ArrayList<>();

        List<Element> elements =
                domAnalyzer.getInteractiveElements();

        for (Element element : elements) {

            int score = calculateScore(variableName, element);

            if (score > 0) {
                candidates.add(
                        new ElementCandidate(element, score)
                );
            }
        }

        candidates.sort(
                Comparator.comparingInt(
                        ElementCandidate::getScore
                ).reversed()
        );

        return candidates;
    }

    private int calculateScore(
            String variableName,
            Element element) {

        int score = 0;

        String variable =
                variableName.toLowerCase();

        String ariaLabel =
                element.attr("aria-label").toLowerCase();

        String placeholder =
                element.attr("placeholder").toLowerCase();

        String name =
                element.attr("name").toLowerCase();

        String id =
                element.attr("id").toLowerCase();

        String text =
                element.text().toLowerCase();

        if (variable.contains("user")
                && (ariaLabel.contains("user")
                || placeholder.contains("user")
                || name.contains("user")
                || id.contains("user")
                || text.contains("user"))) {

            score += 40;
        }

        if (variable.contains("password")
                && (ariaLabel.contains("password")
                || placeholder.contains("password")
                || name.contains("password")
                || id.contains("password")
                || text.contains("password"))) {

            score += 40;
        }

        if (variable.contains("login")
                && element.tagName().equals("button")
                && text.contains("login")) {

            score += 40;
        }

        return score;
    }
}