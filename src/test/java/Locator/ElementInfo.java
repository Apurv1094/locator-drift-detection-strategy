package com.locatorhealth.model;

public class ElementInfo {

    private final String tagName;
    private final String id;
    private final String name;
    private final String className;
    private final String type;
    private final String placeholder;
    private final String text;
    private final String ariaLabel;
    private final String role;
    private final String value;

    public ElementInfo(
            String tagName,
            String id,
            String name,
            String className,
            String type,
            String placeholder,
            String text,
            String ariaLabel,
            String role,
            String value) {

        this.tagName = tagName;
        this.id = id;
        this.name = name;
        this.className = className;
        this.type = type;
        this.placeholder = placeholder;
        this.text = text;
        this.ariaLabel = ariaLabel;
        this.role = role;
        this.value = value;
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

    public String getClassName() {
        return className;
    }

    public String getType() {
        return type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getText() {
        return text;
    }

    public String getAriaLabel() {
        return ariaLabel;
    }

    public String getRole() {
        return role;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {

        return "ElementInfo{" +
                "tagName='" + tagName + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", type='" + type + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", text='" + text + '\'' +
                ", ariaLabel='" + ariaLabel + '\'' +
                ", role='" + role + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}