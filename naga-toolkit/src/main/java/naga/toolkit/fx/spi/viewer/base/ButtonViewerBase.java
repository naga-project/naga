package naga.toolkit.fx.spi.viewer.base;

import naga.toolkit.fx.scene.control.Button;

/**
 * @author Bruno Salmon
 */
public class ButtonViewerBase
        <N extends Button, NB extends ButtonViewerBase<N, NB, NM>, NM extends ButtonViewerMixin<N, NB, NM>>

        extends ButtonBaseViewerBase<N, NB, NM> {
}
