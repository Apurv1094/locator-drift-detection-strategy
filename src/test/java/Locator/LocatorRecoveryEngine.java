package Locator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LocatorRecoveryEngine {

    private final WebDriver driver;

    private final HistoricalElementResolver
            historicalElementResolver;

    private final ElementCandidateEngine
            candidateEngine;

    public LocatorRecoveryEngine(
            WebDriver driver) {

        this.driver = driver;

        this.historicalElementResolver =
                new HistoricalElementResolver();

        this.candidateEngine =
                new ElementCandidateEngine();
    }

    /**
     * Main recovery flow:
     *
     * CURRENT DOM
     *      ↓
     * locator broken?
     *      ↓
     * OLD DOM
     *      ↓
     * old element
     *      ↓
     * old profile
     *      ↓
     * CURRENT DOM
     *      ↓
     * matching element
     *      ↓
     * new locator
     */
    public void processLocator(
            LocatorDefinition locator,
            Path oldSnapshotPath,
            String currentHtml) {

        System.out.println(
                "\n========== LOCATOR RECOVERY =========="
        );

        System.out.println(
                "Variable: "
                        + locator.getVariableName()
        );

        System.out.println(
                "Old Locator: "
                        + locator.getLocatorValue()
        );

        /*
         * STEP 1
         * Check whether the locator still works
         * against the CURRENT application.
         */
        if (isValidCurrentLocator(locator)) {

            System.out.println(
                    "Status       : VALID"
            );

            System.out.println(
                    "Recovery     : NOT REQUIRED"
            );

            return;
        }

        System.out.println(
                "Status       : BROKEN"
        );

        /*
         * STEP 2
         * Locate the element in the OLD DOM.
         */
        System.out.println(
                "\n========== HISTORICAL ELEMENT =========="
        );

        Element oldElement =
                historicalElementResolver.findElement(
                        oldSnapshotPath,
                        locator.getLocatorType(),
                        locator.getLocatorValue()
                );

        if (oldElement == null) {

            System.out.println(
                    "Historical Element: NOT FOUND"
            );

            System.out.println(
                    "Recovery cannot continue."
            );

            System.out.println(
                    "Reason: The old locator does not "
                            + "identify an element in the baseline DOM."
            );

            return;
        }

        System.out.println(
                "Historical Element: FOUND"
        );

        /*
         * STEP 3
         * Create the OLD element profile.
         */
        ElementProfile oldProfile =
                historicalElementResolver
                        .createProfile(oldElement);

        System.out.println(
                "\nOLD ELEMENT PROFILE:"
        );

        System.out.println(
                oldProfile
        );

        /*
         * STEP 4
         * Find matching element in CURRENT DOM.
         */
        System.out.println(
                "\n========== CURRENT DOM MATCHING =========="
        );

        List<ElementCandidate> candidates =
                candidateEngine.findCandidates(
                        oldProfile,
                        currentHtml
                );

        if (candidates.isEmpty()) {

            System.out.println(
                    "No matching current element found."
            );

            return;
        }

        System.out.println(
                "Candidates found: "
                        + candidates.size()
        );

        printCandidates(candidates);

        /*
         * STEP 5
         * Use the highest scoring candidate.
         */
        ElementCandidate bestCandidate =
                candidates.get(0);

        Element recoveredElement =
                bestCandidate.getElement();

        System.out.println(
                "\n========== RECOVERED ELEMENT =========="
        );

        System.out.println(
                "Score: "
                        + bestCandidate.getScore()
        );

        System.out.println(
                recoveredElement
        );

        /*
         * STEP 6
         * Generate locator candidates.
         */
        System.out.println(
                "\n========== NEW LOCATOR CANDIDATES =========="
        );

        List<String> locatorCandidates =
                generateLocatorSuggestions(
                        recoveredElement
                );

        List<ValidatedLocator> validatedLocators =
                validateAndScoreLocators(
                        locatorCandidates,
                        currentHtml
                );

        printValidatedLocators(
                validatedLocators
        );

        /*
         * STEP 7
         * Recommend the highest ranked locator.
         */
        if (validatedLocators.isEmpty()) {

            System.out.println(
                    "\nRecommendation: "
                            + "No unique locator found."
            );

            return;
        }

        ValidatedLocator recommended =
                validatedLocators.get(0);

        System.out.println(
                "\n========== RECOMMENDATION =========="
        );

        System.out.println(
                "Recommended Locator Type: "
                        + recommended.type
        );

        System.out.println(
                "Recommended Locator: "
                        + recommended.value
        );

        System.out.println(
                "Matches: "
                        + recommended.matches
        );

        System.out.println(
                "Final Score: "
                        + recommended.score
        );
    }

    /**
     * Checks the locator against the CURRENT browser DOM.
     */
    private boolean isValidCurrentLocator(
            LocatorDefinition locator) {

        try {

            By by;

            if ("XPATH".equalsIgnoreCase(
                    locator.getLocatorType())) {

                by =
                        By.xpath(
                                locator.getLocatorValue()
                        );

            } else if ("CSS".equalsIgnoreCase(
                    locator.getLocatorType())) {

                by =
                        By.cssSelector(
                                locator.getLocatorValue()
                        );

            } else {

                return false;
            }

            return !driver
                    .findElements(by)
                    .isEmpty();

        } catch (Exception e) {

            return false;
        }
    }

    private void printCandidates(
            List<ElementCandidate> candidates) {

        int number = 1;

        for (ElementCandidate candidate :
                candidates) {

            System.out.println(
                    "\nCandidate #"
                            + number
            );

            System.out.println(
                    "Score: "
                            + candidate.getScore()
            );

            System.out.println(
                    "Element:"
            );

            System.out.println(
                    candidate.getElement()
            );

            number++;
        }
    }

    /**
     * Generates possible locators for the RECOVERED
     * CURRENT element.
     */
    private List<String> generateLocatorSuggestions(
            Element element) {

        List<String> suggestions =
                new ArrayList<>();

        String tag =
                element.tagName();

        String id =
                element.attr("id");

        String name =
                element.attr("name");

        String placeholder =
                element.attr("placeholder");

        String type =
                element.attr("type");

        String ariaLabel =
                element.attr("aria-label");

        String role =
                element.attr("role");

        String text =
                element.text().trim();

        /*
         * ID
         */
        if (!id.isEmpty()) {

            suggestions.add(
                    "CSS::#" + escapeCss(id)
            );
        }

        /*
         * NAME
         */
        if (!name.isEmpty()) {

            suggestions.add(
                    "CSS::"
                            + tag
                            + "[name='"
                            + escapeCss(name)
                            + "']"
            );
        }

        /*
         * NAME + TYPE
         */
        if (!name.isEmpty()
                && !type.isEmpty()) {

            suggestions.add(
                    "CSS::"
                            + tag
                            + "[name='"
                            + escapeCss(name)
                            + "'][type='"
                            + escapeCss(type)
                            + "']"
            );
        }

        /*
         * PLACEHOLDER
         */
        if (!placeholder.isEmpty()) {

            suggestions.add(
                    "CSS::"
                            + tag
                            + "[placeholder='"
                            + escapeCss(placeholder)
                            + "']"
            );
        }

        /*
         * NAME + PLACEHOLDER
         */
        if (!name.isEmpty()
                && !placeholder.isEmpty()) {

            suggestions.add(
                    "CSS::"
                            + tag
                            + "[name='"
                            + escapeCss(name)
                            + "'][placeholder='"
                            + escapeCss(placeholder)
                            + "']"
            );
        }

        /*
         * ARIA LABEL
         */
        if (!ariaLabel.isEmpty()) {

            suggestions.add(
                    "CSS::"
                            + tag
                            + "[aria-label='"
                            + escapeCss(ariaLabel)
                            + "']"
            );
        }

        /*
         * ROLE
         */
        if (!role.isEmpty()) {

            suggestions.add(
                    "CSS::"
                            + tag
                            + "[role='"
                            + escapeCss(role)
                            + "']"
            );
        }

        /*
         * TYPE
         */
        if (!type.isEmpty()) {

            suggestions.add(
                    "CSS::"
                            + tag
                            + "[type='"
                            + escapeCss(type)
                            + "']"
            );
        }

        /*
         * TEXT
         */
        if (!text.isEmpty()
                && text.length() < 80) {

            suggestions.add(
                    "XPATH:://"
                            + tag
                            + "[normalize-space()='"
                            + escapeXPath(text)
                            + "']"
            );
        }

        return removeDuplicates(
                suggestions
        );
    }

    /**
     * Validates every generated locator against
     * the CURRENT DOM and assigns a stability score.
     */
    private List<ValidatedLocator>
    validateAndScoreLocators(
            List<String> candidates,
            String currentHtml) {

        List<ValidatedLocator> validated =
                new ArrayList<>();

        for (String candidate :
                candidates) {

            String[] parts =
                    candidate.split(
                            "::",
                            2
                    );

            if (parts.length != 2) {
                continue;
            }

            String type =
                    parts[0];

            String value =
                    parts[1];

            int matches =
                    countMatches(
                            type,
                            value,
                            currentHtml
                    );

            /*
             * We only recommend a unique locator.
             */
            if (matches != 1) {
                continue;
            }

            int stability =
                    locatorStabilityScore(
                            value
                    );

            int finalScore =
                    stability + 30;

            validated.add(
                    new ValidatedLocator(
                            type,
                            value,
                            matches,
                            stability,
                            finalScore
                    )
            );
        }

        validated.sort(
                (a, b) ->
                        Integer.compare(
                                b.score,
                                a.score
                        )
        );

        return validated;
    }

    private int countMatches(
            String type,
            String locator,
            String html) {

        try {

            Document document =
                    Jsoup.parse(html);

            if ("CSS".equalsIgnoreCase(type)) {

                return document
                        .select(locator)
                        .size();
            }

            if ("XPATH".equalsIgnoreCase(type)) {

                return evaluateSimpleXPath(
                        document,
                        locator
                ).size();
            }

        } catch (Exception e) {

            System.out.println(
                    "Unable to validate locator: "
                            + locator
            );
        }

        return 0;
    }

    /**
     * Supports the simple text XPath generated
     * by this POC.
     */
    private Elements evaluateSimpleXPath(
            Document document,
            String xpath) {

        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile(
                        "//([a-zA-Z][a-zA-Z0-9_-]*)"
                                + "\\[normalize-space\\(\\)='([^']+)'\\]"
                );

        java.util.regex.Matcher matcher =
                pattern.matcher(xpath);

        if (!matcher.matches()) {

            return new Elements();
        }

        String tag =
                matcher.group(1);

        String text =
                matcher.group(2);

        Elements elements =
                document.select(tag);

        Elements matches =
                new Elements();

        for (Element element :
                elements) {

            if (element.text()
                    .trim()
                    .equals(text)) {

                matches.add(element);
            }
        }

        return matches;
    }

    private int locatorStabilityScore(
            String locator) {

        /*
         * Stable identity first.
         */
        if (locator.startsWith("#")) {
            return 100;
        }

        if (locator.contains("[name='")
                && locator.contains("[type='")) {

            return 95;
        }

        if (locator.contains("[name='")) {
            return 95;
        }

        if (locator.contains("[placeholder='")) {
            return 90;
        }

        if (locator.contains("[aria-label='")) {
            return 90;
        }

        if (locator.contains("[role='")) {
            return 85;
        }

        if (locator.contains("[type='")) {
            return 75;
        }

        if (locator.startsWith("//")) {
            return 70;
        }

        return 50;
    }

    private void printValidatedLocators(
            List<ValidatedLocator> locators) {

        System.out.println(
                "\n========== VALIDATED LOCATORS =========="
        );

        int number = 1;

        for (ValidatedLocator locator :
                locators) {

            System.out.println(
                    "\nCandidate #"
                            + number
            );

            System.out.println(
                    "Type: "
                            + locator.type
            );

            System.out.println(
                    "Locator: "
                            + locator.value
            );

            System.out.println(
                    "Matches: "
                            + locator.matches
            );

            System.out.println(
                    "Stability Score: "
                            + locator.stability
            );

            System.out.println(
                    "Final Score: "
                            + locator.score
            );

            number++;
        }
    }

    private List<String> removeDuplicates(
            List<String> values) {

        Set<String> unique =
                new LinkedHashSet<>(values);

        return new ArrayList<>(unique);
    }

    private String escapeCss(
            String value) {

        return value.replace(
                "'",
                "\\'"
        );
    }

    private String escapeXPath(
            String value) {

        return value.replace(
                "'",
                "’"
        );
    }

    /**
     * Small internal result object used only
     * for ranking validated locators.
     */
    private static class ValidatedLocator {

        private final String type;
        private final String value;
        private final int matches;
        private final int stability;
        private final int score;

        private ValidatedLocator(
                String type,
                String value,
                int matches,
                int stability,
                int score) {

            this.type = type;
            this.value = value;
            this.matches = matches;
            this.stability = stability;
            this.score = score;
        }
    }
}