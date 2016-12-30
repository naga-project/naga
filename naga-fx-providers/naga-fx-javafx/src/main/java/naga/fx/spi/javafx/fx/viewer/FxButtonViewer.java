package naga.fx.spi.javafx.fx.viewer;

import naga.fx.scene.control.Button;
import naga.fx.spi.viewer.base.ButtonViewerBase;
import naga.fx.spi.viewer.base.ButtonViewerMixin;

/**
 * @author Bruno Salmon
 */
public class FxButtonViewer
        <FxN extends javafx.scene.control.Button, N extends Button, NB extends ButtonViewerBase<N, NB, NM>, NM extends ButtonViewerMixin<N, NB, NM>>

        extends FxButtonBaseViewer<FxN, N, NB, NM>
        implements ButtonViewerMixin<N, NB, NM>, FxLayoutMeasurable {

    public FxButtonViewer() {
        super((NB) new ButtonViewerBase());
    }

    @Override
    protected FxN createFxNode() {
        return (FxN) new javafx.scene.control.Button();
    }
}