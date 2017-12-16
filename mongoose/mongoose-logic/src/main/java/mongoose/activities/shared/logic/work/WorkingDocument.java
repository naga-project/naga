package mongoose.activities.shared.logic.work;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import mongoose.activities.shared.book.event.options.OptionTree;
import mongoose.activities.shared.logic.time.DateTimeRange;
import mongoose.activities.shared.logic.time.TimeInterval;
import mongoose.activities.shared.logic.work.business.BusinessLines;
import mongoose.activities.shared.logic.work.business.BusinessType;
import mongoose.activities.shared.logic.work.business.logic.WorkingDocumentLogic;
import mongoose.activities.shared.logic.work.price.WorkingDocumentPricing;
import mongoose.entities.Document;
import mongoose.entities.Person;
import mongoose.entities.markers.EntityHasPersonDetails;
import mongoose.entities.markers.HasPersonDetails;
import mongoose.services.EventService;
import naga.framework.orm.entity.Entity;
import naga.framework.orm.entity.EntityStore;
import naga.framework.orm.entity.UpdateStore;
import naga.util.collection.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Bruno Salmon
 */
public class WorkingDocument {

    private final EventService eventService;
    private final Document document;
    private final ObservableList<WorkingDocumentLine> workingDocumentLines;
    private Integer computedPrice;
    private UpdateStore updateStore;
    private WorkingDocument loadedWorkingDocument;
    private boolean changedSinceLastApplyBusinessRules = true;
    private OptionTree optionTree;

    public WorkingDocument(EventService eventService, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, eventService.getPersonService().getPreselectionProfilePerson(), workingDocumentLines);
    }

    public WorkingDocument(EventService eventService, WorkingDocument wd, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, createDocument(wd.getDocument()), workingDocumentLines);
        loadedWorkingDocument = wd.loadedWorkingDocument;
    }

    public WorkingDocument(EventService eventService, Person person, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, createDocument(person), workingDocumentLines);
    }

    public WorkingDocument(EventService eventService, Document document, List<WorkingDocumentLine> lines) {
        this.eventService = eventService;
        this.document = document;
        workingDocumentLines = FXCollections.observableArrayList(lines);
        Collections.forEach(workingDocumentLines, wdl -> wdl.setWorkingDocument(this));
        workingDocumentLines.addListener((ListChangeListener<WorkingDocumentLine>) c -> {
            clearLinesCache();
            clearComputedDateTimeRange();
            clearComputedPrice();
            changedSinceLastApplyBusinessRules = true;
        });
    }

    // Constructor used to make a copy (that can be changed) of a loaded working document (that shouldn't be changed).
    // When submitting this copy, some decisions are made by comparison with the original loaded document.
    public WorkingDocument(WorkingDocument loadedWorkingDocument) {
        this(loadedWorkingDocument.getEventService(), createDocument(loadedWorkingDocument.getDocument()), new ArrayList<>(loadedWorkingDocument.getWorkingDocumentLines()));
        this.loadedWorkingDocument = loadedWorkingDocument;
    }

    public EventService getEventService() {
        return eventService;
    }

    public WorkingDocument getLoadedWorkingDocument() {
        return loadedWorkingDocument;
    }

    public Document getDocument() {
        return document;
    }

    public List<WorkingDocumentLine> getWorkingDocumentLines() {
        return workingDocumentLines;
    }

    public OptionTree getOptionTree() {
        return optionTree;
    }

    public void setOptionTree(OptionTree optionTree) {
        this.optionTree = optionTree;
    }

    private DateTimeRange dateTimeRange;

    public DateTimeRange getDateTimeRange() {
        if (dateTimeRange == null)
            computeDateTimeRange();
        return dateTimeRange;
    }

    private void computeDateTimeRange() {
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
        if (excludedEnd < includedStart)
            includedStart = excludedEnd = 0;
        dateTimeRange = new DateTimeRange(new TimeInterval(includedStart, excludedEnd, TimeUnit.MINUTES));
    }

    void clearComputedDateTimeRange() {
        dateTimeRange = null;
    }

    private static boolean isWorkingDocumentLineToBeIncludedInWorkingDocumentDateTimeRange(WorkingDocumentLine wdl) {
        return wdl.getDayTimeRange() != null; // Excluding lines with no day time range (ex: diet option)
    }

    public WorkingDocument applyBusinessRules() {
        if (changedSinceLastApplyBusinessRules) {
            WorkingDocumentLogic.applyBusinessRules(this);
            changedSinceLastApplyBusinessRules = false;
        }
        return this;
    }

    public void clearComputedPrice() {
        computedPrice = null;
    }

    public int getComputedPrice() {
        if (computedPrice == null)
            computedPrice = computePrice();
        return computedPrice;
    }

    public int computePrice() {
        return computedPrice = WorkingDocumentPricing.computeDocumentPrice(this);
    }

    private Map<BusinessType, BusinessLines> businessLinesMap = new HashMap<>();

    private void clearLinesCache() {
        businessLinesMap.clear();
    }

    public BusinessLines getBusinessLines(BusinessType businessType) {
        BusinessLines businessLines = businessLinesMap.get(businessType);
        if (businessLines == null)
            businessLinesMap.put(businessType, businessLines = new BusinessLines(businessType, this));
        return businessLines;
    }

    public boolean hasBusinessLines(BusinessType businessType) {
        return !getBusinessLines(businessType).isEmpty();
    }

    public void removeBusinessLines(BusinessType businessType) {
        getBusinessLines(businessType).removeAllLines();
    }

    @Deprecated
    public WorkingDocumentLine getBusinessLine(BusinessType businessType) {
        return Collections.first(getBusinessLines(businessType).getBusinessWorkingDocumentLines());
    }

    //// Accommodation line

    @Deprecated
    public WorkingDocumentLine getAccommodationLine() {
        return getBusinessLine(BusinessType.ACCOMMODATION);
    }

    public boolean hasAccommodation() {
        return hasBusinessLines(BusinessType.ACCOMMODATION);
    }

    //// Breakfast line

    public void removeBreakfast() {
        removeBusinessLines(BusinessType.BREAKFAST);
    }

    //// Lunch line

    public boolean hasLunch() {
        return hasBusinessLines(BusinessType.LUNCH);
    }

    //// Supper line

    public boolean hasSupper() {
        return hasBusinessLines(BusinessType.SUPPER);
    }

    public boolean hasMeals() {
        return hasLunch() || hasSupper();
    }


    //// Diet line

    @Deprecated
    public WorkingDocumentLine getDietLine() {
        return getBusinessLine(BusinessType.DIET);
    }

    public void removeDiet() {
        removeBusinessLines(BusinessType.DIET);
    }

    //// TouristTax line

    public boolean hasTouristTax() {
        return hasBusinessLines(BusinessType.TOURIST_TAX);
    }

    public void removeTouristTax() {
        removeBusinessLines(BusinessType.TOURIST_TAX);
    }

    //// Teaching line

    @Deprecated
    public WorkingDocumentLine getTeachingLine() {
        return getBusinessLine(BusinessType.TEACHING);
    }

    public boolean hasTeaching() {
        return hasBusinessLines(BusinessType.TEACHING);
    }

    //// Translation line

    @Deprecated
    public WorkingDocumentLine getTranslationLine() {
        return getBusinessLine(BusinessType.TRANSLATION);
    }

    public boolean hasTranslation() {
        return hasBusinessLines(BusinessType.TRANSLATION);
    }

    public void removeTranslation() {
        removeBusinessLines(BusinessType.TRANSLATION);
    }

    //// Hotel shuttle lines

    public void removeHotelShuttle() {
        removeBusinessLines(BusinessType.HOTEL_SHUTTLE);
    }


    public void syncPersonDetails(HasPersonDetails p) {
        syncPersonDetails(p, document);
    }

    public UpdateStore getUpdateStore() {
        if (updateStore == null)
            updateStore = getUpdateStore(document);
        return updateStore;
    }

    private static UpdateStore getUpdateStore(Entity entity) {
        return getUpdateStore(entity.getStore());
    }

    private static UpdateStore getUpdateStore(EntityStore store) {
        return store instanceof UpdateStore ? (UpdateStore) store : UpdateStore.createAbove(store);
    }

    private static Document createDocument(EntityHasPersonDetails personDetailsEntity) {
        UpdateStore store = getUpdateStore(personDetailsEntity);
        Document document;
        if (personDetailsEntity instanceof Document) // If from an original document, just making a copy
            document = store.copyEntity((Document) personDetailsEntity);
        else // otherwise creating a new document
            syncPersonDetails(personDetailsEntity, document = store.createEntity(Document.class));
        return document;
    }

    public static void syncPersonDetails(HasPersonDetails p1, HasPersonDetails p2) {
        p2.setFirstName(p1.getFirstName());
        p2.setLastName(p1.getLastName());
        p2.setLayName(p1.getLayName());
        p2.setMale(p1.isMale());
        p2.setOrdained(p1.isOrdained());
        p2.setAge(p1.getAge());
        p2.setCarer1Name(p1.getCarer1Name());
        p2.setCarer2Name(p1.getCarer2Name());
        p2.setEmail(p1.getEmail());
        p2.setPhone(p1.getPhone());
        p2.setStreet(p1.getStreet());
        p2.setPostCode(p1.getPostCode());
        p2.setCityName(p1.getCityName());
        p2.setAdmin1Name(p1.getAdmin1Name());
        p2.setAdmin2Name(p1.getAdmin2Name());
        p2.setCountryName(p1.getCountryName());
        p2.setCountry(p1.getCountry());
        p2.setOrganization(p1.getOrganization());
        p2.setUnemployed(p1.isUnemployed());
        p2.setFacilityFee(p1.isFacilityFee());
        p2.setWorkingVisit(p1.isWorkingVisit());
        p2.setDiscovery(p1.isDiscovery());
        p2.setDiscoveryReduced(p1.isDiscoveryReduced());
        p2.setGuest(p1.isGuest());
        p2.setResident(p1.isResident());
        p2.setResident2(p1.isResident2());
    }

    public WorkingDocumentLine findSameWorkingDocumentLine(WorkingDocumentLine wdl) {
        for (WorkingDocumentLine thisWdl : getWorkingDocumentLines()) {
            if (sameLine(thisWdl, wdl))
                return thisWdl;
        }
        return null;
    }

    private static boolean sameLine(WorkingDocumentLine wdl1, WorkingDocumentLine wdl2) {
        return wdl1 == wdl2 || wdl1 != null && Entity.sameId(wdl1.getSite(), wdl2.getSite()) && Entity.sameId(wdl1.getItem(), wdl2.getItem());
    }

}
