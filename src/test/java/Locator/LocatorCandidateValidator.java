        package Locator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocatorCandidateValidator {

    private final WebDriver driver;

    public LocatorCandidateValidator(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Validates, scores and ranks locator candidates.
     */
    public List<CandidateResult> evaluateCandidates(
            List<String> candidates) {

        List<CandidateResult> results =
                new ArrayList<>();

        for (String candidate : candidates) {

            CandidateResult result =
                    evaluateCandidate(candidate);

            results.add(result);
        }

        // Highest score first
        results.sort(
                Comparator.comparingInt(
                        CandidateResult::getScore
                ).reversed()
        );

        return results;
    }

    private CandidateResult evaluateCandidate(
            String candidate) {

        try {

            By by = createBy(candidate);

            int elementCount =
                    driver.findElements(by).size();

            boolean matches =
                    elementCount > 0;

            int score =
                    calculateScore(
                            candidate,
                            elementCount
                    );

            return new CandidateResult(
                    candidate,
                    matches,
                    elementCount,
                    score
            );

        } catch (Exception e) {

            return new CandidateResult(
                    candidate,
                    false,
                    0,
                    0
            );
        }
    }

    /**
     * Converts the generated locator into Selenium By.
     */
    private By createBy(String locator) {

        locator = locator.trim();

        if (locator.startsWith("//")
                || locator.startsWith("(//")) {

            return By.xpath(locator);
        }

        return By.cssSelector(locator);
    }

    /**
     * Scores a locator based on:
     *
     * 1. Does it match?
     * 2. Does it uniquely identify one element?
     * 3. What type of locator is it?
     */
    private int calculateScore(
            String locator,
            int elementCount) {

        if (elementCount == 0) {
            return 0;
        }

        // Multiple elements = weak locator
        if (elementCount > 1) {

            return 40;
        }

        // Unique locator
        int score = 70;

        String lower =
                locator.toLowerCase();

        // Strong locator types
        if (lower.startsWith("#")) {

            score = 100;

        } else if (lower.contains("[id=")) {

            score = 100;

        } else if (lower.contains("[name=")) {

            score = 95;

        } else if (lower.contains("[placeholder=")) {

            score = 90;

        } else if (lower.contains("[aria-label=")) {

            score = 90;

        } else if (lower.contains("[role=")) {

            score = 85;

        } else if (lower.contains("[type=")) {

            score = 80;

        } else if (lower.contains("normalize-space")) {

            score = 85;

        } else if (lower.contains(".")) {

            // Class-based selector
            score = 60;
        }

        return score;
    }
}
