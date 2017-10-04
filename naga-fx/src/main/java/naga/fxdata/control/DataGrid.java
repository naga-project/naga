package naga.fxdata.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import naga.fxdata.displaydata.DisplayResultSet;

/**
 * @author Bruno Salmon
 */
public class DataGrid extends SelectableDisplayResultSetControl {

    public DataGrid() {
    }

    public DataGrid(DisplayResultSet rs) {
        setDisplayResultSet(rs);
    }

    private final BooleanProperty headerVisibleProperty = new SimpleBooleanProperty(true);

    public BooleanProperty headerVisibleProperty() {
        return headerVisibleProperty;
    }

    public boolean isHeaderVisible() {
        return headerVisibleProperty.get();
    }

    public void setHeaderVisible(boolean headerVisible) {
        this.headerVisibleProperty.set(headerVisible);
    }

    private final BooleanProperty fullHeightProperty = new SimpleBooleanProperty(false);

    public BooleanProperty fullHeightProperty() {
        return fullHeightProperty;
    }

    public boolean isFullHeight() {
        return fullHeightProperty.get();
    }

    public void setFullHeight(boolean fullHeight) {
        this.fullHeightProperty.set(fullHeight);
    }

}
