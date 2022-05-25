package nextstep.subway.dto;

import nextstep.subway.domain.Station;

import java.util.Objects;

public class StationRequest {
    private String name;

    public String getName() {
        return name;
    }

    public Station toStation() {
        return new Station(name);
    }
}
