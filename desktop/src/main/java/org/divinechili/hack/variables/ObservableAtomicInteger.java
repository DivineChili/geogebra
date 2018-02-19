package org.divinechili.hack.variables;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class ObservableAtomicInteger extends AtomicInteger implements ObservableIntegerValue {
    int oldValue = 0;
    private Vector<ChangeListener<? super  Number>> listeners = new Vector<>();

    public ObservableAtomicInteger() { super(); }
    public ObservableAtomicInteger(int initialValue) { super(initialValue); }

    @Override
    public void addListener(ChangeListener<? super Number> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
        listeners.remove(listener);
    }

    public void setValue(Integer value) {
        oldValue = super.get();
        super.set(value);
        for(ChangeListener<? super Number> listener : listeners)
            listener.changed(this, oldValue, value);
    }

    @Override
    public Number getValue() {
        return super.get();
    }

    @Override
    public void addListener(InvalidationListener listener) {

    }

    @Override
    public void removeListener(InvalidationListener listener) {

    }
}
