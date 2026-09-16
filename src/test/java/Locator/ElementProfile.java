package Locator;

public class ElementProfile {

    private final String tagName;
    private final String id;
    private final String name;
    private final String type;
    private final String placeholder;
    private final String className;
    private final String role;
    private final String ariaLabel;
    private final String text;

    public ElementProfile(
            String tagName,
            String id,
            String name,
            String type,
            String placeholder,
            String className,
            String role,
            String ariaLabel,
            String text) {

        this.tagName = tagName;
        this.id = id;
        this.name = name;
        this.type = type;
        this.placeholder = placeholder;
        this.className = className;
        this.role = role;
        this.ariaLabel = ariaLabel;
        this.text = text;
    }

    public String getTagName() {
        return tagName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getClassName() {
        return className;
    }

    public String getRole() {
        return role;
    }

    public String getAriaLabel() {
        return ariaLabel;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {

        return "ElementProfile{" +
                "tagName='" + tagName + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", className='" + className + '\'' +
                ", role='" + role + '\'' +
                ", ariaLabel='" + ariaLabel + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
