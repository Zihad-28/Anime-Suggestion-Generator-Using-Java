package animesuggestiongenerator;

public class FilterMaker {

    private final String yearFilter;
    private final String typeFilter;
    private final String originFilter;

    public FilterMaker(String yearFilter, String typeFilter, String originFilter) {
        this.yearFilter   = cleanFilter(yearFilter,   "Any Year");
        this.typeFilter   = cleanFilter(typeFilter,   "Any Type");
        this.originFilter = cleanFilter(originFilter, "Any Origin");
    }

    public boolean matches(String chunk, String released, String type, String origin) {

        if (!yearFilter.equals("Any Year")) {
            if (released.equals("N/A") || !released.startsWith(yearFilter)) {
                return false;
            }
        }

        if (!typeFilter.equals("Any Type")) {
            if (type.equals("N/A") || !type.equalsIgnoreCase(typeFilter)) {
                return false;
            }
        }

        if (!originFilter.equals("Any Origin")) {
            if (origin.equals("N/A") || !origin.equalsIgnoreCase(originFilter)) {
                return false;
            }
        }

        return true;
    }

    private String cleanFilter(String value, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }
}