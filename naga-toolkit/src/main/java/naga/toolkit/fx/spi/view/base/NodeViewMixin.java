package naga.toolkit.fx.spi.view.base;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import naga.toolkit.fx.scene.effect.BlendMode;
import naga.toolkit.fx.scene.Node;
import naga.toolkit.fx.spi.DrawingRequester;
import naga.toolkit.fx.spi.view.NodeView;
import naga.toolkit.fx.scene.effect.Effect;
import naga.toolkit.spi.events.MouseEvent;
import naga.toolkit.spi.events.UiEventHandler;
import naga.toolkit.fx.scene.transform.Transform;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public interface NodeViewMixin
        <D extends Node, DV extends NodeViewBase<D, DV, DM>, DM extends NodeViewMixin<D, DV, DM>>
        extends NodeView<D> {

    DV getNodeViewBase();

    @Override
    default void bind(D node, DrawingRequester drawingRequester) {
        getNodeViewBase().bind(node, drawingRequester);
    }

    @Override
    default void unbind() {
        getNodeViewBase().unbind();
    }

    @Override
    default boolean updateProperty(Property changedProperty) {
        return getNodeViewBase().updateProperty(changedProperty);
    }

    @Override
    default boolean updateList(ObservableList changedList) {
        return getNodeViewBase().updateList(changedList);
    }

    void updateOnMouseClicked(UiEventHandler<? super MouseEvent> onMouseClicked);

    void updateMouseTransparent(Boolean mouseTransparent);

    void updateVisible(Boolean visible);

    void updateOpacity(Double opacity);

    void updateClip(Node clip);

    void updateBlendMode(BlendMode blendMode);

    void updateEffect(Effect effect);

    default void updateLayoutX(Double layoutX) {
        updateLocalToParentTransforms(getNodeViewBase().getNode().localToParentTransforms());
    }

    default void updateLayoutY(Double layoutY) {
        updateLocalToParentTransforms(getNodeViewBase().getNode().localToParentTransforms());
    }

    default void updateTransforms(List<Transform> transforms) {
        updateLocalToParentTransforms(getNodeViewBase().getNode().localToParentTransforms());
    }

    void updateLocalToParentTransforms(Collection<Transform> localToParentTransforms);
}