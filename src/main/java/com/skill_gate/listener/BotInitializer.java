package com.skill_gate.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.skill_gate.exception.BotExceptionHandler;
import com.skill_gate.exception.ProcessingUpdateException;
import com.skill_gate.handler.BotUpdateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotInitializer {

    private final TelegramBot bot;
    private final BotUpdateHandler botUpdateHandler;
    private final BotExceptionHandler botExceptionHandler;

    /**
     * Initializes the Telegram Bot after the Spring bean is constructed.
     * Sets up the updates listener to receive messages and callback queries.
     */
    @PostConstruct
    public void init() {

        bot.setUpdatesListener(updates -> {

            for (var update : updates) {
                try {
                    botUpdateHandler.processUpdate(update);

                } catch (ProcessingUpdateException e) {
                    botExceptionHandler.handle(update, e);
                }
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

        log.info("Telegram bot successfully started");
    }

    /**
     * Gracefully shuts down the Telegram Bot when the Spring application is stopping.
     *
     * <p>This method ensures that the bot's resources are properly released
     * and long polling is stopped cleanly.</p>
     */
    @PreDestroy
    public void shutdown() {
        if (bot != null) {
            try {
                bot.shutdown();
                log.info("Telegram Bot has been successfully shut down.");
            } catch (Exception e) {
                log.warn("Error during Telegram Bot shutdown", e);
            }
        }
    }
}
