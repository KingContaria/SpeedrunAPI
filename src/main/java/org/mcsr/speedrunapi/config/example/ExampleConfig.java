package org.mcsr.speedrunapi.config.example;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.mcsr.speedrunapi.config.api.EnumTextProvider;
import org.mcsr.speedrunapi.config.api.annotations.*;

@SuppressWarnings("unused")
@SpeedrunConfig(modID = "speedrunapi")
public class ExampleConfig {

    public static ExampleConfig INSTANCE;

    // a boolean config option named joe which defaults to false
    @Config.Name(value = "Joe", literal = true)
    @Config.Description(value = "This option is named Joe", literal = true)
    public boolean aBooleanConfigOption = false;

    // a String config option limited to 20 characters which defaults to "fraud"
    // @Config.Strings.MaxChars is an optional annotation, if not present it will default to a 100-character limit
    @Config.Strings.MaxChars(20)
    private String aStringConfigOption = "fraud";

    // an int config option with a minimum value of 11 and a maximum value of 44 which defaults to 43
    // @Config.Numbers.WholeBounds is a required annotation for short, int and long options
    @Config.Numbers.WholeBounds(min = 11, max = 44)
    public int anIntegerConfigOption = 43;

    // an int config option with a (defaulted) minimum value of 0.0 and a maximum value of 5.0 which defaults to 0.7
    // @Config.Numbers.FractionalBounds is a required annotation for float and double options
    @Config.Numbers.FractionalBounds(max = 5.0)
    public double aDoubleConfigOption = 0.7;

    // an enum config option of the ExampleEnum type
    // @Config.Name.Auto will make the name the translation of "speedrunapi.config.<modid>.<option>"
    // @Config.Description.Auto will make the name the translation of "speedrunapi.config.<modid>.<option>.description"
    @Config.Name.Auto
    @Config.Description.Auto
    protected ExampleEnum anEnum = ExampleEnum.THREE;

    // the two following boolean options are just here to make the scrollbar on the config menu appear
    public boolean isTrue = false;
    public boolean isFalse = true;

    // a boolean value in the config class which is not configurable (and also not saved in the config file)
    // because it is annotated by @NoConfig
    @NoConfig
    public boolean aNonConfigurableBooleanValue = true;

    {
        INSTANCE = this;
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

        @Override
        public Text toText() {
            return new LiteralText(this.string);
        }
    }
}
