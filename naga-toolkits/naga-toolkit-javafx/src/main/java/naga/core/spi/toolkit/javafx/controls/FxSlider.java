package naga.core.spi.toolkit.javafx.controls;

import javafx.beans.property.Property;
import naga.core.spi.toolkit.controls.Slider;
import naga.core.spi.toolkit.javafx.JavaFxToolkit;
import naga.core.spi.toolkit.javafx.node.FxNode;
import naga.core.spi.toolkit.property.ConvertedProperty;

/**
 * @author Bruno Salmon
 */
public class FxSlider extends FxNode<javafx.scene.control.Slider> implements Slider<javafx.scene.control.Slider> {

    public FxSlider() {
        this(new javafx.scene.control.Slider());
    }

    public FxSlider(javafx.scene.control.Slider slider) {
        super(slider);
    }


    private ConvertedProperty<Integer, Number> valueProperty = JavaFxToolkit.numberToIntegerProperty(node.valueProperty());
    @Override
    public Property<Integer> valueProperty() {
        return valueProperty;
    }

    private ConvertedProperty<Integer, Number> minProperty = JavaFxToolkit.numberToIntegerProperty(node.minProperty());
    @Override
    public Property<Integer> minProperty() {
        return minProperty;
    }

    private ConvertedProperty<Integer, Number> maxProperty = JavaFxToolkit.numberToIntegerProperty(node.maxProperty());
    @Override
    public Property<Integer> maxProperty() {
        return maxProperty;
    }

}
