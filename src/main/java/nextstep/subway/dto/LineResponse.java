package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<StationResponse> stations;

    protected LineResponse() {
    }

    public LineResponse(Long id, String name, String color, LocalDateTime createdDate, LocalDateTime modifiedDate, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getCreatedDate(),
                line.getModifiedDate(),
                Arrays.asList(
                        StationResponse.of(line.getUpStation()),
                        StationResponse.of(line.getDownStation())));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}