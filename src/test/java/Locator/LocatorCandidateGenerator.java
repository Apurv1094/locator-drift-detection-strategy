package Locator;

import java.util.ArrayList;
import java.util.List;

public class LocatorCandidateGenerator {

    /**
     * Generates possible CSS and XPath locators
     * from the supplied ElementProfile.
     */
    public List<String> generateCandidates(ElementProfile element) {

        List<String> candidates = new ArrayList<>();

        String tag = element.getTagName();
        String id = element.getId();
        String name = element.getName();
        String type = element.getType();
        String placeholder = element.getPlaceholder();
        String className = element.getClassName();
        String role = element.getRole();
        String ariaLabel = element.getAriaLabel();
        String text = element.getText();

        /*
         * =========================
         * CSS LOCATORS
         * =========================
         */

        // ID
        if (!isEmpty(id)) {
            candidates.add("#" + cssEscape(id));
        }

        // Name
        if (!isEmpty(name)) {
            candidates.add(
                    tag + "[name='" + cssEscape(name) + "']"
            );
        }

        // Placeholder
        if (!isEmpty(placeholder)) {
            candidates.add(
                    tag + "[placeholder='" + cssEscape(placeholder) + "']"
            );
        }

        // Type
        if (!isEmpty(type)) {
            candidates.add(
                    tag + "[type='" + cssEscape(type) + "']"
            );
        }

        // Role
        if (!isEmpty(role)) {
            candidates.add(
                    tag + "[role='" + cssEscape(role) + "']"
            );
        }

        // Aria label
        if (!isEmpty(ariaLabel)) {
            candidates.add(
                    tag + "[aria-label='" + cssEscape(ariaLabel) + "']"
            );
        }

        // Class
        if (!isEmpty(className)) {

            String[] classes = className.trim().split("\\s+");

            StringBuilder cssClass = new StringBuilder(tag);

            for (String cssClassName : classes) {

                if (!cssClassName.isEmpty()) {
                    cssClass.append(".")
                            .append(cssEscapeClass(cssClassName));
                }
            }

            candidates.add(cssClass.toString());
        }

        /*
         * =========================
         * COMBINATION CSS LOCATORS
         * =========================
         */

        if (!isEmpty(name) && !isEmpty(type)) {

            candidates.add(
                    tag +
                            "[name='" + cssEscape(name) + "']" +
                            "[type='" + cssEscape(type) + "']"
            );
        }

        if (!isEmpty(name) && !isEmpty(placeholder)) {

            candidates.add(
                    tag +
                            "[name='" + cssEscape(name) + "']" +
                            "[placeholder='" + cssEscape(placeholder) + "']"
            );
        }

        /*
         * =========================
         * XPATH LOCATORS
         * =========================
         */

        // ID
        if (!isEmpty(id)) {

            candidates.add(
                    "//" + tag +
                            "[@id=" + xpathValue(id) + "]"
            );
        }

        // Name
        if (!isEmpty(name)) {

            candidates.add(
                    "//" + tag +
                            "[@name=" + xpathValue(name) + "]"
            );
        }

        // Placeholder
        if (!isEmpty(placeholder)) {

            candidates.add(
                    "//" + tag +
                            "[@placeholder=" + xpathValue(placeholder) + "]"
            );
        }

        // Type
        if (!isEmpty(type)) {

            candidates.add(
                    "//" + tag +
                            "[@type=" + xpathValue(type) + "]"
            );
        }

        // Role
        if (!isEmpty(role)) {

            candidates.add(
                    "//" + tag +
                            "[@role=" + xpathValue(role) + "]"
            );
        }

        // Aria label
        if (!isEmpty(ariaLabel)) {

            candidates.add(
                    "//" + tag +
                            "[@aria-label=" + xpathValue(ariaLabel) + "]"
            );
        }

        // Text
        if (!isEmpty(text)) {

            candidates.add(
                    "//" + tag +
                            "[normalize-space()=" + xpathValue(text) + "]"
            );
        }

        /*
         * =========================
         * COMBINATION XPATH
         * =========================
         */

        if (!isEmpty(name) && !isEmpty(type)) {

            candidates.add(
                    "//" + tag +
                            "[@name=" + xpathValue(name) +
                            " and @type=" + xpathValue(type) + "]"
            );
        }

        if (!isEmpty(name) && !isEmpty(placeholder)) {

            candidates.add(
                    "//" + tag +
                            "[@name=" + xpathValue(name) +
                            " and @placeholder=" +
                            xpathValue(placeholder) + "]"
            );
        }

        return removeDuplicates(candidates);
    }


    /**
     * Removes duplicate locator candidates.
     */
    private List<String> removeDuplicates(List<String> candidates) {

        return new ArrayList<>(
                candidates.stream().distinct().toList()
        );
    }


    /**
     * Checks whether a value is null or empty.
     */
    private boolean isEmpty(String value) {

        return value == null || value.trim().isEmpty();
    }


    /**
     * Basic CSS attribute escaping.
     */
    private String cssEscape(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'");
    }


    /**
     * Escapes CSS class names.
     */
    private String cssEscapeClass(String value) {

        return value
                .replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace(":", "\\:");
    }


    /**
     * Creates a safe XPath string literal.
     *
     * Handles values containing:
     * - single quotes
     * - double quotes
     * - both
     */
    private String xpathValue(String value) {

        if (!value.contains("'")) {

            return "'" + value + "'";

        } else if (!value.contains("\"")) {

            return "\"" + value + "\"";

        } else {

            String[] parts = value.split("'");

            StringBuilder result =
                    new StringBuilder("concat(");

            for (int i = 0; i < parts.length; i++) {

                if (i > 0) {
                    result.append(", \"'\", ");
                }

                result.append("'")
                        .append(parts[i])
                        .append("'");
            }

            result.append(")");

            return result.toString();
        }
    }
}