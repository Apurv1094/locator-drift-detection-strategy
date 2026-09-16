package Locator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocatorRecoveryEngine {

    private final WebDriver driver;
    private final DomAnalyzer domAnalyzer;
    private final LocatorCandidateGenerator candidateGenerator;
    private final LocatorCandidateValidator candidateValidator;

    public LocatorRecoveryEngine(WebDriver driver) {

        this.driver = driver;

        this.domAnalyzer =
                new DomAnalyzer(driver.getPageSource());

        this.candidateGenerator =
                new LocatorCandidateGenerator();

        this.candidateValidator =
                new LocatorCandidateValidator(driver);
    }

    /**
     * Checks a locator and attempts recovery
     * if the locator is broken.
     */
    public void processLocator(
            LocatorDefinition locatorDefinition) {

        String locator =
                locatorDefinition.getLocatorValue();

        String locatorType =
                locatorDefinition.getLocatorType();

        System.out.println(
                "\n=========================================="
        );

        System.out.println(
                "Page Object : "
                        + locatorDefinition.getPageObjectFile()
        );

        System.out.println(
                "Variable    : "
                        + locatorDefinition.getVariableName()
        );

        System.out.println(
                "Type        : "
                        + locatorType
        );

        System.out.println(
                "Locator     : "
                        + locator
        );

        // Step 1: Check existing locator
        if (isLocatorWorking(
                locator,
                locatorType)) {

            System.out.println(
                    "Status      : HEALTHY"
            );

            System.out.println(
                    "No recovery required."
            );

            return;
        }

        // Step 2: Locator is broken
        System.out.println(
                "Status      : BROKEN"
        );

        // Step 3: Extract hint from old locator
        LocatorHint hint =
                extractLocatorHint(
                        locator,
                        locatorType
                );

        if (hint == null) {

            System.out.println(
                    "Unable to analyze old locator."
            );

            return;
        }

        // Step 4: Find matching DOM element
        ElementProfile matchedElement =
                findBestMatch(hint);

        if (matchedElement == null) {

            System.out.println(
                    "No matching DOM element found."
            );

            return;
        }

        // Step 5: Print matched element
        System.out.println(
                "\nMatched Current DOM Element:"
        );

        printProfile(matchedElement);

        // Step 6: Generate replacement locators
        List<String> candidates =
                candidateGenerator.generateCandidates(
                        matchedElement
                );

        // Step 7: Evaluate candidates
        List<CandidateResult> results =
                candidateValidator.evaluateCandidates(
                        candidates
                );

        System.out.println(
                "\n========== CANDIDATE EVALUATION =========="
        );

        int count = 1;

        for (CandidateResult result : results) {

            System.out.println(
                    "\n" + count + ". "
                            + result.getLocator()
            );

            System.out.println(
                    "   Match    : "
                            + (result.isMatches()
                            ? "YES"
                            : "NO")
            );

            System.out.println(
                    "   Elements : "
                            + result.getElementCount()
            );

            System.out.println(
                    "   Score    : "
                            + result.getScore()
            );

            count++;
        }

        // Step 8: Select best candidate
        if (!results.isEmpty()) {

            CandidateResult bestCandidate =
                    results.get(0);

            if (bestCandidate.isMatches()
                    && bestCandidate.getElementCount() == 1) {

                System.out.println(
                        "\n========== RECOMMENDATION =========="
                );

                System.out.println(
                        "Recommended Locator : "
                                + bestCandidate.getLocator()
                );

                System.out.println(
                        "Confidence          : "
                                + bestCandidate.getScore()
                                + "%"
                );

            } else {

                System.out.println(
                        "\nNo reliable locator recommendation."
                );
            }
        }
    }


    /**
     * Checks whether the locator currently
     * exists on the page.
     */
    private boolean isLocatorWorking(
            String locator,
            String locatorType) {

        try {

            By by;

            if (locatorType.equalsIgnoreCase("XPATH")) {

                by = By.xpath(locator);

            } else if (
                    locatorType.equalsIgnoreCase("CSS")) {

                by = By.cssSelector(locator);

            } else {

                return false;
            }

            return !driver.findElements(by).isEmpty();

        } catch (Exception e) {

            return false;
        }
    }


    /**
     * Extracts tag, attribute and value from
     * common XPath and CSS locators.
     */
    private LocatorHint extractLocatorHint(
            String locator,
            String locatorType) {

        locator = locator.trim();

        // =========================
        // XPATH
        // =========================

        if (locatorType.equalsIgnoreCase("XPATH")) {

            /*
             * Examples:
             *
             * //input[@placeholder='User']
             * //input[@name='username']
             * //input[@type='password']
             * //button[@type='submit']
             */

            Pattern attributePattern =
                    Pattern.compile(
                            "//([a-zA-Z0-9_*.-]+)" +
                                    "\\[@([a-zA-Z0-9_:-]+)" +
                                    "\\s*=\\s*" +
                                    "['\"]([^'\"]+)['\"]\\]"
                    );

            Matcher matcher =
                    attributePattern.matcher(locator);

            if (matcher.find()) {

                return new LocatorHint(
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3)
                );
            }

            /*
             * Example:
             *
             * //button[normalize-space()='Login']
             */

            Pattern textPattern =
                    Pattern.compile(
                            "//([a-zA-Z0-9_-]+)" +
                                    "\\[normalize-space\\(\\)" +
                                    "\\s*=\\s*" +
                                    "['\"]([^'\"]+)['\"]\\]"
                    );

            matcher =
                    textPattern.matcher(locator);

            if (matcher.find()) {

                return new LocatorHint(
                        matcher.group(1),
                        "text",
                        matcher.group(2)
                );
            }
        }


        // =========================
        // CSS
        // =========================

        if (locatorType.equalsIgnoreCase("CSS")) {

            /*
             * Examples:
             *
             * input[name='password']
             * input[placeholder='Username']
             * button[type='submit']
             */

            Pattern cssPattern =
                    Pattern.compile(
                            "([a-zA-Z0-9_-]+)" +
                                    "\\[([a-zA-Z0-9_:-]+)" +
                                    "\\s*=\\s*" +
                                    "['\"]?([^\\]'\"\\s]+)" +
                                    "['\"]?\\]"
                    );

            Matcher matcher =
                    cssPattern.matcher(locator);

            if (matcher.find()) {

                return new LocatorHint(
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3)
                );
            }

            /*
             * CSS ID:
             *
             * #username
             */

            if (locator.startsWith("#")) {

                return new LocatorHint(
                        "*",
                        "id",
                        locator.substring(1)
                );
            }

            /*
             * CSS class:
             *
             * .username
             */

            if (locator.startsWith(".")) {

                return new LocatorHint(
                        "*",
                        "class",
                        locator.substring(1)
                );
            }
        }

        return null;
    }


    /**
     * Finds the most likely current DOM element.
     */
    private ElementProfile findBestMatch(
            LocatorHint hint) {

        List<ElementProfile> profiles =
                domAnalyzer.getElementProfiles();

        ElementProfile bestMatch = null;

        double bestScore = 0;

        for (ElementProfile profile : profiles) {

            double score =
                    calculateScore(
                            profile,
                            hint
                    );

            if (score > bestScore) {

                bestScore = score;
                bestMatch = profile;
            }
        }

        if (bestScore < 0.40) {

            return null;
        }

        System.out.println(
                "\nMatch Confidence: "
                        + String.format(
                        "%.2f",
                        bestScore
                )
        );

        return bestMatch;
    }


    /**
     * Calculates similarity between the
     * broken locator and current DOM element.
     */
    private double calculateScore(
            ElementProfile profile,
            LocatorHint hint) {

        double score = 0;

        // Tag match
        if (hint.tag.equals("*")
                || profile.getTagName()
                .equalsIgnoreCase(hint.tag)) {

            score += 0.30;
        }

        // Attribute exists
        if (matchesAttribute(
                profile,
                hint.attribute)) {

            score += 0.30;
        }

        // Compare old value against
        // the SAME current DOM attribute
        double similarity =
                calculateValueSimilarity(
                        profile,
                        hint.attribute,
                        hint.value
                );

        score += similarity * 0.40;

        return score;
    }


    /**
     * Checks whether the specified attribute
     * exists on the current DOM element.
     */
    private boolean matchesAttribute(
            ElementProfile profile,
            String attribute) {

        switch (attribute.toLowerCase()) {

            case "id":
                return !isEmpty(
                        profile.getId()
                );

            case "name":
                return !isEmpty(
                        profile.getName()
                );

            case "type":
                return !isEmpty(
                        profile.getType()
                );

            case "placeholder":
                return !isEmpty(
                        profile.getPlaceholder()
                );

            case "class":
                return !isEmpty(
                        profile.getClassName()
                );

            case "role":
                return !isEmpty(
                        profile.getRole()
                );

            case "aria-label":
                return !isEmpty(
                        profile.getAriaLabel()
                );

            case "text":
                return !isEmpty(
                        profile.getText()
                );

            default:
                return false;
        }
    }


    /**
     * Compares the old locator value
     * against the SAME attribute in the
     * current DOM element.
     */
    private double calculateValueSimilarity(
            ElementProfile profile,
            String attribute,
            String oldValue) {

        String currentValue;

        switch (attribute.toLowerCase()) {

            case "id":

                currentValue =
                        profile.getId();

                break;

            case "name":

                currentValue =
                        profile.getName();

                break;

            case "type":

                currentValue =
                        profile.getType();

                break;

            case "placeholder":

                currentValue =
                        profile.getPlaceholder();

                break;

            case "class":

                currentValue =
                        profile.getClassName();

                break;

            case "role":

                currentValue =
                        profile.getRole();

                break;

            case "aria-label":

                currentValue =
                        profile.getAriaLabel();

                break;

            case "text":

                currentValue =
                        profile.getText();

                break;

            default:

                currentValue = "";
        }

        return similarity(
                oldValue,
                currentValue
        );
    }


    /**
     * Simple fuzzy matching.
     */
    private double similarity(
            String oldValue,
            String currentValue) {

        if (isEmpty(oldValue)
                || isEmpty(currentValue)) {

            return 0;
        }

        String oldText =
                oldValue.trim().toLowerCase();

        String currentText =
                currentValue.trim().toLowerCase();

        // Exact match
        if (oldText.equals(currentText)) {

            return 1.0;
        }

        // Contains match
        if (currentText.contains(oldText)
                || oldText.contains(currentText)) {

            return 0.90;
        }

        // Levenshtein
        int distance =
                levenshteinDistance(
                        oldText,
                        currentText
                );

        int maxLength =
                Math.max(
                        oldText.length(),
                        currentText.length()
                );

        if (maxLength == 0) {

            return 0;
        }

        return 1.0 -
                ((double) distance / maxLength);
    }


    /**
     * Calculates Levenshtein distance.
     */
    private int levenshteinDistance(
            String first,
            String second) {

        int[][] matrix =
                new int[
                        first.length() + 1
                        ][
                        second.length() + 1
                        ];

        for (int i = 0;
             i <= first.length();
             i++) {

            matrix[i][0] = i;
        }

        for (int j = 0;
             j <= second.length();
             j++) {

            matrix[0][j] = j;
        }

        for (int i = 1;
             i <= first.length();
             i++) {

            for (int j = 1;
                 j <= second.length();
                 j++) {

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

        return matrix[
                first.length()
                ][
                second.length()
                ];
    }


    private boolean isEmpty(String value) {

        return value == null
                || value.trim().isEmpty();
    }


    /**
     * Prints the current DOM element profile.
     */
    private void printProfile(
            ElementProfile profile) {

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "Tag         : "
                        + profile.getTagName()
        );

        System.out.println(
                "ID          : "
                        + profile.getId()
        );

        System.out.println(
                "Name        : "
                        + profile.getName()
        );

        System.out.println(
                "Type        : "
                        + profile.getType()
        );

        System.out.println(
                "Placeholder : "
                        + profile.getPlaceholder()
        );

        System.out.println(
                "Class       : "
                        + profile.getClassName()
        );

        System.out.println(
                "Role        : "
                        + profile.getRole()
        );

        System.out.println(
                "Aria-label  : "
                        + profile.getAriaLabel()
        );

        System.out.println(
                "Text        : "
                        + profile.getText()
        );

        System.out.println(
                "------------------------------------------"
        );
    }


    /**
     * Internal representation of a locator.
     */
    private static class LocatorHint {

        private final String tag;
        private final String attribute;
        private final String value;

        private LocatorHint(
                String tag,
                String attribute,
                String value) {

            this.tag = tag;
            this.attribute = attribute;
            this.value = value;
        }
    }
}

