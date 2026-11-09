package com.skill_gate.handler.admin.question;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbAnswerOption;
import com.skill_gate.entity.DbQuestion;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.telegram.TelegramEditor;
import com.skill_gate.handler.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminQuestionSender {

    private final TelegramSender sender;
    private final TelegramEditor editor;
    private final AdminQuestionKeyboard questionKeyboard;

    public void sendQuestions(DbUser admin, Long checkId, Page<DbQuestion> page, int currentPage) {
        sender.send(admin,
                "❓ Questions (Page %d/%d)".formatted(currentPage, page.getTotalPages()),
                questionKeyboard.prepareQuestionsKeyboard(
                        checkId, page.getContent(), currentPage, page.getTotalPages()));
    }

    public void sendViewQuestion(DbUser admin, DbQuestion question, List<DbAnswerOption> options) {
        var text = """
                ❓ Question #%d
                
                %s
                
                Answers: (tap to toggle correct)
                """.formatted(question.getNumber(), question.getText());

        sender.send(admin, text,
                questionKeyboard.prepareViewQuestionKeyboard(
                        question.getId(), question.getCheck().getId(), options));
    }

    public void sendEditQuestion(DbUser admin, DbQuestion question) {
        sender.send(admin,
                "What do you want to edit?\n\n#%d. %s".formatted(question.getNumber(), question.getText()),
                questionKeyboard.prepareEditQuestionKeyboard());
    }

    public void sendEditText(DbUser admin) {
        sender.send(admin, "Enter new question text:");
    }

    public void sendEditNumber(DbUser admin) {
        sender.send(admin, "Enter new question number:");
    }

    public void sendNewAnswerText(DbUser admin) {
        sender.send(admin, "Enter answer text:");
    }

    public void sendFieldUpdated(DbUser admin, String field) {
        sender.send(admin, "%s updated.".formatted(field));
    }

    public void sendEmpty(DbUser admin) {
        sender.send(admin, "No questions found.");
    }

    public void sendDeleteConfirmation(DbUser admin, DbQuestion question) {
        sender.send(admin,
                "⚠️ Delete question #%d?".formatted(question.getNumber()),
                questionKeyboard.prepareDeleteConfirmation(question.getId()));
    }

    public void sendDeleteResult(DbUser admin, CallbackQuery callback, boolean confirmed) {
        if (!(callback.maybeInaccessibleMessage() instanceof Message msg)) {
            return;
        }
        editor.edit(admin, msg.messageId(),
                msg.text() + (confirmed ? "\n\n✅ DELETED" : "\n\n❌ CANCELED"));
    }
}
