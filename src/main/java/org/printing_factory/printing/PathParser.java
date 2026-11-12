package org.printing_factory.printing;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.gmuth.ipp.attributes.MediaSource;
import de.gmuth.ipp.attributes.Sides;

public class PathParser {
	private Sides sides;
	private int quantity;
	private Tray tray;

	public PathParser(String fileName) throws InvalidFileParametersException {
		Pattern parser = Pattern.compile("(q&(?<quantity>\\d+))*(t&(?<tray>\\d+))*(s&(?<sides>s|dv|dh))*");
		Matcher m = parser.matcher(fileName);

		if (!m.hasMatch())
			throw new InvalidFileParametersException();

		Map<String, String> groups = m.namedGroups().entrySet().stream()
				.map((entry) -> new AbstractMap.SimpleImmutableEntry<>(
						entry.getKey(),
						m.group(entry.getValue())))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		parseQuantity(groups);
		parseSides(groups);
		parseTray(groups);
	}

	private void parseQuantity(Map<String, String> groups) {
		Optional<String> quantityGroup = Optional.ofNullable(groups.get("quantity"));
		if (quantityGroup.isPresent()) {
			this.quantity = Integer.parseInt(quantityGroup.get());
		} else {
			this.quantity = 1;
		}
	}

	private void parseSides(Map<String, String> groups) {
		String sidesGroup = Optional.ofNullable(groups.get("sides")).orElse("s");

		this.sides = switch (sidesGroup) {
			case "dv" -> Sides.TwoSidedLongEdge;
			case "dh" -> Sides.TwoSidedShortEdge;
			default -> Sides.OneSided;
		};
	}

	private void parseTray(Map<String, String> groups) {
		Optional<String> trayGroup = Optional.ofNullable(groups.get("tray"));

		this.tray = switch (trayGroup) {
			case Optional<String> opt when opt.isPresent() -> {
				int trayNum = Integer.parseInt(opt.get());
				yield new Tray(MediaSource.Manual, trayNum);
			}
			case Optional<String> opt when opt.isEmpty() -> new Tray(MediaSource.Main, -1);
			default -> new Tray(MediaSource.Main, -1);
		};
	}

	public Sides getSides() {
		return sides;
	}

	public int getQuantity() {
		return quantity;
	}

	public Tray getTray() {
		return tray;
	}

	public record Tray(MediaSource mediaSource, int trayNum) {
		public boolean isCustomTray() {
			return mediaSource == MediaSource.Manual;
		}
	}

	public class InvalidFileParametersException extends Exception {
		InvalidFileParametersException() {
			super();
		}
	}

}
