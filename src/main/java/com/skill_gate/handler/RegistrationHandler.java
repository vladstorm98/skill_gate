package com.skill_gate.handler;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.service.PositionService;
import com.skill_gate.service.BotService;
import com.skill_gate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Handles the initial user registration flow:
 *
 * <p>Manages the step-by-step collection of user data:
 * <ul>
 *   <li>Full name</li>
 *   <li>Phone number</li>
 *   <li>Position selection</li>
 * </ul>
 * After completion, sends a registration request for administrator approval.
 */
@Component
@RequiredArgsConstructor
public class RegistrationHandler {

    private final BotService botService;
    private final UserService userService;
    private final PositionService positionService;

    public void startRegistration(DbUser user) {
        if (isRegistered(user)) return;

        setCurrentStateAndSave(user, UserState.REGISTRATION_WAITING_FULL_NAME);

        var text = """
                👋 Welcome to the onboarding system!
                
                Please enter your name and surname:""";

        sendMessage(user.getTelegramId(), text);
    }

    /**
     * Processes the user's full name input.
     *
     * @param message incoming message with full name
     * @param user    current user entity
     */
    public void handleFullName(DbUser user, Message message) {
        var fullName = message.text().trim();

        if (fullName.startsWith("/")) return;

        user.setFullName(fullName);
        setCurrentStateAndSave(user, UserState.REGISTRATION_WAITING_PHONE);

        var keyboard = prepareShareContactKeyboard();

        botService.sendMessageWithReplyKeyboard(message.chat().id(),
                "Press the button to share your phone number:", keyboard);
    }

    /**
     * Processes the user's phone number (text input or shared contact).
     *
     * @param message incoming message with phone
     * @param user    current user entity
     */
    public void handlePhone(DbUser user, Message message) {
        var phone = validatePhone(message);

        user.setPhone(phone);
        setCurrentStateAndSave(user, UserState.REGISTRATION_WAITING_POSITION);

        var text = "Please select your position:";
        var keyboard = prepareSelectPositionKeyboard();

        sendMessageWithMarkup(message.chat().id(), text, keyboard);
    }

    /**
     * Handles position selection via inline keyboard callback.
     *
     * @param callbackData callback data from position button
     * @param user         current user entity
     */
    public void handlePositionChoice(DbUser user, String callbackData) {
        if (!isWaitingPosition(user)) return;

        assignPosition(user, callbackData);
        setCurrentStateAndSave(user, UserState.REGISTRATION_WAITING_APPROVAL);
        notifyAdmins(user);

        var text = """
                ✅ Registration completed!
                
                Your application has been sent to the administrator for approval.""";

        sendMessage(user.getTelegramId(), text);
    }

    /**
     * Checks if the user is currently waiting to select a position.
     *
     * @param user the user entity
     * @return {@code true} if the user is in {@link UserState#REGISTRATION_WAITING_POSITION} state
     */
    private boolean isWaitingPosition(DbUser user) {
        return user.getCurrentState() == UserState.REGISTRATION_WAITING_POSITION;
    }

    /**
     * Notifies all administrators about a new registration request.
     *
     * @param user the newly registered user
     */
    private void notifyAdmins(DbUser user) {
        var notification = """
                🆕 New registration request:

                Name: %s
                Phone: %s
                Position: %s
                """.formatted(user.getFullName(), user.getPhone(), user.getPosition().getName());

        getAdmins().forEach(admin -> {
            var keyboard = prepareApprovalAdminKeyboard(user);
            sendMessageWithMarkup(admin.getTelegramId(), notification, keyboard);
        });
    }

    /**
     * Checks whether the user has already completed registration.
     *
     * @param user the user entity
     * @return {@code true} if the user is not in pending approval state
     */
    private boolean isRegistered(DbUser user) {
        return user.getStatus() != UserStatus.NEW;
    }

    /**
     * Extracts and validates the phone number from the message.
     *
     * @param message message that may contain a contact or text
     * @return phone number as string
     */
    private String validatePhone(Message message) {
        return message.contact() != null ? message.contact().phoneNumber() : message.text().trim();
    }

    /**
     * Assigns the selected position to the user.
     *
     * @param user         the user entity
     * @param callbackData callback data containing the position ID
     */
    private void assignPosition(DbUser user, String callbackData) {
        var positionId = Long.parseLong(callbackData.split(":")[1]);
        var position = positionService.getPosition(positionId);

        userService.assignPosition(user, position);
    }

    /**
     * Creates a reply keyboard with a "Share Contact" button.
     *
     * @return {@link ReplyKeyboardMarkup} configured to request contact
     */
    private ReplyKeyboardMarkup prepareShareContactKeyboard() {
        var contactButton = new KeyboardButton("📱 Share Contact")
                .requestContact(true);

        return new ReplyKeyboardMarkup(contactButton)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);
    }

    /**
     * Creates an inline keyboard with all available positions for selection.
     *
     * @return {@link InlineKeyboardMarkup} with position buttons
     */
    private InlineKeyboardMarkup prepareSelectPositionKeyboard() {
        var rows = positionService.getAllPositions().stream()
                .map(position -> new InlineKeyboardButton(position.getName())
                        .callbackData("choose_position:" + position.getId()))
                .map(button -> new InlineKeyboardButton[]{button})
                .toArray(InlineKeyboardButton[][]::new);

        return new InlineKeyboardMarkup(rows);
    }

    /**
     * Creates an approval/rejection keyboard for administrators.
     *
     * @param user the user to be approved or rejected
     * @return {@link InlineKeyboardMarkup} with Approve and Reject buttons
     */
    private InlineKeyboardMarkup prepareApprovalAdminKeyboard(DbUser user) {
        var buttons = new InlineKeyboardButton[]{
                new InlineKeyboardButton("✅ Approve")
                        .callbackData("approve:" + user.getTelegramId()),
                new InlineKeyboardButton("❌ Reject")
                        .callbackData("reject:" + user.getTelegramId())
        };

        return new InlineKeyboardMarkup(buttons);
    }

    /**
     * Retrieves the list of all administrators.
     *
     * @return list of admin users
     */
    private List<DbUser> getAdmins() {
        return userService.getAdmins();
    }

    /**
     * Sets the user's current state and saves it to the database.
     *
     * @param user      the user entity
     * @param userState the new state to set
     */
    private void setCurrentStateAndSave(DbUser user, UserState userState) {
        user.setCurrentState(userState);
        save(user);
    }

    private void save(DbUser user) {
        userService.save(user);
    }

    private void sendMessage(Long chatId, String text) {
        botService.sendMessage(chatId, text);
    }

    private void sendMessageWithMarkup(Long chatId, String text, InlineKeyboardMarkup markup) {
        botService.sendMessageWithMarkup(chatId, text, markup);
    }
}
