package webfx.kit.mapper.spi.gwt;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import webfx.kit.mapper.peers.javafxgraphics.emul_coupling.ScenePeer;
import webfx.kit.mapper.peers.javafxgraphics.emul_coupling.StagePeer;
import webfx.kit.mapper.peers.javafxgraphics.emul_coupling.WindowPeer;
import webfx.kit.mapper.spi.base.WebFxKitMapperProviderBase;
import webfx.kit.mapper.peers.javafxgraphics.gwt.html.HtmlScenePeer;
import webfx.kit.mapper.peers.javafxgraphics.gwt.shared.GwtPrimaryStagePeer;
import webfx.kit.mapper.peers.javafxgraphics.gwt.shared.GwtSecondaryStagePeer;
import webfx.kit.launcher.WebFxKitLauncher;

/**
 * @author Bruno Salmon
 */
public final class GwtWebFxKitSvgMapperProvider extends WebFxKitMapperProviderBase {

    @Override
    public StagePeer createStagePeer(Stage stage) {
        if (stage == WebFxKitLauncher.getPrimaryStage())
            return new GwtPrimaryStagePeer(stage);
        return new GwtSecondaryStagePeer(stage);
    }

    @Override
    public WindowPeer createWindowPeer(Window window) {
        return null;
    }

    @Override
    public ScenePeer createScenePeer(Scene scene) {
        return new HtmlScenePeer(scene);
    }
}
