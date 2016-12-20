package naga.providers.toolkit.javafx.fx.viewer;

import naga.toolkit.fx.ext.chart.AreaChart;
import naga.toolkit.fx.spi.viewer.base.AreaChartViewerBase;
import naga.toolkit.fx.spi.viewer.base.AreaChartViewerMixin;

/**
 * @author Bruno Salmon
 */
public class FxAreaChartViewer
        <FxN extends javafx.scene.chart.AreaChart, N extends AreaChart, NB extends AreaChartViewerBase<FxN, N, NB, NM>, NM extends AreaChartViewerMixin<FxN, N, NB, NM>>

        extends FxXYChartViewer<FxN, N, NB, NM>
        implements AreaChartViewerMixin<FxN, N, NB, NM> {

    public FxAreaChartViewer() {
        super((NB) new AreaChartViewerBase());
    }

    @Override
    protected FxN createFxNode() {
        javafx.scene.chart.AreaChart<Number, Number> areaChart = new javafx.scene.chart.AreaChart<>(createNumberAxis(), createNumberAxis());
        areaChart.setCreateSymbols(false);
        return (FxN) areaChart;
    }
}
