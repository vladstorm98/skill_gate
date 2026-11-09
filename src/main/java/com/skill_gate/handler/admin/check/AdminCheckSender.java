package com.skill_gate.handler.admin.check;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.telegram.TelegramEditor;
import com.skill_gate.handler.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminCheckSender {

    private final TelegramSender sender;
    private final TelegramEditor editor;
    private final AdminCheckKeyboard checkKeyboard;

    public void sendChecks(DbUser admin, Long lessonId, String lessonTitle,
                           Page<DbCheck> page, int currentPage) {
        sender.send(admin,
                "📝 Checks for lesson: %s (Page %d/%d)".formatted(
                        lessonTitle, currentPage, page.getTotalPages()),
                checkKeyboard.prepareChecksKeyboard(
                        lessonId, page.getContent(), currentPage, page.getTotalPages()));
    }

    public void sendViewCheck(DbUser admin, DbCheck check) {
        var text = """
                📝 Check v.%d
                
                Lesson: %s
                Questions: open list below
                """.formatted(check.getVersion(), check.getLesson().getTitle());

        sender.send(admin, text,
                checkKeyboard.prepareViewCheckKeyboard(check.getId(), check.getLesson().getId()));
    }

    public void sendEmpty(DbUser admin, Long lessonId) {
        sender.send(
                admin,
                "No checks found for this lesson.",
                checkKeyboard.prepareEmptyChecksKeyboard(lessonId)
        );
    }

    public void sendDeleteConfirmation(DbUser admin, DbCheck check) {
        var text = "⚠️ Delete check v.%d?".formatted(check.getVersion());
        sender.send(admin, text, checkKeyboard.prepareDeleteConfirmation(check.getId()));
    }

    public void sendDeleteResult(DbUser admin, CallbackQuery callback, boolean confirmed) {
        if (!(callback.maybeInaccessibleMessage() instanceof Message msg)) {
            return;
        }
        var suffix = confirmed ? "\n\n✅ DELETED" : "\n\n❌ CANCELED";
        editor.edit(admin, msg.messageId(), msg.text() + suffix);
    }

    public void sendCreated(DbUser admin, DbCheck check) {
        sender.send(admin, "Check v.%d created.".formatted(check.getVersion()));
    }
}
