package client.utils;

import com.google.inject.Inject;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class Translation {
    private final ObservableResourceFactory resourceFactory;
    private Locale locale;
    public static final String LANGUAGE_PATH = "client/lang/";
    private static final String LANGUAGE_PREFIX = "lang_";

    /***
     * Constructor that takes the resourceFactory the Translation encapsulates
     * @param resourceFactory - the ObservableResourceFactory to be used
     */
    @Inject
    public Translation(ObservableResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    /***
     * Provides a binding for the translated value corresponding to a text key
     * @param key the text key (e.g. "startup.label.create")
     * @return a live-updating binding for the translated value
     */
    public ObservableValue<String> getStringBinding(String key) {
        return resourceFactory.getStringBinding(key);
    }

    /***
     * Provides a binding for the translated value corresponding to a text key, with placeholders replaced based on the given Map
     * @param key the text key (e.g. "startup.label.create")
     * @param values the values to substitute in the final String
     * @return a live-updating binding for the translated value (values do NOT live update)
     */
    public ObservableValue<String> getStringSubstitutionBinding(String key, Map<String, String> values) {
        return resourceFactory.getStringSubstitutionBinding(key, values);
    }

    /***
     * Changes the language resources supplied to those from a corresponding language file
     * @param locale - the Local corresponding to the language file to switch to
     */
    public void changeLanguage(Locale locale) {
        String languageCode = locale.getLanguage();
        String fileName = LANGUAGE_PREFIX.concat(languageCode).concat(".properties");

        Properties languageProperties = new Properties();
        File file = new File(LANGUAGE_PATH, fileName);
        try {
            languageProperties.load(new FileReader(file));
        } catch (IOException e) {
            // no such localization file
            if(ConfigUtils.DEFAULT_PROPS_LANGUAGE.equals(locale.getLanguage())){
                // a default language is missing!!!
                throw new RuntimeException("Bundled language files could not be found");
            }
            else{
                // try to load default instead
                changeLanguage(Locale.forLanguageTag(ConfigUtils.DEFAULT_PROPS_LANGUAGE));
                return;
            }
        }
        this.locale = locale;
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, String> map = new HashMap<String, String>((Map) languageProperties);
        resourceFactory.setResources(map);
    }

    /***
     * Returns the Locale currently in use
     * @return the Locale currently in use
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Binds a label to a text
     * @param label the label to bind
     * @param binding the binding to use
     */
    public void bindLabel(Label label, String binding){
        label.textProperty().bind(this.getStringBinding(binding));
    }

    /**
     * Binds a textfield to a text
     * @param textField the textfield to bind
     * @param binding the binding to use
     */
    public void bindTextField(TextField textField, String binding){
        textField.textProperty().bind(this.getStringBinding(binding));
    }
    /**
     * Binds a textfield's prompt to a text
     * @param textField the textfield to bind
     * @param binding the binding to use
     */
    public void bindTextFieldPrompt(TextField textField, String binding){
        textField.promptTextProperty().bind(this.getStringBinding(binding));
    }

    /**
     * Binds a button to a text
     * @param button the button to bind
     * @param binding the binding to use
     */
    public void bindButton(Button button, String binding){
        button.textProperty().bind(this.getStringBinding(binding));
    }

}
