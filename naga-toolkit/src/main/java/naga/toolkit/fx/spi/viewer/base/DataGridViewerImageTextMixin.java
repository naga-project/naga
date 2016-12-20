package naga.toolkit.fx.spi.viewer.base;

import naga.toolkit.display.DisplayColumn;
import naga.toolkit.fx.ext.control.DataGrid;
import naga.toolkit.fx.scene.Node;

/**
 * @author Bruno Salmon
 */
public interface DataGridViewerImageTextMixin
        <C, N extends DataGrid, NB extends DataGridViewerBase<C, N, NB, NM>, NM extends DataGridViewerMixin<C, N, NB, NM>>

        extends DataGridViewerMixin<C, N, NB, NM> {

    default void setCellContent(C cell, Node content, DisplayColumn displayColumn) {
        setCellImageAndTextContent(cell, content, null, displayColumn);
    }

    default void setCellTextContent(C cell, String text, DisplayColumn displayColumn) {
        setCellImageAndTextContent(cell, null, text, displayColumn);
    }

    void setCellImageAndTextContent(C cell, Node image, String text, DisplayColumn displayColumn);

}
