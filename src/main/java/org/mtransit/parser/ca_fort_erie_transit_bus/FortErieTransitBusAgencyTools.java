package org.mtransit.parser.ca_fort_erie_transit_bus;

import org.jetbrains.annotations.NotNull;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GAgency;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.regex.Pattern;

import static org.mtransit.commons.StringUtils.EMPTY;

// https://niagaraopendata.ca/dataset/public-transit-routes
// https://maps.niagararegion.ca/googletransit/NiagaraRegionTransit.zip
public class FortErieTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new FortErieTransitBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Fort Erie Transit";
	}

	private static final String FE = "FE_";

	@Override
	public boolean excludeAgency(@NotNull GAgency gAgency) {
		//noinspection deprecation
		if (!gAgency.getAgencyId().startsWith(FE)) {
			return EXCLUDE;
		}
		return super.excludeAgency(gAgency);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final Pattern STARTS_WITH_AGENCY_ID = Pattern.compile("(^fe_([fws])[\\d]{2,4}_)", Pattern.CASE_INSENSITIVE);

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		//noinspection deprecation
		String routeId = gRoute.getRouteId();
		routeId = STARTS_WITH_AGENCY_ID.matcher(routeId).replaceAll(EMPTY);
		return Long.parseLong(routeId);
	}

	private static final String AGENCY_COLOR_GOLD = "A68E34"; // GOLD (from web site CSS)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GOLD;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final Pattern STARTS_WITH_NF_A00_ = Pattern.compile("((^)(((nf|nft)_[a-z]{1,3}[\\d]{2,4}(_)?)+([a-z]{3}(stop))?(stop|sto)?))",
			Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopOriginalId(@NotNull String gStopId) {
		gStopId = STARTS_WITH_NF_A00_.matcher(gStopId).replaceAll(EMPTY);
		return gStopId;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern STARTS_WITH_TO = Pattern.compile("(^to[\\s]*)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = STARTS_WITH_TO.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.cleanSlashes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		return Integer.parseInt(gStop.getStopCode()); // use stop code as stop ID
	}
}
