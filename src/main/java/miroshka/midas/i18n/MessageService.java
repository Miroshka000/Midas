package miroshka.midas.i18n;

import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.message.I18n;
import org.allaymc.api.message.LangCode;
import org.allaymc.api.utils.TextFormat;

import java.util.Map;
import java.util.Optional;

public final class MessageService {
    public String tr(MessageKey key) {
        return tr(I18n.get().getDefaultLangCode(), key, Map.of());
    }

    public String tr(MessageKey key, Map<String, ?> placeholders) {
        return tr(I18n.get().getDefaultLangCode(), key, placeholders);
    }

    public String tr(EntityPlayer player, MessageKey key) {
        return tr(resolveLangCode(player), key, Map.of());
    }

    public String tr(EntityPlayer player, MessageKey key, Map<String, ?> placeholders) {
        return tr(resolveLangCode(player), key, placeholders);
    }

    public void send(EntityPlayer player, MessageKey key) {
        player.sendMessage(tr(player, key));
    }

    public void send(EntityPlayer player, MessageKey key, Map<String, ?> placeholders) {
        player.sendMessage(tr(player, key, placeholders));
    }

    private String tr(LangCode langCode, MessageKey key, Map<String, ?> placeholders) {
        var template = resolveTranslation(langCode, key.translationKey());
        return TextFormat.colorize(applyPlaceholders(template, placeholders));
    }

    private String resolveTranslation(LangCode langCode, String translationKey) {
        var requested = I18n.get().tr(langCode, translationKey);
        if (!isMissingTranslation(requested, translationKey)) {
            return requested;
        }

        var fallback = I18n.get().tr(I18n.get().getDefaultLangCode(), translationKey);
        if (!isMissingTranslation(fallback, translationKey)) {
            return fallback;
        }

        var english = I18n.get().tr(LangCode.en_US, translationKey);
        if (!isMissingTranslation(english, translationKey)) {
            return english;
        }

        return translationKey;
    }

    private String applyPlaceholders(String template, Map<String, ?> placeholders) {
        var result = template;
        for (var entry : placeholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", String.valueOf(entry.getValue()));
        }
        return result;
    }

    private LangCode resolveLangCode(EntityPlayer player) {
        return Optional.ofNullable(player)
                .map(EntityPlayer::getController)
                .map(controller -> controller.getLoginData().getLangCode())
                .orElse(I18n.get().getDefaultLangCode());
    }

    private boolean isMissingTranslation(String value, String translationKey) {
        return value == null || value.isBlank() || translationKey.equals(value);
    }
}
