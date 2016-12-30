package naga.fxdata.cell.renderer;

import naga.commons.util.Strings;
import naga.fx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public class TextRenderer implements ValueRenderer {

    public static TextRenderer SINGLETON = new TextRenderer();

    private TextRenderer() {}

    @Override
    public Text renderCellValue(Object value) {
        return new Text(Strings.toSafeString(value));
    }
}