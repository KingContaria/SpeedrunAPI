package me.contaria.speedrunapi.config.screen.widgets.option;

import me.contaria.speedrunapi.config.option.*;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class OptionWidgetClientWrapper {
    public static @NotNull AbstractButtonWidget createBooleanOptionButtonWidget(BooleanOption option, int x, int y) {
        return new BooleanOptionButtonWidget(option, x, y);
    }

    public static @NotNull AbstractButtonWidget createEnumOptionButtonWidget(EnumOption option, int x, int y) {
        return new EnumOptionButtonWidget(option, x, y);
    }

    public static @NotNull <T extends Number> AbstractButtonWidget createFractionalNumberOptionSliderWidget(FractionalNumberOption<T> option, int x, int y) {
        return new FractionalNumberOptionSliderWidget<>(option, x, y);
    }

    public static @NotNull <S extends Number, T extends NumberOption<S>> AbstractButtonWidget createNumberOptionTextFieldWidget(T option, int x, int y) {
        return new NumberOptionTextFieldWidget<>(option, x, y);
    }

    public static @NotNull AbstractButtonWidget createStringOptionTextFieldWidget(StringOption option, int x, int y) {
        return new StringOptionTextFieldWidget(option, x, y);
    }

    public static @NotNull <T extends Number> AbstractButtonWidget createWholeNumberOptionSliderWidget(WholeNumberOption<T> option, int x, int y) {
        return new WholeNumberOptionSliderWidget<>(option, x, y);
    }
}
