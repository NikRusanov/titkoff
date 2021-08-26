package com.rusanov.titkoff.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * @author Magnusyatina
 * Abstract visitor viewer, base on Visitor pattern, implements Handler functional interface
 *
 * @param <T> - generic type of processed object
 * @param <R> - generic type of received object
 */
public class AbstractVisitorViewer <T, R> implements Handler<T, R> {
    /**
     * a value map that associates the type of an object with a method that processes the object
     */
    private Map<String, Method> methodList = new HashMap<>();
    /**
     * a set containing all types of objects that should be processed by the standard handler
     */
    private Set<String> defaultHandles = new HashSet<>();
    /**
     * sublayer whose method will be called if no method is found to process the current object in the current layer
     */
    private Handler<T, R> subHandler;

    /**
     * Method to get facade sub handler
     *
     * @return Facade sublayer with type {@link Handler}
     */
    public Handler<T, R> getSubHandler() {
        return subHandler;
    }

    /**
     * Method to set facade sub handler
     *
     * @param subHandler - sublayer whose method will be called if no method is found to process the current object in the current layer
     */
    public void setSubHandler(Handler<T, R> subHandler) {
        this.subHandler = subHandler;
    }

    /**
     * Base constructor
     *
     * @param subHandler - sublayer whose method will be called if no method is found to process the current object in the current layer
     */
    public AbstractVisitorViewer(Handler<T, R> subHandler) {
        this.subHandler = subHandler;
    }

    /**
     * Object handling method implementation
     * Looks for a method of the current class to process the received parameters
     * If a handler method was found for the current parameters, binds the type of the parameters and the method
     * that handles that parameters so that it doesn't use Reflection API in the future
     * If the method is not found, the base object handler is called
     *
     * @param params - handled parameters
     * @return object type of {@link R}
     */
    @Override
    public R handle(T... params) {
        Class<?> classes[] = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            classes[i] = params[i].getClass();
        }

        String constraintKey = getConstraintKey(params);
        try {
            if (defaultHandles.contains(constraintKey))
                return defaultHandle(params);

            Method method;

            if ((method = methodList.get(constraintKey)) == null) {
                method = getClass().getMethod("handle", classes);
                method.setAccessible(true);
                methodList.put(constraintKey, method);
            }

            return (R) method.invoke(this, params);
        } catch (NoSuchMethodException e) {
            defaultHandles.add(constraintKey);
            return defaultHandle(params);
        } catch (IllegalAccessException e) {
            // TODO Add logger
        } catch (InvocationTargetException e) {
            // TODO Add logger
        }

        return null;
    }

    /**
     * Default method to handle object
     * If {@link Handler} sublayer is exist, call handle method at the sub handler
     *
     * @param params - handled params
     * @return {@link R} object with generic type
     */
    protected R defaultHandle(T... params) {
        if (subHandler != null) {
            return (R) subHandler.handle(params);
        }
        return null;
    }

    /**
     * Method to generate constraint key from objects
     *
     * @param objects from which the constraint key is built
     * @return constraint key
     */
    private String getConstraintKey(T... objects) {
        StringBuilder constraintKey = new StringBuilder("");
        for (T object : objects) {
            constraintKey.append(object.getClass().getCanonicalName());
        }
        return constraintKey.toString();
    }
}
