package client;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class TestObservableUtils {
    public static ObservableValue<String> stringToObservable(String string){
        return new ObservableValue<>() {
            @Override
            public String getValue() {
                return string;
            }

            @Override
            public void addListener(ChangeListener<? super String> listener) {
            }

            @Override
            public void removeListener(ChangeListener<? super String> listener) {
            }

            @Override
            public void addListener(InvalidationListener listener) {
            }

            @Override
            public void removeListener(InvalidationListener listener) {
            }
        };
    }
}
