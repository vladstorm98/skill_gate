package com.skill_gate.config;

public final class BotConstants {

    private BotConstants() {}

    public static final int PAGE_SIZE = 5;
    public static final String ADMIN_MENU = "admin_menu";

    public static final class Position {
        public static final String NEW = "new_position";
        public static final String VIEW = "view_position:";
        public static final String EDIT = "edit_position:";
        public static final String EDIT_NAME = "edit_position_name";
        public static final String EDIT_DESCRIPTION = "edit_position_description";
        public static final String DELETE = "delete_position:";
        public static final String CONFIRM_DELETE = "confirm_delete_position:";
        public static final String REJECT_DELETE = "reject_delete_position:";
        public static final String CHOOSE = "choose_position:";
        public static final String PAGE = "positions_page_";
        public static final String PAGE_DEFAULT = "positions_page_1";

        private Position() {}
    }

    public static final class Lesson {
        public static final String NEW = "new_lesson";
        public static final String TOGGLE_NEW_POSITION = "toggle_new_lesson_position:";
        public static final String CONFIRM_NEW_POSITIONS = "confirm_new_lesson_positions";
        public static final String VIEW = "view_lesson:";
        public static final String VIEW_LIST = "view_lessons";
        public static final String EDIT = "edit_lesson:";
        public static final String EDIT_TITLE = "edit_lesson_title";
        public static final String EDIT_NUMBER = "edit_lesson_number";
        public static final String EDIT_VIDEO_URL = "edit_lesson_video_url";
        public static final String EDIT_SUMMARY = "edit_lesson_summary";
        public static final String DELETE = "delete_lesson:";
        public static final String CONFIRM_DELETE = "confirm_delete_lesson:";
        public static final String REJECT_DELETE = "reject_delete_lesson:";
        public static final String PAGE = "lessons_page_";
        public static final String PAGE_DEFAULT = "lessons_page_1";

        private Lesson() {}
    }

    public static final class Employee {
        public static final String PAGE = "employees_page_";
        public static final String PAGE_DEFAULT = "employees_page_1";

        public static final String FILTERS = "employee_filters";
        public static final String FILTERS_APPLY = "employee_filters_apply";
        public static final String FILTERS_RESET = "employee_filters_reset";
        public static final String FILTER_STATUS = "employee_filter_status";
        public static final String FILTER_ROLE = "employee_filter_role";
        public static final String FILTER_POSITION = "employee_filter_position";

        public static final String SET_FILTER_STATUS = "set_employee_filter_status:";
        public static final String SET_FILTER_ROLE = "set_employee_filter_role:";
        public static final String SET_FILTER_POSITION = "set_employee_filter_position:";

        public static final String VIEW = "view_employee:";
        public static final String EDIT_ROLE = "edit_employee_role:";
        public static final String EDIT_STATUS = "edit_employee_status:";
        public static final String EDIT_POSITION = "edit_employee_position:";
        public static final String SET_ROLE = "set_employee_role:";
        public static final String SET_STATUS = "set_employee_status:";
        public static final String SET_POSITION = "set_employee_position:";

        private Employee() {}
    }

    public static final class Check {
        public static final String PAGE = "checks_page_";           // checks_page_{lessonId}_{page}
        public static final String VIEW = "view_check:";
        public static final String NEW = "new_check:";               // new_check:{lessonId}
        public static final String DELETE = "delete_check:";
        public static final String CONFIRM_DELETE = "confirm_delete_check:";
        public static final String REJECT_DELETE = "reject_delete_check:";

        private Check() {}
    }

    public static final class Question {
        public static final String PAGE = "questions_page_";         // questions_page_{checkId}_{page}
        public static final String VIEW = "view_question:";
        public static final String NEW = "new_question:";             // new_question:{checkId}
        public static final String EDIT = "edit_question:";
        public static final String EDIT_TEXT = "edit_question_text";
        public static final String EDIT_NUMBER = "edit_question_number";
        public static final String DELETE = "delete_question:";
        public static final String CONFIRM_DELETE = "confirm_delete_question:";
        public static final String REJECT_DELETE = "reject_delete_question:";

        public static final String TOGGLE_ANSWER = "toggle_answer:";  // toggle_answer:{optionId}
        public static final String NEW_ANSWER = "new_answer:";         // new_answer:{questionId}
        public static final String DELETE_ANSWER = "delete_answer:";

        private Question() {}
    }

    /** Callbacks for completed employee (teacher) knowledge-base panel */
    public static final class Knowledge {
        public static final String MENU = "kb_menu";
        public static final String LESSONS_PAGE = "kb_lessons_page_";  // kb_lessons_page_{page}
        public static final String VIEW_LESSON = "kb_view_lesson:";
        public static final String PROGRESS = "kb_progress";

        private Knowledge() {}
    }
}
