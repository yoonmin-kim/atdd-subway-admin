package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.Test;

class LineStationsTest {
    private final Line line = new Line("신분당선", "red");
    private final Station station1 = new Station("강남역");
    private final Station station2 = new Station("판교역");
    private final long from1To2 = 30L;

    @Test
    void 상행종점역과_하행종점역을_등록할_수_있어야_한다() {
        // given
        final LineStations lineStations = new LineStations();

        // when
        lineStations.addFinalStations(line, station1, station2, from1To2);

        // then
        assertThat(lineStations.stations()).containsExactly(StationResponse.of(station1), StationResponse.of(station2));
    }

    @Test
    void 신규역_추가_시_구간의_거리가_0보다_크지_않으면_IllegalStatementException이_발생해야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station upStation = new Station("양재역");

        // when and then
        assertThatThrownBy(() -> lineStations.addStationBySection(line, upStation, station2, 0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 신규역_추가_시_상행선_하행선이_이미_등록된_구간이면_IllegalStatementException이_발생해야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when and then
        assertThatThrownBy(() -> lineStations.addStationBySection(line, station1, station2, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 신규역_추가_시_상행선_하행선이_하나도_등록되지_않은_구간이면_IllegalStatementException이_발생해야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when and then
        assertThatThrownBy(() -> lineStations.addStationBySection(line, new Station("new1"), new Station("new2"), 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행역인_신규역_추가_시_기존_구간의_거리보다_신규_구간의_거리가_짧지_않으면_IllegalArgumentException이_발생해야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station upStation = new Station("양재역");

        // when and then
        assertThatThrownBy(() -> lineStations.addStationBySection(line, upStation, station2, from1To2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상행역인_신규역_추가_시_기존_구간이_존재하면_기존_구간이_수정되어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station upStation = new Station("양재역");
        final long distance = 10L;

        // when
        lineStations.addStationBySection(line, upStation, station2, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(
                                line.getName(),
                                station1.getName(),
                                upStation.getName(),
                                from1To2 - distance),
                        new SectionResponse(
                                line.getName(),
                                upStation.getName(),
                                station2.getName(),
                                distance));
    }

    @Test
    void 상행역인_신규역_추가_시_하행역이_기존_상행종점역이어도_등록이되어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station upStation = new Station("신논현역");
        final long distance = 50L;

        // when
        lineStations.addStationBySection(line, upStation, station1, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), upStation.getName(), station1.getName(), distance),
                        new SectionResponse(line.getName(), station1.getName(), station2.getName(), from1To2));
    }

    @Test
    void 하행역인_신규역_추가_시_기존_구간의_거리보다_신규_구간의_거리가_짧지_않으면_IllegalArgumentException이_발생해야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station downStation = new Station("양재역");

        // when and then
        assertThatThrownBy(() -> lineStations.addStationBySection(line, station1, downStation, from1To2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 하행역인_신규역_추가_시_기존_구간이_존재하면_기존_구간이_수정되어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station downStation = new Station("양재역");
        final long distance = 10L;

        // when
        lineStations.addStationBySection(line, station1, downStation, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), station1.getName(), downStation.getName(), distance),
                        new SectionResponse(line.getName(), downStation.getName(), station2.getName(),
                                from1To2 - distance));
    }

    @Test
    void 하행역인_신규역_추가_시_상행역이_기존_하행종점역이어도_등록되어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station downStation = new Station("정자역");
        final long distance = 50L;

        // when
        lineStations.addStationBySection(line, station2, downStation, distance);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), station1.getName(), station2.getName(), from1To2),
                        new SectionResponse(line.getName(), station2.getName(), downStation.getName(), distance));
    }

    @Test
    void 구간이_2개_있을_때_상행종점역을_삭제할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station middleStation = new Station("양재역");
        final long distance = 10L;
        lineStations.addStationBySection(line, station1, middleStation, distance);

        // when
        lineStations.removeStation(station1);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), middleStation.getName(), station2.getName(),
                                from1To2 - distance));
    }

    @Test
    void 구간이_2개_있을_때_하행종점역을_삭제할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station middleStation = new Station("양재역");
        final long distance = 10L;
        lineStations.addStationBySection(line, station1, middleStation, distance);

        // when
        lineStations.removeStation(station2);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), station1.getName(), middleStation.getName(), distance));
    }

    @Test
    void 구간이_2개_있을_때_두_번째_역을_삭제할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        final Station middleStation = new Station("양재역");
        final long distance = 10L;
        lineStations.addStationBySection(line, station1, middleStation, distance);

        // when
        lineStations.removeStation(middleStation);

        // then
        assertThat(lineStations.sections())
                .containsOnly(
                        new SectionResponse(line.getName(), station1.getName(), station2.getName(), from1To2));
    }

    @Test
    void 구간이_2개_있을_때_등록되지_않은_역을_삭제하면_IllegalArgumentException이_발생해야_한다() {
        // given
        final LineStations lineStations = givenLineStations();
        lineStations.addStationBySection(line, station1, new Station("양재역"), 10L);

        // when and then
        assertThatThrownBy(() -> lineStations.removeStation(new Station("없는역")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 구간이_하나만_있을_때_역을_삭제하면_IllegalStateException이_발생해야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when and then
        assertThatThrownBy(() -> lineStations.removeStation(station1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 지하철역_목록을_조회할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final List<StationResponse> stations = lineStations.stations();

        // then
        assertThat(stations).containsExactly(StationResponse.of(station1), StationResponse.of(station2));
    }

    @Test
    void 구간_목록을_조회할_수_있어야_한다() {
        // given
        final LineStations lineStations = givenLineStations();

        // when
        final List<SectionResponse> sections = lineStations.sections();

        // then
        assertThat(sections).containsExactly(
                new SectionResponse(line.getName(), station1.getName(), station2.getName(), 30L));
    }

    private LineStations givenLineStations() {
        final LineStations lineStations = new LineStations();
        lineStations.addFinalStations(line, station1, station2, from1To2);
        return lineStations;
    }
}