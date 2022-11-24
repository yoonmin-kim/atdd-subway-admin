package nextstep.subway.section;

import static nextstep.subway.fixtures.StationTestFixture.setStations;
import static nextstep.subway.fixtures.StationTestFixture.경기광주역;
import static nextstep.subway.fixtures.StationTestFixture.경기광주역ID;
import static nextstep.subway.fixtures.StationTestFixture.모란역;
import static nextstep.subway.fixtures.StationTestFixture.모란역ID;
import static nextstep.subway.fixtures.StationTestFixture.미금역ID;
import static nextstep.subway.fixtures.StationTestFixture.중앙역;
import static nextstep.subway.fixtures.StationTestFixture.중앙역ID;
import static nextstep.subway.section.SectionAcceptanceTestActions.기존노선과_동일하게_상행_하행역을_등록;
import static nextstep.subway.section.SectionAcceptanceTestActions.기존노선의_상행_하행_역과_모두_일치하지_않게_등록;
import static nextstep.subway.section.SectionAcceptanceTestActions.기존역_구간_길이보다_크거나_같은_역을_기존역_사이_등록;
import static nextstep.subway.section.SectionAcceptanceTestActions.노선이_순서대로_등록되어_있다;
import static nextstep.subway.section.SectionAcceptanceTestActions.등록_완료;
import static nextstep.subway.section.SectionAcceptanceTestActions.등록이_불가하다;
import static nextstep.subway.section.SectionAcceptanceTestActions.새로운_역_상행_종점으로_등록;
import static nextstep.subway.section.SectionAcceptanceTestActions.새로운_역_하행_종점으로_등록;
import static nextstep.subway.section.SectionAcceptanceTestActions.역_사이_새로운역_등록;
import static nextstep.subway.section.SectionAcceptanceTestActions.역을_제거하려_하면;
import static nextstep.subway.section.SectionAcceptanceTestActions.제거할_수_없다;
import static nextstep.subway.section.SectionAcceptanceTestActions.지하철_노선_등록되어_있음;
import static nextstep.subway.section.SectionAcceptanceTestActions.해당_구간만_등록되어_있다;
import static nextstep.subway.section.SectionAcceptanceTestActions.해당_구간만_조회된다;
import static nextstep.subway.section.SectionAcceptanceTestActions.해당_구간으로_합쳐지며_길이도_합쳐진다;
import static nextstep.subway.section.SectionAcceptanceTestActions.해당역을_제거한다;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.common.AcceptanceTest;
import nextstep.subway.domain.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("구간 추가 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    StationRepository stationRepository;

    @BeforeEach
    void beforeEach() {
        setStations(stationRepository);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 역 사이에 새로운 역을 등록하면
     * <p>
     * Then 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정 한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addBetween() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 경기광주역ID, 중앙역ID, "7", "id");

        //when
        역_사이_새로운역_등록(경기광주역ID, 모란역ID, "4", lineId);

        //then
//        새로운_길이를_뺀_나머지를_새롭게_추가된_역과의_길이로_설정(lineId, "4", "3");
        등록_완료(lineId, 경기광주역, 모란역, 중앙역);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 새로운 역을 상행 종점으로 등록한다.
     * <p>
     * Then 기존 구간 앞에 상행 종점으로 등록한 구간이 함께 조회된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void prependUpStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 경기광주역ID, 중앙역ID, "7", "id");

        //when
        새로운_역_상행_종점으로_등록(모란역ID, 경기광주역ID, "4", lineId);

        //then
//        기존_구간_앞에_상행_종점으로_등록한_모란역_구간이_함께_조회됨(lineId, "4", "7");
        등록_완료(lineId, 모란역, 경기광주역, 중앙역);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 새로운 역을 하행 종점으로 등록한다.
     * <p>
     * Then 기존 구간 뒤에 하행 종점으로 등록한 구간이 함께 조회된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void appendDownStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 경기광주역ID, 모란역ID, "7", "id");

        //when
        새로운_역_하행_종점으로_등록(모란역ID, 중앙역ID, "4", lineId);

        //then
//        기존_구간_뒤에_하행_종점으로_등록한_중앙역_구간이_함께_조회됨(lineId, "7", "4");
        등록_완료(lineId, 경기광주역, 모란역, 중앙역);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 기존역 구간 길이보다 크거나 같은 역을 기존역 사이에 등록하면
     * <p>
     * Then 등록이 불가하다.
     */
    @DisplayName("기존역 구간 길이보다 크거나 같으면 새로운역 등록 불가하다.")
    @Test
    void validateLength() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 경기광주역ID, 중앙역ID, "7", "id");

        //when
        ExtractableResponse<Response> response = 기존역_구간_길이보다_크거나_같은_역을_기존역_사이_등록(경기광주역ID, 모란역ID, "8", lineId);

        //then
        등록이_불가하다(response);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 기존노선과 동일하게 상행 하행역을 등록하면
     * <p>
     * Then 등록이_불가하다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void validateAlreadyExistsStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 경기광주역ID, 중앙역ID, "7", "id");

        //when
        ExtractableResponse<Response> response = 기존노선과_동일하게_상행_하행역을_등록(경기광주역ID, 중앙역ID, "4", lineId);

        //then
        등록이_불가하다(response);
    }

    /**
     * Given 노선이 등록되어 있다.
     * <p>
     * When 등록하려는 역이 기존노선의 상행 하행 역 모두 일치하지 않으면
     * <p>
     * Then 등록이_불가하다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되지 않으면 추가할 수 없다.")
    @Test
    void validateNotExistsStation() {
        //given
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 경기광주역ID, 중앙역ID, "7", "id");

        //when
        ExtractableResponse<Response> response = 기존노선의_상행_하행_역과_모두_일치하지_않게_등록(모란역ID, 미금역ID, "4", lineId);

        //then
        등록이_불가하다(response);
    }

    /**
     * Given 한 노선에 두개의 구간이 등록되어 있다.
     * <p>
     * When 두 구간에서 가장 마지막 역을 제거하면
     * <p>
     * Then 한 구간 만 조회된다.
     */
    @DisplayName("한 노선에 두개의 구간이 등록 된 상태에서 가장 마지막역을 제거하는 경우")
    @Test
    void deleteLastSectionsAndDownStation() {
        //given
        String lineId = 노선이_순서대로_등록되어_있다(경기광주역ID, 모란역ID, 중앙역ID);

        //when
        해당역을_제거한다(lineId, 중앙역ID);

        //then
        해당_구간만_조회된다(lineId, 경기광주역, 모란역);
    }

    /**
     * Given 한 노선에 두개의 구간이 등록되어 있다.
     * <p>
     * When 앞 구간의 하행역이며 뒷 구간의 상행역에 해당하는 가운데 역을 제거하면
     * <p>
     * Then 앞 구간의 상행역과 뒷 구간의 하행역이 한 구간으로 합쳐지며 구간의 길이도 합쳐진다.
     */
    @DisplayName("한 노선에 두개의 구간이 등록 된 상태에서 가운데 역을 제거하는 경우")
    @Test
    void deleteBetweenStationOfSections() {
        //given
        String lineId = 노선이_순서대로_등록되어_있다(경기광주역ID, 모란역ID, 중앙역ID);

        //when
        해당역을_제거한다(lineId, 모란역ID);

        //then
        해당_구간으로_합쳐지며_길이도_합쳐진다(lineId, 경기광주역, 중앙역);
    }

    /**
     * Given 한 노선에 두개의 구간이 등록되어 있다.
     * <p>
     * When 어느 구간에도 속하지 않은 역을 제거하려고 시도하면
     * <p>
     * Then 제거할 수 없다.
     */
    @DisplayName("노선에 등록되어있지 않은 역을 제거하려 하는 경우 제거할_수_없다.")
    @Test
    void deleteNotExistsStationOfSections() {
        //given
        String lineId = 노선이_순서대로_등록되어_있다(경기광주역ID, 모란역ID, 중앙역ID);

        //when
        ExtractableResponse<Response> response = 역을_제거하려_하면(lineId, 미금역ID);

        //then
        제거할_수_없다(response);
    }

    /**
     * Given 구간이 하나인 노선이 등록되어 있다.
     * <p>
     * When 해당 노선의 상행 혹은 하행에 해당하는 역을 제거하려고 시도하면
     * <p>
     * Then 제거할 수 없다.
     */
    @DisplayName("구간이 하나인 노선의 상행 혹은 하행에 해당하는 역을 제거하려 하면 제거할 수 없다.")
    @Test
    void deleteStationOfOneSectionThenThrow() {
        //given
        String lineId = 해당_구간만_등록되어_있다(경기광주역ID, 미금역ID);

        //when
        ExtractableResponse<Response> response = 역을_제거하려_하면(lineId, 미금역ID);

        //then
        제거할_수_없다(response);
    }
}
