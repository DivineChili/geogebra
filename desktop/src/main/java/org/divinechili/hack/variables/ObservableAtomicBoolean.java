package org.divinechili.hack.variables;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class ObservableAtomicBoolean extends AtomicBoolean implements ObservableValue<Boolean> {

    private Vector<ChangeListener<Boolean>> listeners = new Vector<>();

    public ObservableAtomicBoolean() {
        super();
    }
    public ObservableAtomicBoolean(boolean initialValue) { super(initialValue); }

    @Override
    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public void setValue(boolean val) {
        super.set(val);
        for(ChangeListener<Boolean> listener : listeners)
            listener.changed(this, !val, val);
    }

    @Override
    public Boolean getValue() {
        return super.get();
    }

    @Override
    public void addListener(InvalidationListener listener) {

    }

    @Override
    public void removeListener(InvalidationListener listener) {

    }
}
