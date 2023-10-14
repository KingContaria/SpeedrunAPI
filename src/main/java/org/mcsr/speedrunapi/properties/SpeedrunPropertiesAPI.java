package org.mcsr.speedrunapi.properties;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.mcsr.speedrunapi.properties.api.SpeedrunProperties;
import org.mcsr.speedrunapi.properties.exceptions.NoSuchPropertyException;
import org.mcsr.speedrunapi.properties.exceptions.SpeedrunPropertiesAPIException;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpeedrunPropertiesAPI {

    private static final Map<String, SpeedrunProperties> PROPERTIES = Collections.synchronizedMap(new HashMap<>());

    public static void initialize() {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            try {
                CustomValue customValues = mod.getMetadata().getCustomValues().get("speedrunapi");
                if (customValues != null) {
                    CustomValue.CvObject customObject = customValues.getAsObject();

                    CustomValue properties = customObject.get("properties");
                    if (properties != null) {
                        Class<?> propertiesClass = Class.forName(properties.getAsString());
                        if (SpeedrunProperties.class.isAssignableFrom(propertiesClass)) {
                            register(mod, (Class<? extends SpeedrunProperties>) propertiesClass);
                        }
                    }
                }
            } catch (ClassCastException e) {
                throw new SpeedrunPropertiesAPIException("Faulty fabric.mod.json values from " + mod.getMetadata().getId() + ".", e);
            } catch (ClassNotFoundException e) {
                throw new SpeedrunPropertiesAPIException("Provided class from " + mod.getMetadata().getId() + " does not exist.", e);
            }
        }
    }

    private static <T extends SpeedrunProperties> void register(ModContainer mod, Class<T> configClass) {
        String modID = mod.getMetadata().getId();

        if (PROPERTIES.containsKey(modID)) {
            throw new SpeedrunPropertiesAPIException("Properties for " + modID + " are already registered!");
        }

        try {
            PROPERTIES.put(modID, constructClass(configClass));
        } catch (ReflectiveOperationException e) {
            throw new SpeedrunPropertiesAPIException("Failed to build config for " + modID, e);
        }
    }

    private static <T> T constructClass(Class<T> aClass) throws ReflectiveOperationException {
        Constructor<T> constructor = aClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    /**
     * Retrieves the requested property's value from the {@link SpeedrunProperties} linked to the given mod ID.
     *
     * @param modID - The mod ID of the mod owning the property.
     * @param property - The name of the property.
     * @return Returns the property's value.
     *
     * @throws NoSuchPropertyException - If the given mod does not exist, does not provide any {@link SpeedrunProperties} or does not provide the requested property.
     */
    public static Object getProperty(String modID, String property) throws NoSuchPropertyException {
        SpeedrunProperties properties = PROPERTIES.get(modID);
        if (properties != null) {
            return properties.get(property);
        } else {
            throw new NoSuchPropertyException();
        }
    }

    /**
     * Wraps the result of {@link SpeedrunPropertiesAPI#getProperty} in an {@link Optional}.
     * Returns {@link Optional#empty} if a {@link NoSuchPropertyException} is thrown.
     * <p>
     * This will not catch any other {@link SpeedrunPropertiesAPIException}'s that may be thrown.
     *
     * @param modID - The mod ID of the mod owning the property.
     * @param property - The name of the property.
     * @return Returns an {@link Optional} of the property's value.
     *
     * @see SpeedrunPropertiesAPI#getProperty
     */
    public static Optional<Object> getPropertyOptionally(String modID, String property) {
        try {
            return Optional.of(getProperty(modID, property));
        } catch (NoSuchPropertyException e) {
            return Optional.empty();
        }
    }

    /**
     * Sets the requested property's value from the {@link SpeedrunProperties} linked to the given mod ID.
     *
     * @param modID - The mod ID of the mod owning the property.
     * @param property - The name of the property.
     * @param value - The value to set the property to.
     *
     * @throws NoSuchPropertyException - If the given mod does not exist, does not provide any {@link SpeedrunProperties} or does not provide the requested property.
     */
    public static <T> void setProperty(String modID, String property, T value) throws NoSuchPropertyException {
        SpeedrunProperties properties = PROPERTIES.get(modID);
        if (properties != null) {
            properties.set(property, value);
        } else {
            throw new NoSuchPropertyException();
        }
    }

    /**
     * Calls {@link SpeedrunPropertiesAPI#setProperty}.
     * Returns {@code true} if the property is set successfully, {@code false} if a {@link NoSuchPropertyException} is thrown.
     * <p>
     * This will not catch any other {@link SpeedrunPropertiesAPIException}'s that may be thrown.
     *
     * @param modID - The mod ID of the mod owning the property.
     * @param property - The name of the property.
     * @param value - The value to set the property to.
     *
     * @see SpeedrunPropertiesAPI#setProperty
     */
    public static boolean setPropertyOptionally(String modID, String property, Object value) {
        try {
            setProperty(modID, property, value);
            return true;
        } catch (NoSuchPropertyException e) {
            return false;
        }
    }
}
