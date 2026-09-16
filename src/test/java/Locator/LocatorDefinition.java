package Locator;

public class LocatorDefinition {

    private final String pageObjectFile;
    private final String variableName;
    private final String locatorType;
    private final String locatorValue;

    public LocatorDefinition(
            String pageObjectFile,
            String variableName,
            String locatorType,
            String locatorValue) {

        this.pageObjectFile = pageObjectFile;
        this.variableName = variableName;
        this.locatorType = locatorType;
        this.locatorValue = locatorValue;
    }

    public String getPageObjectFile() {
        return pageObjectFile;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getLocatorType() {
        return locatorType;
    }

    public String getLocatorValue() {
        return locatorValue;
    }
}