package com.hiro.util.methods;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

@Slf4j
public class DataUtil {

    /**
     * Convert object to target class ( must not have other shared fields besides logger )
     * @param source source object
     * @param target target class
     * @return instance of target
     * @param <T> target class
     */
    public static <T> T convert(Object source, Class<T> target) {
        try {
            T instance = target.getDeclaredConstructor().newInstance();

            // traverse fields
            for (Field sourceField : source.getClass().getDeclaredFields()) {

                // Skip shared field ( ex: logger )
                if (org.slf4j.Logger.class.equals(sourceField.getType())) continue;

                // allow util to visit field
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                // skip null field
                if (value == null) continue;

                try {
                    // find filed with field name in target class
                    Field targetField = target.getDeclaredField(sourceField.getName());

                    // allow util to visit field
                    targetField.setAccessible(true);

                    // if target field is assignable type to source field
                    if (targetField.getType().isAssignableFrom(sourceField.getType())) {

                        // get target's setter, and invoke
                        Method method = target.getDeclaredMethod(toSetter(targetField.getName()), targetField.getType());
                        method.invoke(instance, value);
                    }
                } catch (NoSuchFieldException e) {
                    // skip field while can't find it in target class
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.warn("convert class: {} error.", target.getName(), e);
            return null;
        }
    }

    /**
     * flush field values in source to target class ( must not have other shared fields besides logger )
     * @param source source object
     * @param target target
     * @return target
     * @param <T> target class
     */
    public static <T> T flush(Object source, T target) {

        // traverse fields
        for (Field sourceField : source.getClass().getDeclaredFields()) {

            // Skip shared field ( ex: logger )
            if (org.slf4j.Logger.class.equals(sourceField.getType())) continue;

            try {
                // allow util to visit field
                sourceField.setAccessible(true);
                Object value = sourceField.get(source);

                // skip null field
                if (value == null) continue;

                // find filed with field name in target class
                Field targetField = target.getClass().getDeclaredField(sourceField.getName());

                // allow util to visit field
                targetField.setAccessible(true);

                // if target field is assignable type to source field
                if (targetField.getType().isAssignableFrom(sourceField.getType())) {

                    // get target's setter, and invoke
                    Method method = target.getClass()
                            .getDeclaredMethod(toSetter(targetField.getName()), targetField.getType());
                    method.invoke(target, value);
                }
            } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                // skip field while can't find it in target class
            }
        }
        return target;
    }

    /**
     * flush fields in set
     * @param source source object
     * @param target target object
     * @param fields modified fields
     * @return target
     * @param <T> target class
     */
    public static <T> T flushIn(Object source, T target, Set<String> fields) {

        // traverse fields
        for (String field : fields) {
            try {

                // get fields from both object
                Field sourceField = source.getClass().getDeclaredField(field);
                Field targetField = target.getClass().getDeclaredField(field);

                // if target field is assignable type to source field
                if (targetField.getType().isAssignableFrom(sourceField.getType())) {

                    // let util can visit both fields
                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);

                    // get getter & setter from source & target
                    Method getMethod = source.getClass()
                            .getMethod(toGetter(sourceField.getName(), sourceField.getType()));
                    Method setMethod = target.getClass()
                            .getMethod(toSetter(targetField.getName()), targetField.getType());

                    // flush value
                    Object value = getMethod.invoke(source);
                    setMethod.invoke(target, value);
                }
            } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                // skip field while can't find it in target class
            }
        }
        return target;
    }

    /**
     * field to getter method name
     * @param name file name
     * @param type field type
     * @return method name
     */
    private static String toGetter(String name, Class<?> type) {
        if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            return "is" + capitalize(name);
        }
        return "get" + capitalize(name);
    }

    /**
     * field to setter method name
     * @param name field name
     * @return method name
     */
    private static String toSetter(String name) {
        return "set" + capitalize(name);
    }

    /**
     * format str to be upper case in first char
     * @param str str
     * @return str start with upper case
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
