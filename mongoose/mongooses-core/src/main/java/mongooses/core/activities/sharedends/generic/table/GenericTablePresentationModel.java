package mongooses.core.activities.sharedends.generic.table;

import javafx.beans.property.*;
import webfx.fxkits.extra.displaydata.DisplayResult;
import webfx.fxkits.extra.displaydata.DisplaySelection;

/**
 * @author Bruno Salmon
 */
public class GenericTablePresentationModel {

    // Display input

    private final StringProperty searchTextProperty = new SimpleStringProperty();
    public StringProperty searchTextProperty() { return searchTextProperty; }

    private final IntegerProperty limitProperty = new SimpleIntegerProperty(0);
    public IntegerProperty limitProperty() { return limitProperty; }

    private final Property<DisplaySelection> genericDisplaySelectionProperty = new SimpleObjectProperty<>();
    public Property<DisplaySelection> genericDisplaySelectionProperty() { return genericDisplaySelectionProperty; }

    // Display output

    private final Property<DisplayResult> genericDisplayResultProperty = new SimpleObjectProperty<>();
    public Property<DisplayResult> genericDisplayResultProperty() { return genericDisplayResultProperty; }

}