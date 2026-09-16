package Locator;

import org.jsoup.nodes.Element;

public class ElementCandidate {

    private final Element element;
    private final int score;

    public ElementCandidate(Element element, int score) {
        this.element = element;
        this.score = score;
    }

    public Element getElement() {
        return element;
    }

    public int getScore() {
        return score;
    }
}