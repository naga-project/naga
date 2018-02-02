package mongoose.activities.shared.book.event.terms;

import mongoose.activities.shared.book.event.shared.BookingProcessPresentationLogicActivity;
import naga.framework.ui.filter.ReactiveExpressionFilterFactoryMixin;

/**
 * @author Bruno Salmon
 */
class TermsPresentationLogicActivity
        extends BookingProcessPresentationLogicActivity<TermsPresentationModel>
        implements ReactiveExpressionFilterFactoryMixin {

    public TermsPresentationLogicActivity() {
        super(TermsPresentationModel::new, null);
    }

    @Override
    protected void startLogic(TermsPresentationModel pm) {
        // Loading the domain model and setting up the reactive filter
        createReactiveExpressionFilter("{class: 'Letter', where: 'type.terms', limit: '1'}")
                .combine(pm.eventIdProperty(), e -> "{where: 'event=" + e + "'}")
                .combine(getI18n().languageProperty(), lang -> "{columns: '[`html(" + lang + ")`]'}")
                .displayResultSetInto(pm.termsLetterDisplayResultSetProperty())
                .start();
    }
}
