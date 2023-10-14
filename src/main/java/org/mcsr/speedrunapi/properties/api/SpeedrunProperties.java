package org.mcsr.speedrunapi.properties.api;

import org.mcsr.speedrunapi.properties.api.annotations.NoProperty;
import org.mcsr.speedrunapi.properties.api.annotations.Property;
import org.mcsr.speedrunapi.properties.exceptions.NoSuchPropertyException;
import org.mcsr.speedrunapi.properties.exceptions.SpeedrunPropertiesAPIException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface SpeedrunProperties {

    String modID();

    default Object get(String property) throws NoSuchPropertyException {
        try {
            Field field = this.getClass().getDeclaredField(property);

            if (field.isAnnotationPresent(NoProperty.class)) {
                throw new NoSuchPropertyException("Property \"" + property + "\" does not exist in " + this.modID() + ".");
            }

            Property.Restrictions restrictions = field.getAnnotation(Property.Restrictions.class);
            if (restrictions != null && !restrictions.gettable()) {
                throw new NoSuchPropertyException("Property \"" + property + "\" is not gettable in " + this.modID() + ".");
            }

            Property.Access access = field.getAnnotation(Property.Access.class);
            if (access != null && !access.getter().isEmpty()) {
                Method getter = this.getClass().getDeclaredMethod(access.getter());
                if (!getter.getReturnType().equals(field.getType())) {
                    throw new SpeedrunPropertiesAPIException("Provided getter method for \"" + property + "\" does not exist in " + this.modID() + ".");
                }
                getter.setAccessible(true);
                return getter.invoke(this);
            }

            field.setAccessible(true);
            return field.get(this);
        } catch (NoSuchFieldException e) {
            throw new NoSuchPropertyException("Property \"" + property + "\" does not exist in " + this.modID() + ".", e);
        } catch (IllegalAccessException e) {
            throw new NoSuchPropertyException("Property \"" + property + "\" could not be accessed in " + this.modID() + ".", e);
        } catch (NoSuchMethodException e) {
            throw new SpeedrunPropertiesAPIException("Provided getter method for \"" + property + "\" does not exist in " + this.modID() + ".", e);
        } catch (InvocationTargetException e) {
            throw new SpeedrunPropertiesAPIException("Failed to invoke provided getter method for \"" + property + " in " + this.modID() + ".", e);
        }
    }

    default void set(String property, Object value) {
        try {
            Field field = this.getClass().getDeclaredField(property);

            if (field.isAnnotationPresent(NoProperty.class)) {
                throw new NoSuchPropertyException("Property \"" + property + "\" does not exist in " + this.modID() + ".");
            }

            Property.Restrictions restrictions = field.getAnnotation(Property.Restrictions.class);
            if (restrictions == null || !restrictions.settable()) {
                throw new NoSuchPropertyException("Property \"" + property + "\" is not settable in " + this.modID() + ".");
            }

            Property.Access access = field.getAnnotation(Property.Access.class);
            if (access != null && !access.setter().isEmpty()) {
                Method getter = this.getClass().getDeclaredMethod(access.setter(), field.getType());
                getter.setAccessible(true);
                getter.invoke(this, value);
                return;
            }

            field.setAccessible(true);
            field.set(this, value);
        } catch (NoSuchFieldException e) {
            throw new NoSuchPropertyException("Property \"" + property + "\" does not exist in " + this.modID() + ".", e);
        } catch (IllegalAccessException e) {
            throw new NoSuchPropertyException("Property \"" + property + "\" could not be accessed in " + this.modID() + ".", e);
        } catch (NoSuchMethodException e) {
            throw new SpeedrunPropertiesAPIException("Provided setter method for \"" + property + "\" does not exist in " + this.modID() + ".", e);
        } catch (InvocationTargetException e) {
            throw new SpeedrunPropertiesAPIException("Failed to invoke provided setter method for \"" + property + " in " + this.modID() + ".", e);
        }
    }
}
