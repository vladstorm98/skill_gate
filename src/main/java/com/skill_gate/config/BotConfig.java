package com.skill_gate.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.skill_gate.exception.BotExceptionHandler;
import com.skill_gate.exception.ProcessingUpdateException;
import com.skill_gate.handler.BotUpdateHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for initializing and starting the Telegram Bot.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    /**
     * Provides TelegramBot bean to be injected into services.
     *
     * @return the initialized TelegramBot instance
     */
    @Bean
    public TelegramBot bot() {
        validateBotToken();

        return new TelegramBot(botToken);
    }

    /**
     * Validates that the bot token is present and looks valid.
     */
    private void validateBotToken() {
        if (botToken == null || botToken.trim().isEmpty()) {
            log.error("Telegram bot token is not configured! Check 'telegram.bot.token' in application.yml");
            throw new IllegalStateException("Telegram bot token is missing");
        }

        if (botToken.length() < 30) {
            log.warn("Bot token looks too short. Please make sure it's a valid token from @BotFather");
        }
    }

}
