package com.skill_gate.service;

import com.skill_gate.entity.DbAnswerOption;
import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbQuestion;
import com.skill_gate.exception.AnswerOptionNotFoundException;
import com.skill_gate.exception.QuestionNotFoundException;
import com.skill_gate.repository.AnswerOptionRepository;
import com.skill_gate.repository.QuestionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    private static final String DEFAULT_TEXT = "_blank_";
    private static final Long DEFAULT_NUMBER = 1000L;

    public DbQuestion getQuestion(@NonNull Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question with id #" + id + " not found"));
    }

    public Page<DbQuestion> getPage(DbCheck check, int page, int size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("number"));
        var result = questionRepository.findByCheckOrderByNumberAsc(check, pageable);
        if (result.isEmpty()) {
            throw new QuestionNotFoundException("Questions not found");
        }
        return result;
    }

    public List<DbQuestion> getQuestions(DbCheck check) {
        var questions = questionRepository.findByCheckOrderByNumberAsc(check);
        if (questions.isEmpty()) {
            throw new QuestionNotFoundException("Questions not found");
        }
        return questions;
    }

    public List<DbAnswerOption> getAnswerOptions(DbQuestion question) {
        return answerOptionRepository.findByQuestionOrderByIdAsc(question);
    }

    /** For onboarding — must have answers */
    public List<DbAnswerOption> getRequiredAnswerOptions(DbQuestion question) {
        var options = getAnswerOptions(question);
        if (options.isEmpty()) {
            throw new AnswerOptionNotFoundException(
                    "Answer options for question #" + question.getId() + " not found");
        }
        return options;
    }

    public DbAnswerOption getAnswerOption(@NonNull Long id) {
        return answerOptionRepository.findById(id)
                .orElseThrow(() -> new AnswerOptionNotFoundException(
                        "Answer option with id #" + id + " not found"));
    }

    @Transactional
    public DbQuestion createQuestion(DbCheck check) {
        Long nextNumber = questionRepository.findMaxNumberByCheck(check).orElse(0L) + 1;

        var question = new DbQuestion(check, nextNumber, DEFAULT_TEXT);
        questionRepository.save(question);

        log.info("Question #{} (number {}) created for check #{}",
                question.getId(), nextNumber, check.getId());
        return question;
    }

    @Transactional
    public void updateText(Long id, @NonNull String text) {
        var question = getQuestion(id);
        question.setText(text);
        questionRepository.save(question);
    }

    @Transactional
    public void updateNumber(Long id, @NonNull Long number) {
        var question = getQuestion(id);
        question.setNumber(number);
        questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(@NonNull Long id) {
        var question = getQuestion(id);
        answerOptionRepository.deleteByQuestion(question);
        questionRepository.delete(question);
        log.info("Question #{} deleted", id);
    }

    @Transactional
    public DbAnswerOption createAnswerOption(DbQuestion question, String text, boolean correct) {
        var option = new DbAnswerOption(question, text, correct);
        answerOptionRepository.save(option);
        return option;
    }

    @Transactional
    public void toggleCorrect(Long optionId) {
        var option = getAnswerOption(optionId);
        option.setIsCorrect(!option.getIsCorrect());
        answerOptionRepository.save(option);
    }

    @Transactional
    public void deleteAnswerOption(@NonNull Long optionId) {
        answerOptionRepository.deleteById(optionId);
    }
}
