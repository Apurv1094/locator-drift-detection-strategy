package Locator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PageObjectLocatorExtractor {

    public List<LocatorDefinition> extract(Class<?> pageObjectClass) {

        List<LocatorDefinition> locators = new ArrayList<>();

        Field[] fields =
                pageObjectClass.getDeclaredFields();

        for (Field field : fields) {

            // Only process String fields
            if (field.getType() != String.class) {
                continue;
            }

            try {

                // Allow access to private fields
                field.setAccessible(true);

                Object value = field.get(null);

                if (value == null) {
                    continue;
                }

                String locatorValue =
                        value.toString();

                String locatorType =
                        detectLocatorType(locatorValue);

                LocatorDefinition definition =
                        new LocatorDefinition(
                                pageObjectClass.getSimpleName() + ".java",
                                field.getName(),
                                locatorType,
                                locatorValue
                        );

                locators.add(definition);

            } catch (Exception e) {

                System.out.println(
                        "Unable to extract locator from field: "
                                + field.getName()
                );
            }
        }

        return locators;
    }

    private String detectLocatorType(String locator) {

        if (locator.startsWith("//")
                || locator.startsWith("/")) {

            return "XPATH";
        }

        else {

            return "CSS";
        }

        //return "UNKNOWN";
    }
}