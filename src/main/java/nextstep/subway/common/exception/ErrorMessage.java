package nextstep.subway.common.exception;

public enum ErrorMessage {
    LINE_NOT_FOUND("존재하지 않는 노선입니다."),
    STATION_NOT_FOUND("존재하지 않는 역입니다."),
    STATION_ALL_NOT_EXISTED_EXCEPTION("구간에 존재하는 역이 없습니다."),
    DISTANCE_NEGATIVE_EXCEPTION("구간의 길이는 양수여야 합니다."),
    SECTION_DUPLICATION("중복된 구간 입니다."),
    SECTION_STATION_NOT_FOUND("구간에 존재하지 않는 역입니다."),
    SECTION_CAN_NOT_DELETE("마지막 구간은 삭제할 수 없습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
