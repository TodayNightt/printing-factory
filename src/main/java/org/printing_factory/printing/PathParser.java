package org.printing_factory.printing;

import java.nio.file.Path;
import java.util.regex.Pattern;

import de.gmuth.ipp.attributes.MediaSource;
import de.gmuth.ipp.attributes.Sides;

public class PathParser {

	// private MediaSource paperType;

	// public MediaSource getPaperType() {
	// return paperType;
	// }

	private Sides sides;

	public Sides getSides() {
		return sides;
	}

	private Path absPath;

	private Integer copies;

	public Integer getCopies() {
		return copies;
	}

	public PathParser(Path path) {
		// for (String part : parts) {
		// if (part.matches(".*白紙|粉紅紙|黃色牌位|大牌.*"))
		// paperType = intoMediaSource(part);
		// else if (part.matches(".*單面|雙面直向|雙面橫向.*"))
		// sides = intoSides(part);
		// }

		var fileName = path.getFileName().toString();

		Pattern quantity = Pattern.compile(".*\\{q:(?<quantity>\\d*)\\}.*");

		var matcher = quantity.matcher(fileName);
		if (matcher.matches()) {
			copies = Integer.parseInt(matcher.group(1));
		} else {
			copies = 1;
		}

		this.absPath = path;
	}

	public String getFileName() {
		return absPath.getFileName().toString();
	}

	public Path getPath() {
		return absPath;
	}

	private Sides intoSides(String sides) {
		if (sides.matches(".*單面.*")) {
			return Sides.OneSided;
		} else if (sides.matches(".*雙面直向.*")) {
			return Sides.TwoSidedLongEdge;
		} else if (sides.matches(".*雙面橫向.*")) {
			return Sides.TwoSidedShortEdge;
		} else {
			return Sides.OneSided;
		}
	}

	private MediaSource intoMediaSource(String paperType) {
		if (paperType.matches(".*白紙.*")) {
			return MediaSource.Tray1;
		} else if (paperType.matches(".*粉紅紙.*")) {
			return new MediaSource("tray-2");
		} else if (paperType.matches(".*黃色牌位.*")) {
			return new MediaSource("tray-3");
		} else if (paperType.matches(".*大牌*")) {
			return new MediaSource("tray-4");
		} else {
			return MediaSource.ByPassTray;
		}
	}

}
