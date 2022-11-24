package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.domain.Station;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public abstract class SectionAcceptanceTestActions {

    private static final String PATH_LINE = "/lines";
    private static final String PATH_LINE_PATH_LINE_ID = PATH_LINE + "/{lineId}";
    private static final String PATH_LINE_ID_SECTION = PATH_LINE + "/{lineId}/sections";

    public static String 해당_구간만_등록되어_있다(String stationId1, String stationId2) {
        return 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId2, "7", "id");
    }

    public static String 노선이_순서대로_등록되어_있다(String stationId1, String stationId2, String stationId3) {
        String lineId = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", stationId1, stationId3, "7", "id");
        역_사이_새로운역_등록(stationId1, stationId2, "4", lineId);
        return lineId;
    }

    public static void 해당_구간만_조회된다(String pathVariable, Station station1, Station station2) {
        JsonPath 목록조회결과 = 목록조회(PATH_LINE_ID_SECTION, pathVariable);
        assertThat(목록조회결과.getList("distances", String.class)).containsOnly("4");
        assertThat(목록조회결과.getList("stationNames", String.class))
                .containsExactly(station1.getName(), station2.getName());
    }

    public static void 해당_구간으로_합쳐지며_길이도_합쳐진다(String pathVariable, Station station1, Station station2) {
        JsonPath 목록조회결과 = 목록조회(PATH_LINE_ID_SECTION, pathVariable);
        assertThat(목록조회결과.getList("distances", String.class)).containsOnly("7");
        assertThat(목록조회결과.getList("stationNames", String.class))
                .containsExactly(station1.getName(), station2.getName());
    }

    public static void 제거할_수_없다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static ExtractableResponse<Response> 역을_제거하려_하면(String lineId, String stationId) {
        return 삭제(PATH_LINE_ID_SECTION + "?stationId=" + stationId, lineId);
    }

    public static void 해당역을_제거한다(String lineId, String stationId) {
        ExtractableResponse<Response> response = 삭제(PATH_LINE_ID_SECTION + "?stationId=" + stationId, lineId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static ExtractableResponse<Response> 삭제(String path, String pathVariable) {
        return RestAssured.given().log().all()
                .when().delete(path, pathVariable)
                .then().log().all()
                .extract();
    }

    public static void 등록이_불가하다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 등록_완료(String pathVariable, Station... stations) {
        JsonPath 목록조회결과 = 목록조회(PATH_LINE_PATH_LINE_ID, pathVariable);
        assertThat(목록조회결과.getList("stationNames", String.class))
                .containsExactly(stations[0].getName(), stations[1].getName(), stations[2].getName());
    }

    public static ExtractableResponse<Response> 새로운_역_하행_종점으로_등록(String upStationId, String downStationId,
                                                                 String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 새로운_역_상행_종점으로_등록(String upStationId, String downStationId,
                                                                 String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 기존노선과_동일하게_상행_하행역을_등록(String upStationId, String downStationId,
                                                                      String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 기존노선의_상행_하행_역과_모두_일치하지_않게_등록(String upStationId, String downStationId,
                                                                             String distance, String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 기존역_구간_길이보다_크거나_같은_역을_기존역_사이_등록(String upStationId,
                                                                                String downStationId, String distance,
                                                                                String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static ExtractableResponse<Response> 역_사이_새로운역_등록(String upStationId, String downStationId, String distance,
                                                             String pathVariable) {
        return 생성(구간(upStationId, downStationId, distance), PATH_LINE_ID_SECTION, pathVariable);
    }

    public static String 지하철_노선_등록되어_있음(String name, String color, String upStationId, String downStationId,
                                        String distance, String returnValue) {
        return 생성_값_리턴(노선(name, color, upStationId, downStationId, distance), PATH_LINE, returnValue);
    }

    private static JsonPath 목록조회(String path, String pathVariable) {
        return RestAssured.given().log().all()
                .when().get(path, pathVariable)
                .then().log().all()
                .extract().jsonPath();
    }

    private static String 생성_값_리턴(Map<String, String> paramMap, String path, String returnValue) {
        return 생성(paramMap, path).jsonPath().getString(returnValue);
    }

    private static ExtractableResponse<Response> 생성(Map<String, String> paramMap, String path, String pathVariable) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path, pathVariable)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> 생성(Map<String, String> paramMap, String path) {
        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    private static Map<String, String> 구간(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private static Map<String, String> 노선(String name, String color, String upStationId, String downStationId,
                                          String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
}