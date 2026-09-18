package Locator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HistoricalElementResolver {

    /**
     * Finds the element represented by the old locator
     * inside the historical DOM snapshot.
     *
     * This class ONLY works with the OLD DOM.
     *
     * @param snapshotPath  path to historical HTML
     * @param locatorType   XPATH or CSS
     * @param locatorValue  old locator value
     * @return historical element or null
     */
    public Element findElement(
            Path snapshotPath,
            String locatorType,
            String locatorValue) {

        if (snapshotPath == null
                || !Files.exists(snapshotPath)) {

            System.out.println(
                    "Historical snapshot not found: "
                            + snapshotPath
            );

            return null;
        }

        if (locatorValue == null
                || locatorValue.isBlank()) {

            return null;
        }

        try {

            String html =
                    Files.readString(snapshotPath);

            Document document =
                    Jsoup.parse(html);

            if ("CSS".equalsIgnoreCase(locatorType)) {

                return findByCss(
                        document,
                        locatorValue
                );
            }

            if ("XPATH".equalsIgnoreCase(locatorType)) {

                return findByXPath(
                        document,
                        locatorValue
                );
            }

            System.out.println(
                    "Unsupported locator type: "
                            + locatorType
            );

            return null;

        } catch (IOException e) {

            System.out.println(
                    "Unable to read historical DOM: "
                            + e.getMessage()
            );

            return null;

        } catch (Exception e) {

            System.out.println(
                    "Unable to resolve historical element: "
                            + e.getMessage()
            );

            return null;
        }
    }

    private Element findByCss(
            Document document,
            String locator) {

        try {

            List<Element> elements =
                    document.select(locator);

            if (elements.size() == 1) {

                return elements.get(0);
            }

            if (elements.isEmpty()) {

                System.out.println(
                        "Old CSS locator matched 0 elements: "
                                + locator
                );

            } else {

                System.out.println(
                        "Old CSS locator matched "
                                + elements.size()
                                + " elements: "
                                + locator
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Invalid CSS locator: "
                            + locator
            );
        }

        return null;
    }

    /**
     * Supports the XPath patterns currently used by the POC:
     *
     * //input[@placeholder='Username']
     * //*[@placeholder='Username']
     * //input[@name='username']
     * //button[normalize-space()='Login']
     */
    private Element findByXPath(
            Document document,
            String locator) {

        String xpath =
                locator.trim();

        /*
         * //tag[@attribute='value']
         */
        if (xpath.matches(
                "//\\w+\\[@[\\w-]+='[^']+'\\]"
        )) {

            int tagEnd =
                    xpath.indexOf("[@");

            String tag =
                    xpath.substring(
                            2,
                            tagEnd
                    );

            int attributeStart =
                    xpath.indexOf("@") + 1;

            int equalsIndex =
                    xpath.indexOf("=");

            String attribute =
                    xpath.substring(
                            attributeStart,
                            equalsIndex
                    );

            String value =
                    extractQuotedValue(xpath);

            String css =
                    tag
                            + "["
                            + attribute
                            + "='"
                            + value
                            + "']";

            return findSingleCss(
                    document,
                    css
            );
        }

        /*
         * //*[@attribute='value']
         */
        if (xpath.matches(
                "//\\*\\[@[\\w-]+='[^']+'\\]"
        )) {

            int attributeStart =
                    xpath.indexOf("@") + 1;

            int equalsIndex =
                    xpath.indexOf("=");

            String attribute =
                    xpath.substring(
                            attributeStart,
                            equalsIndex
                    );

            String value =
                    extractQuotedValue(xpath);

            String css =
                    "["
                            + attribute
                            + "='"
                            + value
                            + "']";

            return findSingleCss(
                    document,
                    css
            );
        }

        /*
         * //tag[normalize-space()='text']
         */
        if (xpath.matches(
                "//\\w+\\[normalize-space\\(\\)='[^']+'\\]"
        )) {

            int tagEnd =
                    xpath.indexOf("[");

            String tag =
                    xpath.substring(
                            2,
                            tagEnd
                    );

            String value =
                    extractQuotedValue(xpath);

            List<Element> elements =
                    document.select(tag);

            Element matchedElement = null;

            for (Element element : elements) {

                if (element.text()
                        .trim()
                        .equalsIgnoreCase(value)) {

                    if (matchedElement != null) {

                        // More than one matching element.
                        // Do not guess.
                        return null;
                    }

                    matchedElement = element;
                }
            }

            return matchedElement;
        }

        System.out.println(
                "Unsupported XPath pattern: "
                        + locator
        );

        return null;
    }

    private Element findSingleCss(
            Document document,
            String css) {

        try {

            List<Element> elements =
                    document.select(css);

            if (elements.size() == 1) {

                return elements.get(0);
            }

        } catch (Exception e) {

            System.out.println(
                    "Invalid CSS generated from XPath: "
                            + css
            );
        }

        return null;
    }

    private String extractQuotedValue(
            String xpath) {

        int firstQuote =
                xpath.indexOf("'");

        int lastQuote =
                xpath.lastIndexOf("'");

        if (firstQuote >= 0
                && lastQuote > firstQuote) {

            return xpath.substring(
                    firstQuote + 1,
                    lastQuote
            );
        }

        return "";
    }

    /**
     * Converts the historical DOM element into
     * the ElementProfile used by the matcher.
     */
    public ElementProfile createProfile(
            Element element) {

        if (element == null) {

            return null;
        }

        return new ElementProfile(
                element.tagName(),
                element.attr("id"),
                element.attr("name"),
                element.attr("type"),
                element.attr("placeholder"),
                element.attr("class"),
                element.attr("role"),
                element.attr("aria-label"),
                element.text()
        );
    }
}