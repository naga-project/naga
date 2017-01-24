package mongoose.activities.shared.application;

import mongoose.activities.shared.bookingform.cart.CartPresentationActivity;
import mongoose.activities.shared.bookingform.fees.FeesPresentationActivity;
import mongoose.activities.shared.bookingform.options.OptionsViewActivity;
import mongoose.activities.shared.bookingform.program.ProgramViewActivity;
import mongoose.activities.shared.bookingform.terms.TermsPresentationActivity;
import mongoose.domainmodel.loader.DomainModelSnapshotLoader;
import naga.commons.util.function.Consumer;
import naga.commons.util.function.Factory;
import naga.framework.activity.combinations.domainpresentation.impl.DomainPresentationActivityContextFinal;
import naga.framework.activity.combinations.viewapplication.ViewApplicationContext;
import naga.framework.activity.combinations.viewdomain.ViewDomainActivityContext;
import naga.framework.activity.combinations.viewdomain.impl.ViewDomainActivityContextFinal;
import naga.framework.activity.combinations.viewdomainapplication.ViewDomainApplicationContext;
import naga.framework.ui.i18n.I18n;
import naga.framework.ui.router.UiRouter;
import naga.fx.properties.Properties;
import naga.platform.activity.Activity;
import naga.platform.activity.ActivityManager;
import naga.platform.bus.call.PendingBusCall;
import naga.platform.spi.Platform;

/**
 * @author Bruno Salmon
 */
public abstract class SharedMongooseApplication implements Activity<ViewDomainActivityContext> {

    protected ViewDomainActivityContext context;

    @Override
    public void onCreate(ViewDomainActivityContext context) {
        this.context = context;
        context.getUiRouter().routeAndMount("/", getContainerActivityFactory(), setupContainedRouter(UiRouter.createSubRouter(context)));
    }

    protected abstract Factory<Activity<ViewDomainActivityContextFinal>> getContainerActivityFactory();

    protected UiRouter setupContainedRouter(UiRouter containedRouter) {
        return containedRouter
            .route("/event/:eventId/fees",    FeesPresentationActivity::new, DomainPresentationActivityContextFinal::new)
            .route("/event/:eventId/terms",   TermsPresentationActivity::new, DomainPresentationActivityContextFinal::new)
            .route("/event/:eventId/program", ProgramViewActivity::new, ViewDomainActivityContextFinal::new)
            .route("/event/:eventId/options", OptionsViewActivity::new, ViewDomainActivityContextFinal::new)
            .route("/cart/:cartUuid", CartPresentationActivity::new, DomainPresentationActivityContextFinal::new)
                ;
    }

    @Override
    public void onStart() {
        context.getUiRouter().start();
    }

    protected static void launchApplication(SharedMongooseApplication mongooseApplication, String[] args) {
        Platform.bus(); // instantiating the platform bus here to open the connection as soon as possible (ex: before loading the model which is time consuming)
        ActivityManager.launchApplication(mongooseApplication, ViewDomainApplicationContext.createViewDomainApplicationContext(
                        DomainModelSnapshotLoader.getDataSourceModel(),
                        I18n.create("mongoose/dictionaries/{lang}.json"),
                        args));
    }

    public static void setLoadingSpinnerVisibleConsumer(Consumer<Boolean> consumer) {
        Properties.consumeInUiThread(Properties.combine(ViewApplicationContext.getViewApplicationContext().windowBoundProperty(), PendingBusCall.pendingCallsCountProperty(),
                (windowBound, pendingCallsCount) -> !windowBound || pendingCallsCount > 0)
                , consumer);
    }

}