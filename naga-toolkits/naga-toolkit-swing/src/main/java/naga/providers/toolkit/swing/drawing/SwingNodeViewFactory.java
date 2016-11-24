package naga.providers.toolkit.swing.drawing;

import naga.providers.toolkit.swing.drawing.view.*;
import naga.toolkit.drawing.scene.impl.EmbedGuiNodeImpl;
import naga.toolkit.drawing.scene.impl.GroupImpl;
import naga.toolkit.drawing.shape.impl.*;
import naga.toolkit.drawing.spi.impl.NodeViewFactoryImpl;
import naga.toolkit.drawing.text.impl.TextShapeImpl;

/**
 * @author Bruno Salmon
 */
class SwingNodeViewFactory extends NodeViewFactoryImpl {

    final static SwingNodeViewFactory SINGLETON = new SwingNodeViewFactory();

    private SwingNodeViewFactory() {
        registerNodeViewFactory(RectangleImpl.class, SwingRectangleView::new);
        registerNodeViewFactory(CircleImpl.class, SwingCircleView::new);
        registerNodeViewFactory(TextShapeImpl.class, SwingTextShapeView::new);
        registerNodeViewFactory(GroupImpl.class, SwingGroupView::new);
        registerNodeViewFactory(EmbedGuiNodeImpl.class, SwingEmbedGuiNodeView::new);
    }
}