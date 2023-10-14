package org.mcsr.speedrunapi.properties.example;

import org.mcsr.speedrunapi.SpeedrunAPI;
import org.mcsr.speedrunapi.properties.api.SpeedrunProperties;
import org.mcsr.speedrunapi.properties.api.annotations.NoProperty;
import org.mcsr.speedrunapi.properties.api.annotations.Property;

import java.util.Random;

@SuppressWarnings("unused")
public class ExampleProperties implements SpeedrunProperties {

    @NoProperty
    public static ExampleProperties INSTANCE;

    public String readOnlyString = "Don't you dare change me from a different mod!!";

    @Property.Restrictions(settable = true)
    public boolean doWhateverYouWantWithThisBoolean = true;

    @Property.Restrictions(settable = true)
    @Property.Access(getter = "getTheAwareInt", setter = "setTheAwareInt")
    public int thisIntKnowsThatYouChangedIt = 0;

    public int getTheAwareInt() {
        SpeedrunAPI.LOGGER.info("I will tell you my value but you'll owe me a favor!");
        return this.thisIntKnowsThatYouChangedIt;
    }

    public void setTheAwareInt(int value) {
        if (new Random().nextBoolean()) {
            SpeedrunAPI.LOGGER.info("Not this time, try again later!");
            return;
        }
        SpeedrunAPI.LOGGER.info("Okay, I accept you changing me.");
        this.thisIntKnowsThatYouChangedIt = value;
    }

    @Override
    public String modID() {
        return "speedrunapi";
    }
}
