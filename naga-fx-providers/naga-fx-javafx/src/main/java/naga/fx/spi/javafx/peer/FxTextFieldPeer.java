package naga.fx.spi.javafx.peer;

import naga.commons.util.Objects;
import naga.fx.spi.javafx.util.FxFonts;
import naga.fx.scene.control.TextField;
import naga.fx.scene.text.Font;
import naga.fx.spi.peer.base.TextFieldPeerBase;
import naga.fx.spi.peer.base.TextFieldPeerMixin;

/**
 * @author Bruno Salmon
 */
public class FxTextFieldPeer
        <FxN extends javafx.scene.control.TextField, N extends TextField, NB extends TextFieldPeerBase<N, NB, NM>, NM extends TextFieldPeerMixin<N, NB, NM>>

        extends FxTextInputControlPeer<FxN, N, NB, NM>
        implements TextFieldPeerMixin<N, NB, NM>, FxLayoutMeasurable {

    public FxTextFieldPeer() {
        super((NB) new TextFieldPeerBase());
    }

    @Override
    protected FxN createFxNode() {
        javafx.scene.control.TextField textField = new javafx.scene.control.TextField();
        textField.textProperty().addListener((observable, oldValue, newValue) -> updateNodeText(newValue));
        return (FxN) textField;
    }

    @Override
    public void updateFont(Font font) {
        if (font != null)
            getFxNode().setFont(FxFonts.toFxFont(font));
    }

    private void updateNodeText(String text) {
        getNode().setText(text);
    }

    @Override
    public void updateText(String text) {
        if (!Objects.areEquals(text, getFxNode().getText())) // to avoid caret position reset while typing
            getFxNode().setText(text);
    }

    @Override
    public void updatePrompt(String prompt) {
        getFxNode().setPromptText(prompt);
    }
}