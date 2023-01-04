package cn.createlight.cswitch.config;

public enum LanguageConfigKey {
    OP_HELP_COMMAND,
    NOT_ENOUGH_PARAMETER,
    NOT_EXIST_ROOM,
    EXIST_ROOM,
    NOT_FREE_ROOM,
    WRONG_INPUT_SERIAL_NUMBER,
    COMMAND_USE_IN_TERMINAL,
    SETUP_ROOM,
    SUCCESSFUL_CREATE_ROOM,
    SUCCESSFUL_DELETE_ROOM,
    FAIL_DELETE_ROOM,

    GAME_FAIL,
    GAME_START,
    GAME_STOP,
    GAME_PREPARE,

    SIGN_JOIN_FREE_LINE1,
    SIGN_JOIN_FREE_LINE2,
    SIGN_JOIN_FREE_LINE3,
    SIGN_JOIN_FREE_LINE4,
    SIGN_JOIN_NOT_FREE_LINE1,
    SIGN_JOIN_NOT_FREE_LINE2,
    SIGN_JOIN_NOT_FREE_LINE3,
    SIGN_JOIN_NOT_FREE_LINE4,
    SIGN_RULE_LINE1,
    SIGN_RULE_LINE2,
    SIGN_RULE_LINE3,
    SIGN_RULE_LINE4,

    COUNT_TYPE_GAMING,
    COUNT_TYPE_FINISH,
    COUNTDOWN_TYPE_GAMING,
    COUNTDOWN_TYPE_FINISH,

    RULE_FORM_WINDOW_TITLE,
    RULE_FORM_WINDOW_CONTENT,

    SETUP_FIRST_TIP,
    SETUP_LAST_TIP,
    SETUP_ARENA_POINT2,
    SETUP_RULE_POINT,
    SETUP_JOIN_POINT,
    SETUP_BREAK_SIGN,
    SETUP_FINISH,
    SETUP_STEP2_TIP_SUDOKU,

    ;

    public String toConfigKey() {
        return name().toLowerCase().replace("_", "-");
    }
}
