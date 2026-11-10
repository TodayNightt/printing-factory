package org.printing_factory.printing;

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

	private Integer copies;

	public Integer getCopies() {
		return copies;
	}

	public PathParser(String fileName) {
		Pattern quantity = Pattern.compile(".*\\{q:(?<quantity>\\d*)\\}.*");

		var matcher = quantity.matcher(fileName);
		if (matcher.matches()) {
			copies = Integer.parseInt(matcher.group(1));
		} else {
			copies = 1;
		}

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
