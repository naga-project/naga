package naga.providers.toolkit.html.drawing.svg.view;

import elemental2.Element;
import naga.providers.toolkit.html.drawing.svg.SvgDrawing;
import naga.providers.toolkit.html.events.HtmlMouseEvent;
import naga.providers.toolkit.html.util.HtmlPaints;
import naga.providers.toolkit.html.util.HtmlUtil;
import naga.providers.toolkit.html.util.SvgTransforms;
import naga.providers.toolkit.html.util.SvgUtil;
import naga.toolkit.drawing.effect.BlendMode;
import naga.toolkit.drawing.geometry.VPos;
import naga.toolkit.drawing.paint.Color;
import naga.toolkit.drawing.paint.LinearGradient;
import naga.toolkit.drawing.paint.Paint;
import naga.toolkit.drawing.scene.Node;
import naga.toolkit.drawing.spi.impl.DrawingImpl;
import naga.toolkit.drawing.spi.view.NodeView;
import naga.toolkit.drawing.spi.view.base.NodeViewBase;
import naga.toolkit.drawing.spi.view.base.NodeViewImpl;
import naga.toolkit.drawing.spi.view.base.NodeViewMixin;
import naga.toolkit.drawing.text.Font;
import naga.toolkit.drawing.text.FontPosture;
import naga.toolkit.drawing.text.TextAlignment;
import naga.toolkit.drawing.effect.Effect;
import naga.toolkit.drawing.effect.GaussianBlur;
import naga.toolkit.spi.events.MouseEvent;
import naga.toolkit.spi.events.UiEventHandler;
import naga.toolkit.transform.Transform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Bruno Salmon
 */
public abstract class SvgNodeView
        <N extends Node, NV extends NodeViewBase<N, NV, NM>, NM extends NodeViewMixin<N, NV, NM>>
        extends NodeViewImpl<N, NV, NM> {

    private final Element element;
    private Map<String, Element> svgLinearGradients;
    private Element svgClipPath;

    SvgNodeView(NV base, Element element) {
        super(base);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    @Override
    public void updateVisible(Boolean visible) {
        setElementAttribute("visibility", visible ? null : "hidden");
    }

    @Override
    public void updateOpacity(Double opacity) {
        setElementAttribute("opacity", opacity == 1d ? null : opacity);
    }

    @Override
    public void updateClip(Node clip) {
        setElementAttribute("clip-path", toClipAttribute(clip));
    }

    private String toClipAttribute(Node clip) {
        String value = null;
        if (clip != null) {
            SvgDrawing drawing = (SvgDrawing) DrawingImpl.getThreadLocalDrawing();
            NodeView nodeView = drawing.getOrCreateAndBindNodeView(clip);
            if (svgClipPath == null)
                svgClipPath = drawing.addDef(SvgUtil.createClipPath());
            HtmlUtil.setChild(svgClipPath, ((SvgNodeView) nodeView).getElement());
            value = SvgUtil.getDefUrl(svgClipPath);
        }
        return value;
    }

    @Override
    public void updateBlendMode(BlendMode blendMode) {
        String svgBlend = toSvgBlendMode(blendMode);
        setElementAttribute("style", svgBlend == null ? null : "mix-blend-mode:" + svgBlend);
    }

    @Override
    public void updateEffect(Effect effect) {
        setElementAttribute("filter", effect == null ? null : toSvgEffectFilterUrl(effect));
    }

    private static String toSvgEffectFilterUrl(Effect effect) {
        return SvgUtil.getDefUrl(toSvgEffectFilter(effect));
    }

    private static Element toSvgEffectFilter(Effect effect) {
        Element filterPrimitive = toSvgEffectFilterPrimitive(effect);
        if (filterPrimitive == null)
            return null;
        SvgDrawing drawing = (SvgDrawing) DrawingImpl.getThreadLocalDrawing();
        return drawing.addDef(HtmlUtil.appendChild(SvgUtil.createFilter(), filterPrimitive));
    }

    private static Element toSvgEffectFilterPrimitive(Effect effect) {
        if (effect == null)
            return null;
        if (effect instanceof GaussianBlur) {
            Element fe = SvgUtil.createSvgElement("feGaussianBlur");
            fe.setAttribute("in", "SourceGraphic");
            fe.setAttribute("stdDeviation", ((GaussianBlur) effect).getSigma());
            return fe;
        }
        return null;
    }

    @Override
    public void updateLocalToParentTransforms(Collection<Transform> localToParentTransforms) {
        setElementAttribute("transform", SvgTransforms.toSvgTransforms(localToParentTransforms));
    }

    @Override
    public void updateOnMouseClicked(UiEventHandler<? super MouseEvent> onMouseClicked) {
        element.onclick = onMouseClicked == null ? null : e -> {
            onMouseClicked.handle(new HtmlMouseEvent(e));
            return null;
        };
    }

    void setElementAttribute(String name, String value) {
        setElementAttribute(name, value, null);
    }

    void setElementAttribute(String name, String value, String skipValue) {
        if (Objects.equals(value, skipValue))
            element.removeAttribute(name);
        else
            element.setAttribute(name, value);
    }

    void setElementAttribute(String name, Double value) {
        setElementAttribute(name, value, null);
    }

    void setElementAttribute(String name, Double value, Double skipValue) {
        if (Objects.equals(value, skipValue))
            element.removeAttribute(name);
        else
            element.setAttribute(name, value);
    }

    void setElementAttribute(String name, Integer value) {
        setElementAttribute(name, value, null);
    }

    void setElementAttribute(String name, Integer value, Integer skipValue) {
        if (Objects.equals(value, skipValue))
            element.removeAttribute(name);
        else
            element.setAttribute(name, value);
    }

    void setSvgFontAttributes(Font font) {
        setElementAttribute("font-family", font.getFamily());
        setElementAttribute("font-style", font.getPosture() == FontPosture.ITALIC ? "italic" : "normal", "normal");
        setElementAttribute("font-weight", font.getWeight() == null ? 0 : font.getWeight().getWeight(), 0);
        setElementAttribute("font-size", font.getSize());
    }

    void setSvgTextContent(String textContent) {
        element.textContent = textContent;
    }

    void setPaintAttribute(String name, Paint paint) {
        setElementAttribute(name, toPaintAttribute(name, paint));
    }

    private String toPaintAttribute(String name, Paint paint) {
        String value = null;
        if (paint instanceof Color)
            value = HtmlPaints.toSvgCssPaint(paint);
        else if (paint instanceof LinearGradient) {
            if (svgLinearGradients == null)
                svgLinearGradients = new HashMap<>();
            Element svgLinearGradient = svgLinearGradients.get(name);
            if (svgLinearGradient == null)
                svgLinearGradients.put(name, svgLinearGradient = ((SvgDrawing) DrawingImpl.getThreadLocalDrawing()).addDef(SvgUtil.createLinearGradient()));
            SvgUtil.updateLinearGradient((LinearGradient) paint, svgLinearGradient);
            value = SvgUtil.getDefUrl(svgLinearGradient);
        }
        return value;
    }

    static String vPosToSvgAlignmentBaseLine(VPos vpos) {
        if (vpos != null)
            switch (vpos) {
                case TOP: return "text-before-edge";
                case CENTER: return "central";
                case BASELINE: return "baseline";
                case BOTTOM: return "text-after-edge";
            }
        return null;
    }

    static String textAlignmentToSvgTextAnchor(TextAlignment textAlignment) {
        if (textAlignment != null)
            switch (textAlignment) {
                case LEFT: return "start";
                case CENTER: return "middle";
                case RIGHT: return "end";
            }
        return null;
    }

    static String toSvgBlendMode(BlendMode blendMode) {
        if (blendMode != null)
            switch (blendMode) {
                case SRC_OVER: return "";
                case SRC_ATOP: return "";
                case ADD: return "";
                case MULTIPLY: return "multiply";
                case SCREEN: return "screen";
                case OVERLAY: return "overlay";
                case DARKEN: return "darken";
                case LIGHTEN: return "lighten";
                case COLOR_DODGE: return "color-dodge";
                case COLOR_BURN: return "color-burn";
                case HARD_LIGHT: return "hard-light";
                case SOFT_LIGHT: return "soft-light";
                case DIFFERENCE: return "difference";
                case EXCLUSION: return "exclusion";
                case RED: return "";
                case GREEN: return "";
                case BLUE: return "";
            }
        return null;
    }

}
