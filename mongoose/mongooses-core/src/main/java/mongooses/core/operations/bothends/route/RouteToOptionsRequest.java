package mongooses.core.operations.bothends.route;

import mongooses.core.activities.sharedends.book.options.OptionsRouting;
import mongooses.core.activities.sharedends.logic.preselection.OptionsPreselection;
import mongooses.core.activities.sharedends.logic.work.WorkingDocument;
import mongooses.core.aggregates.EventAggregate;
import webfx.framework.operations.route.RoutePushRequest;
import webfx.platforms.core.client.url.history.History;

/**
 * @author Bruno Salmon
 */
public final class RouteToOptionsRequest extends RoutePushRequest {

    public RouteToOptionsRequest(Object eventId, History history) {
        super(OptionsRouting.getEventOptionsPath(eventId), history);
    }

    public RouteToOptionsRequest(WorkingDocument workingDocument, History history) {
        this(workingDocument, null, history);
    }

    public RouteToOptionsRequest(OptionsPreselection optionsPreselection, History history) {
        this(optionsPreselection.getWorkingDocument(), optionsPreselection, history);
    }

    public RouteToOptionsRequest(WorkingDocument workingDocument, OptionsPreselection optionsPreselection, History history) {
        this(prepareEventServiceAndReturnEventId(workingDocument, optionsPreselection), history);
    }

    private static Object prepareEventServiceAndReturnEventId(WorkingDocument workingDocument, OptionsPreselection optionsPreselection) {
        EventAggregate eventAggregate = workingDocument.getEventAggregate();
        eventAggregate.setSelectedOptionsPreselection(optionsPreselection);
        eventAggregate.setWorkingDocument(optionsPreselection == null ? workingDocument : null);
        Object eventId = workingDocument.getDocument().getEventId();
        if (eventId == null)
            eventId = eventAggregate.getEvent().getId();
        return eventId;
    }

}