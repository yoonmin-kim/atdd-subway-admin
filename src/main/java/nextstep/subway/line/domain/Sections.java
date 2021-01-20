package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

	@OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Section> sections = new ArrayList<>();

	protected Sections() {
	}

	public Sections(Section... sections) {
		this.sections.addAll(Arrays.asList(sections));
	}

	public List<Section> getSections() {
		return sections;
	}

	public void addSection(final Section section) {
		validate(section);
		findByUpStation(section.getUp())
			.ifPresent(existsSection ->
				existsSection.update(section.getDown(), existsSection.getDown(),
					existsSection.minusDistance(section.getDistance())));

		findByDownStation(section.getDown())
			.ifPresent(existsSection ->
				existsSection.update(existsSection.getUp(), section.getUp(),
					existsSection.minusDistance(section.getDistance())));

		this.sections.add(section);
	}

	private void validate(final Section section) {
		List<Station> stations = this.getStations();

		if (stations.isEmpty()) {
			return;
		}

		if (isUpAndDownExists(stations, section)) {
			throw new IllegalArgumentException("상행역과 하행역이 이미 존재합니다.");
		}

		if (isUpAndDownNotExists(stations, section)) {
			throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 포함되어 있지 않습니다.");
		}
	}

	private boolean isUpAndDownExists(final List<Station> stations, final Section section) {
		return stations.contains(section.getUp()) && stations.contains(section.getDown());
	}

	private boolean isUpAndDownNotExists(final List<Station> stations, final Section section) {
		return !stations.contains(section.getUp()) && !stations.contains(section.getDown());
	}

	public Optional<Section> findByUpStation(final Station up) {
		return this.sections.stream()
			.filter(section -> section.equalsUpStation(up))
			.findFirst();
	}

	public Optional<Section> findByDownStation(final Station down) {
		return this.sections.stream()
			.filter(section -> section.equalsDownStation(down))
			.findFirst();
	}

	public List<Station> getStations() {
		return this.sections.stream()
			.flatMap(section -> section.getStations().stream())
			.distinct()
			.collect(Collectors.toList());
	}
}