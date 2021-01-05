package nextstep.subway.line.application;

import nextstep.subway.advice.exception.LineNotFoundException;
import nextstep.subway.advice.exception.StationNotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private StationRepository stationRepository;
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = getStationById(request.getUpStationId());
        Station downStation = getStationById(request.getDownStationId());
        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }

    public void deleteLine(Long id) {
        lineRepository.delete(getLindById(id));
    }

    public LineResponse modifyLine(Long id, LineRequest request) {
        Line line = getLindById(id);
        line.updateLine(request.getName(), request.getColor());
        return LineResponse.of(line);
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(getLindById(id));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    private Line getLindById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new LineNotFoundException(id));
    }

    private Station getStationById(Long id) {
        return stationRepository.findById(id).orElseThrow(() -> new StationNotFoundException(id));
    }
}
