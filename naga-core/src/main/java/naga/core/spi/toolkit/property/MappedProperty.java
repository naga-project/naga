package naga.core.spi.toolkit.property;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import naga.core.spi.platform.Platform;
import naga.core.util.function.Converter;

/**
 * @author Bruno Salmon
 */
public class MappedProperty<A, B> extends MappedObservableValue<A, B> implements Property<A> {

    private A dontGarbageA; // to avoid garbage collection
    private final Property<B> property;
    private final Converter<A, B> aToBConverter;

    public MappedProperty(Property<B> property, Converter<A, B> aToBConverter, Converter<B, A> bToAConverter) {
        super(property, bToAConverter);
        this.property = property;
        this.aToBConverter = aToBConverter;
    }

    @Override
    public void setValue(A value) {
        dontGarbageA = value;
        property.setValue(aToBConverter.convert(value));
    }

    @Override
    public Object getBean() {
        return property.getBean();
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public void bind(ObservableValue<? extends A> observable) {
        property.bind(new MappedObservableValue<>((ObservableValue<A>) observable, aToBConverter));
    }

    @Override
    public void unbind() {
        property.unbind();
    }

    @Override
    public boolean isBound() {
        return property.isBound();
    }

    @Override
    public void bindBidirectional(Property<A> other) {
        property.bindBidirectional(new MappedProperty<>(other, bToAConverter, aToBConverter));
    }

    @Override
    public void unbindBidirectional(Property<A> other) {
        Platform.log("MappedProperty.unbindBidirectional() not yet implemented");
        //TODO property.unbindBidirectional(new MappedProperty<>(other, bToAConverter, aToBConverter));
    }
}
