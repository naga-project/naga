package naga.toolkit.fx.properties.markers;

import javafx.beans.property.Property;
import naga.toolkit.fx.geometry.VPos;

/**
 * @author Bruno Salmon
 */
public interface HasTextOriginProperty {

    Property<VPos> textOriginProperty();
    default void setTextOrigin(VPos textOrigin) {
        textOriginProperty().setValue(textOrigin);
    }
    default VPos getTextOrigin() {
        return textOriginProperty().getValue();
    }

}