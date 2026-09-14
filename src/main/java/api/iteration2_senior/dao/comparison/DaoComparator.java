package api.iteration2_senior.dao.comparison;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class DaoComparator {
    private final DaoComparisonConfigLoader configLoader;

    public DaoComparator() {
        this.configLoader = new DaoComparisonConfigLoader("dao-comparison.properties");
    }

    public void compare(Object apiResponse, Object dao) {
        DaoComparisonConfigLoader.DaoComparisonRule rule = configLoader.getRuleFor(apiResponse.getClass());

        if (rule == null) {
            throw new RuntimeException("No comparison rule found for " + apiResponse.getClass().getSimpleName());
        }

        Map<String, String> fieldMappings = rule.getFieldMappings();

        for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
            String apiFieldName = mapping.getKey();
            String daoFieldName = mapping.getValue();

            Object apiValue = getFieldValue(apiResponse, apiFieldName);
            Object daoValue = getFieldValue(dao, daoFieldName);

            if (!Objects.equals(apiValue, daoValue)) {
                throw new AssertionError(String.format(
                        "Field mismatch for %s: API=%s, DAO=%s",
                        apiFieldName, apiValue, daoValue));
            }
        }
    }

    private Object getFieldValue(Object obj, String fieldPath) {
        Object currentObject = obj;

        for (String fieldName : fieldPath.split("\\.")) {
            if (currentObject == null) {
                return null;
            }

            Class<?> clazz = currentObject.getClass();
            Field field = null;

            while (clazz != null) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }

            if (field == null) {
                throw new RuntimeException(
                        "Field not found: "
                                + fieldName
                                + " in class "
                                + currentObject.getClass().getName()
                );
            }

            try {
                field.setAccessible(true);
                currentObject = field.get(currentObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "Cannot access field: " + fieldName,
                        e
                );
            }
        }

        return currentObject;
    }
}
