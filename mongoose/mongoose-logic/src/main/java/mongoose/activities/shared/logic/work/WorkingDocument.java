package mongoose.activities.shared.logic.work;

import mongoose.activities.shared.logic.time.DateTimeRange;
import mongoose.activities.shared.logic.time.DaysArray;
import mongoose.activities.shared.logic.time.DaysArrayBuilder;
import mongoose.activities.shared.logic.time.TimeInterval;
import mongoose.entities.*;
import mongoose.entities.markers.HasPersonDetails;
import mongoose.services.EventService;
import mongoose.services.PersonService;
import naga.commons.util.async.Batch;
import naga.commons.util.async.Future;
import naga.commons.util.collection.Collections;
import naga.framework.expression.sqlcompiler.sql.SqlCompiled;
import naga.framework.orm.domainmodel.DataSourceModel;
import naga.framework.orm.domainmodel.DomainModel;
import naga.framework.orm.entity.EntityList;
import naga.framework.orm.entity.EntityStore;
import naga.framework.orm.entity.UpdateStore;
import naga.framework.orm.mapping.QueryResultSetToEntityListGenerator;
import naga.platform.services.query.QueryArgument;
import naga.platform.services.query.QueryResultSet;
import naga.platform.services.update.UpdateArgument;
import naga.platform.services.update.UpdateResult;
import naga.platform.spi.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Bruno Salmon
 */
public class WorkingDocument {

    private final EventService eventService;
    private final Document document;
    private final List<WorkingDocumentLine> workingDocumentLines;
    private UpdateStore updateStore;

    public WorkingDocument(EventService eventService, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, PersonService.getOrCreate(eventService.getEventDataSourceModel()).getPreselectionProfilePerson(), workingDocumentLines);
    }

    public WorkingDocument(EventService eventService, Person person, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, createDocument(person, person.getStore().getDataSourceModel()), workingDocumentLines);
    }

    public WorkingDocument(EventService eventService, Document document, List<WorkingDocumentLine> workingDocumentLines) {
        this.eventService = eventService;
        this.document = document;
        this.workingDocumentLines = workingDocumentLines;
        Collections.forEach(workingDocumentLines, wdl -> wdl.setWorkingDocument(this));
    }

    public EventService getEventService() {
        return eventService;
    }

    public Document getDocument() {
        return document;
    }

    public List<WorkingDocumentLine> getWorkingDocumentLines() {
        return workingDocumentLines;
    }

    private DateTimeRange dateTimeRange;

    public DateTimeRange getDateTimeRange() {
        if (dateTimeRange == null) {
            long includedStart = Long.MAX_VALUE, excludedEnd = Long.MIN_VALUE;
            for (WorkingDocumentLine wdl : getWorkingDocumentLines()) {
                if (isWorkingDocumentLineToBeIncludedInWorkingDocumentDateTimeRange(wdl)) {
                    DateTimeRange wdlDateTimeRange = wdl.getDateTimeRange();
                    if (wdlDateTimeRange != null && !wdlDateTimeRange.isEmpty()) {
                        TimeInterval interval = wdlDateTimeRange.getInterval().changeTimeUnit(TimeUnit.MINUTES);
                        includedStart = Math.min(includedStart, interval.getIncludedStart());
                        excludedEnd = Math.max(excludedEnd, interval.getExcludedEnd());
                    }
                }
            }
            dateTimeRange = new DateTimeRange(new TimeInterval(includedStart, excludedEnd, TimeUnit.MINUTES));
        }
        return dateTimeRange;
    }

    private boolean isWorkingDocumentLineToBeIncludedInWorkingDocumentDateTimeRange(WorkingDocumentLine wdl) {
        return wdl.getDayTimeRange() != null; // Excluding lines with no day time range (ex: diet option)
    }

    public WorkingDocument applyBusinessRules() {
        applyBreakfastRule();
        applyDietRule();
        return this;
    }

    private void applyBreakfastRule() {
        if (!hasAccommodation())
            workingDocumentLines.remove(getBreakfastLine());
        else if (!hasBreakfast()) {
            Option breakfastOption = eventService.getBreakfastOption();
            if (breakfastOption != null)
                breakfastLine = addNewDependantLine(breakfastOption, getAccommodationLine(), 1);
        }
    }

    private void applyDietRule() {
        if (!hasLunch() && !hasSupper()) {
            if (hasDiet())
                workingDocumentLines.remove(getDietLine());
        } else {
            WorkingDocumentLine dietLine = getDietLine();
            if (dietLine == null) {
                Option dietOption = eventService.getDietOption();
                if (dietOption == null)
                    return;
                dietLine = new WorkingDocumentLine(dietOption, this);
                workingDocumentLines.add(dietLine);
            }
            DaysArrayBuilder dab = new DaysArrayBuilder();
            if (hasLunch())
                dab.addSeries(getLunchLine().getDaysArray().toSeries(), null);
            if (hasSupper())
                dab.addSeries(getSupperLine().getDaysArray().toSeries(), null);
            dietLine.setDaysArray(dab.build());
        }
    }

    //// Accommodation line

    private WorkingDocumentLine accommodationLine;

    public WorkingDocumentLine getAccommodationLine() {
        if (accommodationLine == null)
            accommodationLine = Collections.findFirst(workingDocumentLines, wdl -> wdl.getOption().isAccommodation());
        return accommodationLine;
    }

    private boolean hasAccommodation() {
        return getAccommodationLine() != null;
    }

    //// Breakfast line

    private WorkingDocumentLine breakfastLine;

    private WorkingDocumentLine getBreakfastLine() {
        if (breakfastLine == null)
            breakfastLine = Collections.findFirst(workingDocumentLines, wdl -> wdl.getOption().isBreakfast());
        return breakfastLine;
    }

    private boolean hasBreakfast() {
        return getBreakfastLine() != null;
    }

    //// Lunch line

    private WorkingDocumentLine lunchLine;

    private WorkingDocumentLine getLunchLine() {
        if (lunchLine == null)
            lunchLine = Collections.findFirst(workingDocumentLines, wdl -> wdl.getOption().isLunch());
        return lunchLine;
    }

    private boolean hasLunch() {
        return getLunchLine() != null;
    }

    //// Supper line

    private WorkingDocumentLine supperLine;

    private WorkingDocumentLine getSupperLine() {
        if (supperLine == null)
            supperLine = Collections.findFirst(workingDocumentLines, wdl -> wdl.getOption().isSupper());
        return supperLine;
    }

    private boolean hasSupper() {
        return getSupperLine() != null;
    }


    //// Diet line

    private WorkingDocumentLine dietLine;

    private WorkingDocumentLine getDietLine() {
        if (dietLine == null)
            dietLine = Collections.findFirst(workingDocumentLines, wdl -> wdl.getOption().isDiet());
        return dietLine;
    }

    private boolean hasDiet() {
        return getDietLine() != null;
    }

    private WorkingDocumentLine addNewDependantLine(Option dependantOption, WorkingDocumentLine masterLine, long shiftDays) {
        WorkingDocumentLine dependantLine = new WorkingDocumentLine(dependantOption, this);
        workingDocumentLines.add(dependantLine);
        applySameAttendances(dependantLine, masterLine, shiftDays);
        return dependantLine;
    }

    private void applySameAttendances(WorkingDocumentLine dependantLine, WorkingDocumentLine masterLine, long shiftDays) {
        dependantLine.setDaysArray(masterLine.getDaysArray().shift(shiftDays));
    }

    public void syncPersonDetails(HasPersonDetails p) {
        syncPersonDetails(p, document);
    }

    private UpdateStore getUpdateStore() {
        if (updateStore == null) {
            if (document.getStore() instanceof UpdateStore)
                updateStore = (UpdateStore) document.getStore();
            else
                updateStore = UpdateStore.create(eventService.getEventDataSourceModel());
        }
        return updateStore;
    }

    private static Document createDocument(Person person, DataSourceModel dataSourceModel) {
        UpdateStore store = UpdateStore.create(dataSourceModel);
        Document document = store.insertEntity(Document.class);
        syncPersonDetails(person, document);
        return document;
    }

    private static void syncPersonDetails(HasPersonDetails p1, HasPersonDetails p2) {
        p2.setFirstName(p1.getFirstName());
        p2.setLastName(p1.getLastName());
        p2.setAge(p1.getAge());
        p2.setUnemployed(p1.isUnemployed());
        p2.setFacilityFee(p1.isFacilityFee());
        p2.setWorkingVisit(p1.isWorkingVisit());
        p2.setDiscovery(p1.isDiscovery());
        p2.setDiscoveryReduced(p1.isDiscoveryReduced());
        p2.setGuest(p1.isGuest());
        p2.setResident(p1.isResident());
        p2.setResident2(p1.isResident2());
    }

    public Future<Batch<UpdateResult>> submit() {
        UpdateStore store = getUpdateStore();
        document.setEvent(eventService.getEvent());
        if (document.getFirstName() == null) {
            document.setFirstName("Bruno");
            document.setLastName("Salmon");
        }
        for (WorkingDocumentLine wdl : workingDocumentLines) {
            DocumentLine dl = wdl.getDocumentLine();
            if (dl == null) {
                dl = store.insertEntity(DocumentLine.class);
                wdl.setDocumentLine(dl);
                dl.setDocument(document);
                dl.setSite(wdl.getSite());
                dl.setItem(wdl.getItem());
                DaysArray daysArray = wdl.getDaysArray();
                for (int i = 0, n = daysArray.getArray().length; i < n; i++) {
                    Attendance a = store.insertEntity(Attendance.class);
                    a.setDate(daysArray.getDate(i));
                    a.setDocumentLine(dl);
                }
            }
        }
        return store.executeUpdate(new UpdateArgument[]{new UpdateArgument("select set_transaction_parameters(false)", null, false, eventService.getEventDataSourceModel().getId())});
    }

    public static Future<WorkingDocument> load(EventService eventService, Document document) {
        DataSourceModel dataSourceModel = eventService.getEventDataSourceModel();
        Object dataSourceId = dataSourceModel.getId();
        DomainModel domainModel = dataSourceModel.getDomainModel();
        SqlCompiled sqlCompiled1 = domainModel.compileSelect("select <frontend_cart> from DocumentLine where site!=null and document=?");
        SqlCompiled sqlCompiled2 = domainModel.compileSelect("select documentLine,date from Attendance where documentLine.document=? order by date");
        Object[] documentIdParameter = {document.getId().getPrimaryKey()};
        Future<Batch<QueryResultSet>> queryBatchFuture;
        return Future.allOf(eventService.onFeesGroups(), queryBatchFuture = Platform.getQueryService().executeQueryBatch(
                new Batch<>(new QueryArgument[]{
                        new QueryArgument(sqlCompiled1.getSql(), documentIdParameter, dataSourceId),
                        new QueryArgument(sqlCompiled2.getSql(), documentIdParameter, dataSourceId)
                })
        )).compose(v -> {
            Batch<QueryResultSet> b = queryBatchFuture.result();
            EntityStore store = document.getStore();
            EntityList<DocumentLine> dls = QueryResultSetToEntityListGenerator.createEntityList(b.getArray()[0], sqlCompiled1.getQueryMapping(), store, "dl");
            EntityList<Attendance> as = QueryResultSetToEntityListGenerator.createEntityList(b.getArray()[1], sqlCompiled2.getQueryMapping(), store, "a");
            List<WorkingDocumentLine> wdls = new ArrayList<>();
            for (DocumentLine dl : dls)
                wdls.add(new WorkingDocumentLine(dl, Collections.filter(as, a -> a.getDocumentLine() == dl), eventService));
            return Future.succeededFuture(new WorkingDocument(eventService, document, wdls));
        });
    }

}
