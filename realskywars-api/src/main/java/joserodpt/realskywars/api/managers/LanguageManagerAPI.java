package joserodpt.realskywars.api.managers;

import java.util.List;

public abstract class LanguageManagerAPI {
    public abstract void loadLanguages();

    public abstract String getDefaultLanguage();

    public abstract boolean areLanguagesEmpty();

    public abstract List<String> getLanguages();

    public abstract String getPrefix();
}
