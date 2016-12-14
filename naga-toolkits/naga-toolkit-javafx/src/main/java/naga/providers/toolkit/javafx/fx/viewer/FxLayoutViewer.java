package naga.providers.toolkit.javafx.fx.viewer;

import naga.toolkit.fx.scene.layout.Region;
import naga.toolkit.fx.spi.viewer.base.RegionViewerBase;
import naga.toolkit.fx.spi.viewer.base.RegionViewerMixin;

/**
 * @author Bruno Salmon
 */
public class FxLayoutViewer
        <N extends Region, NV extends RegionViewerBase<N, NV, NM>, NM extends RegionViewerMixin<N, NV, NM>>
        extends FxRegionViewer<javafx.scene.layout.Region, N, NV, NM> {

    public FxLayoutViewer() {
        super((NV) new RegionViewerBase<N, NV, NM>());
    }

    @Override
    javafx.scene.layout.Region createFxNode() {
        // We override the children layout since the layout is now done by NagaFx (and not JavaFx)
        return new javafx.scene.layout.Pane() {
            @Override
            protected void layoutChildren() {
                // Most of the time the layout is already done by NagaFx and there is nothing more to do but there are a
                // few exceptions like for example when this layout (ex: HBox) is displayed within a TableView cell. In
                // this case, NagaFx hasn't done the layout job yet (because it's not direct part of the scene graph),
                // so we take the opportunity of this JavaFx call to do it now.
                // First, we resize the NagaFx node to match the JavaFx one (typically the visual rectangle computed by
                // JavaFx within the table cell where the node must be drawn).
                N node = getNode();
                if (!node.widthProperty().isBound())
                    node.setWidth(getWidth());
                if (!node.heightProperty().isBound())
                    node.setHeight(getHeight());
                // Then we ask NagaFx to layout the children (this will update the children viewers which actually hold
                node.layout(); // the JavaFx children of this layout Region).
            }
        };
    }
}