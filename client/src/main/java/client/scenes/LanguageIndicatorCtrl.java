package client.scenes;

import client.utils.LanguageSwitchUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.util.Locale;

public class LanguageIndicatorCtrl {
    private final Translation translation;
    private final LanguageSwitchUtils utils;

    /**
     * Constructor for the LanguageIndicatorCtrl
     * @param translation - the translation to use
     * @param utils - the language switch utils to use
     */
    @Inject
    public LanguageIndicatorCtrl(Translation translation, LanguageSwitchUtils utils) {
        this.translation = translation;
        this.utils = utils;
    }

    /**
     * Set up the language indicator.
     * Graphics of the indicator are set here.
     * Behavior for changing the language is set here.
     * @param languageIndicator Give language indicator
     */
    public void initializeLanguageIndicator(ComboBox<Locale> languageIndicator) {
        languageIndicator.setItems(utils.getLanguages());
        Callback<ListView<Locale>, ListCell<Locale>> cellFactory = listView -> new ListCell<>() {
            @Override
            public void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if(item.getLanguage().equals("nl"))
                        setText("Dutch");
                    if(item.getLanguage().equals("en"))
                        setText("English");
                    if(item.getLanguage().equals("ro"))
                        setText("Romanian");
                    try {
                        Image img = new Image("images/flags/" + item.getLanguage() + "_flag - small.png");
                        ImageView flag =
                            new ImageView(img);
                        setGraphic(flag);
                    }catch (RuntimeException e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        };
        languageIndicator.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    ImageView flag = loadFlag(item.getLanguage());
                    flag.setFitWidth(getWidth());
                    setGraphic(flag);
                }
            }
        });
        languageIndicator.setCellFactory(cellFactory);
        ReadOnlyObjectProperty<Locale> selectedLanguage = languageIndicator.getSelectionModel().selectedItemProperty();
        utils.setBehavior(selectedLanguage);
    }

    /**
     * Refresh the given language indicator.
     * Its list view will be updated as well.
     * @param languageIndicator Language Indicator combo box
     */
    public void refresh(ComboBox<Locale> languageIndicator) {
        utils.refreshLanguages();
        languageIndicator.getSelectionModel().select(translation.getLocale());
    }

    /**
     * Retrieves the flag of the given language
     * @param lang - Language code of the flag
     * @return ImageView of the language flag
     */
    public ImageView loadFlag(String lang) {
        // Should look if caching is beneficial when adding more languages
        Image defaultLanguage = new Image("images/flags/" + lang + "_flag.png");
        ImageView iv = new ImageView();
        iv.setImage(defaultLanguage);
        iv.setPreserveRatio(true);
        return iv;
    }
}
