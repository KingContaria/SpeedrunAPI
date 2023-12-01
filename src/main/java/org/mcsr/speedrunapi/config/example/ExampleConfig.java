package org.mcsr.speedrunapi.config.example;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.mcsr.speedrunapi.config.api.SpeedrunConfig;
import org.mcsr.speedrunapi.config.api.SpeedrunConfigStorage;
import org.mcsr.speedrunapi.config.api.option.EnumTextProvider;
import org.mcsr.speedrunapi.config.api.annotations.*;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
@InitializeOn(InitializeOn.InitPoint.PRELAUNCH)
public class ExampleConfig implements SpeedrunConfig {

    @Config.Ignored
    public static ExampleConfig INSTANCE;

    // a boolean config option using custom translation keys for name and description
    // without these, translation keys default to "speedrunapi.config.modid.option.theOption" for the name
    // and "speedrunapi.config.modid.option.theOption.description" for the description
    @Config.Name("speedrunapi.some.other.translationkey")
    @Config.Description("speedrunapi.some.other.translationkey.description")
    public boolean aBooleanConfigOption = false;

    // a String config option limited to 20 characters which defaults to "fraud"
    // @Config.Strings.MaxChars is an optional annotation
    @Config.Strings.MaxChars(20)
    private String aStringConfigOption = "fraud";

    // an int config option with a minimum value of 11 and a maximum value of 44 which defaults to 43
    // the given minimum and maximum are not enforced, instead they only serve as boundaries for the gui widget
    // @Config.Numbers.Whole.Bounds is a required annotation for short, int and long options
    // @Config.Numbers.Whole.Intervals is an optional annotation for short, int and long options
    @Config.Numbers.Whole.Bounds(min = 11, max = 44, enforce = Config.Numbers.EnforceBounds.FALSE)
    @Config.Numbers.Whole.Intervals(3)
    public int anIntegerConfigOption = 41;

    // an int config option with a (defaulted) minimum value of 0.0 and a maximum value of 5.0 which defaults to 0.7
    // @Config.Numbers.Fractional.Bounds is a required annotation for float and double options
    // @Config.Numbers.Fractional.Intervals is an optional annotation for float and double options
    // @Config.Numbers.TextField is an optional annotation for number options and makes the gui widget for the option
    // a text input field rather than a slider
    @Config.Numbers.Fractional.Bounds(max = 5.0)
    @Config.Numbers.Fractional.Intervals(0.1)
    @Config.Numbers.TextField
    public double aDoubleConfigOption = 0.7;

    // an enum config option of the ExampleEnum type
    protected ExampleEnum anEnum = ExampleEnum.THREE;

    // the two following boolean options are sorted under a "filler" category using the translation key "speedrunapi.config.speedrunapi.category.fillers"
    @Config.Category("fillers")
    public boolean isTrue = false;

    // @Config.Description.None is an optional annotation for options to remove the default description of "speedrunapi.config.<modid>.option.<option>.description"
    @Config.Category("fillers")
    @Config.Description.None
    public boolean isFalse = true;

    // this object implements the SpeedrunConfigStorage interface, making it so that all the fields in the object get turned into options
    // if the storage is annotated with a category, options in the storage automatically get assigned this category if they do not already have one
    @Config.Category("storage")
    public ExampleOptionStorage thisStoresMoreOptions = new ExampleOptionStorage();

    // a boolean value in the config class which is not configurable (and also not saved in the config file)
    // because it is annotated by @Config.Ignored
    @Config.Ignored
    public boolean aNonConfigurableBooleanValue = true;

    {
        INSTANCE = this;
    }

    @Override
    public String modID() {
        return "speedrunapi";
    }

    @Override
    public boolean isAvailable() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static class ExampleOptionStorage implements SpeedrunConfigStorage {

        public boolean thisBooleanIsStoredInAnotherClass = true;

        @Config.Numbers.Whole.Bounds(max = 5)
        public short soIsThisShort = 3;
    }

    public enum ExampleEnum implements EnumTextProvider {
        THREE("pausemansit"),
        TWO("pauseman"),
        ONE("pause"),
        GO("POGGERS");

        private final String string;

        ExampleEnum(String string) {
            this.string = string;
        }

        // Implementing EnumTextProvider allows you to use custom names for your enum values
        @Override
        public Text toText() {
            return new LiteralText(this.string);
        }
    }
}
