package client;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;

public class TestObservableUtils {
    public static ObservableValue<String> stringToObservable(String string){
        return Bindings.createStringBinding(()-> string);
    }
}
