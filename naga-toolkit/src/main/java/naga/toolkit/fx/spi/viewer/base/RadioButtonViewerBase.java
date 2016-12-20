package naga.toolkit.fx.spi.viewer.base;

import naga.toolkit.fx.scene.control.RadioButton;

/**
 * @author Bruno Salmon
 */
public class RadioButtonViewerBase
        <N extends RadioButton, NB extends RadioButtonViewerBase<N, NB, NM>, NM extends RadioButtonViewerMixin<N, NB, NM>>

        extends ToggleButtonViewerBase<N, NB, NM> {
}
