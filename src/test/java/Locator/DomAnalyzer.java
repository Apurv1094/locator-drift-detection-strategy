package Locator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class DomAnalyzer {

    private final Document document;

    public DomAnalyzer(String pageSource) {
        this.document = Jsoup.parse(pageSource);
    }

    public List<Element> getInteractiveElements() {

        List<Element> elements = new ArrayList<>();

        elements.addAll(document.select("input"));
        elements.addAll(document.select("button"));
        elements.addAll(document.select("a"));
        elements.addAll(document.select("textarea"));
        elements.addAll(document.select("select"));

        return elements;
    }

    public List<ElementProfile> getElementProfiles() {

        List<ElementProfile> profiles = new ArrayList<>();

        for (Element element : getInteractiveElements()) {

            // Ignore hidden inputs
            if (element.tagName().equals("input")
                    && element.attr("type").equalsIgnoreCase("hidden")) {
                continue;
            }

            ElementProfile profile = new ElementProfile(

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

            profiles.add(profile);
        }

        return profiles;
    }

    public void printElementInventory() {

        List<ElementProfile> profiles =
                getElementProfiles();

        System.out.println(
                "\n========== DOM ELEMENT INVENTORY =========="
        );

        for (ElementProfile profile : profiles) {

            System.out.println("-------------------------------------------");

            System.out.println(
                    "Tag        : " + profile.getTagName()
            );

            System.out.println(
                    "ID         : " + profile.getId()
            );

            System.out.println(
                    "Name       : " + profile.getName()
            );

            System.out.println(
                    "Type       : " + profile.getType()
            );

            System.out.println(
                    "Placeholder: " + profile.getPlaceholder()
            );

            System.out.println(
                    "Class      : " + profile.getClassName()
            );

            System.out.println(
                    "Role       : " + profile.getRole()
            );

            System.out.println(
                    "Aria-label : " + profile.getAriaLabel()
            );

            System.out.println(
                    "Text       : " + profile.getText()
            );
        }

        System.out.println("-------------------------------------------");

        System.out.println(
                "Total meaningful elements: "
                        + profiles.size()
        );

        System.out.println(
                "===========================================\n"
        );
    }
}

