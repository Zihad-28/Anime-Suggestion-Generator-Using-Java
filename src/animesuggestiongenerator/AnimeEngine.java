package animesuggestiongenerator;

public class AnimeEngine extends WebConnector {

    public void showSuggestions(String input) {
        showSuggestions(input, "Any Year", "Any Type", "Any Origin");
    }

    public void showSuggestions(String input, String yearFilter, String typeFilter, String originFilter) {

        String genreId = "0";
        String keyword = input.toLowerCase().trim();

        if      (keyword.equals("action"))    genreId = "1";
        else if (keyword.equals("adventure")) genreId = "2";
        else if (keyword.equals("comedy"))    genreId = "4";
        else if (keyword.equals("fantasy"))   genreId = "10";
        else if (keyword.equals("horror"))    genreId = "14";
        else if (keyword.equals("music"))     genreId = "19";
        else if (keyword.equals("harem"))     genreId = "35";
        else if (keyword.equals("sports"))    genreId = "30";
        else if (keyword.equals("isekai"))    genreId = "62";

        System.out.println("\nSearching for: " + input);

        String data;

        if (genreId.equals("0")) {
            data = extractData(keyword);
        } else {
            data = extractData(genreId, keyword);
        }

        if (data.equals("") || data.equals("none")) {
            System.out.println("No results found.");
            return;
        }

        System.out.println("\n--- Top Suggestions ---\n");

        String[] parts = data.split("\"mal_id\":");

        int count = 1;

        FilterMaker filterMaker = new FilterMaker(yearFilter, typeFilter, originFilter);

        for (int i = 1; i < parts.length; i++) {

            String chunk = parts[i];

            String title = "N/A";
            String[] titleSplit = chunk.split("\"title_english\":\"");
            if (titleSplit.length > 1) {
                String raw = titleSplit[1].split("\"")[0];
                if (!raw.equals("null") && !raw.isEmpty()) {
                    title = raw;
                }
            }
            if (title.equals("N/A")) {
                String[] romajiSplit = chunk.split("\"title\":\"");
                if (romajiSplit.length > 1) {
                    String raw = romajiSplit[1].split("\"")[0];
                    if (!raw.equals("null") && !raw.isEmpty()) {
                        title = raw;
                    }
                }
            }

            if (title.equals("N/A") || title.isEmpty()) continue;

            String released = "N/A";
            String[] fromSplit = chunk.split("\"from\":\"");
            if (fromSplit.length > 1) {
                String raw = fromSplit[1].split("\"")[0];
                if (!raw.equals("null") && !raw.isEmpty()) {
                    released = raw.contains("T") ? raw.split("T")[0] : raw;
                }
            }

            String type       = extractAnimeType(chunk);
            String episodes   = extractField(chunk, "\"episodes\":");
            String popularity = extractField(chunk, "\"popularity\":");
            String origin     = detectOrigin(chunk);

            if (!filterMaker.matches(chunk, released, type, origin)) continue;

            String imageUrl = "N/A";
            String[] jpgSplit = chunk.split("\"jpg\":\\{\"image_url\":\"");
            if (jpgSplit.length > 1) {
                String raw = jpgSplit[1].split("\"")[0];
                if (!raw.equals("null") && !raw.isEmpty()) {
                    imageUrl = raw;
                }
            }
            if (imageUrl.equals("N/A")) {
                String[] imgSplit = chunk.split("\"image_url\":\"");
                if (imgSplit.length > 1) {
                    String raw = imgSplit[1].split("\"")[0];
                    if (!raw.equals("null") && !raw.isEmpty()) {
                        imageUrl = raw;
                    }
                }
            }

            System.out.println(count + ". " + title);
            System.out.println("   Released   : " + released);
            System.out.println("   Type       : " + type);
            System.out.println("   Episodes   : " + episodes);
            System.out.println("   Popularity : " + humanizePopularity(popularity));
            System.out.println("   Origin     : " + origin);
            System.out.println("   Image      : " + imageUrl);
            System.out.println();

            count++;
            if (count > 50) break;
        }

        if (count == 1) {
            System.out.println("No results matched your selected filters.");
        }
    }

    private String extractField(String chunk, String key) {
        String[] split = chunk.split(key);
        if (split.length > 1) {
            String raw = split[1].split("[,}]")[0].trim();
            if (!raw.equals("null") && !raw.isEmpty()) {
                return raw;
            }
        }
        return "N/A";
    }

    private String extractAnimeType(String chunk) {
        String[] typeParts = chunk.split("\"type\":\"");

        for (int i = 1; i < typeParts.length; i++) {
            String raw = typeParts[i].split("\"")[0];

            if (raw.equals("TV")
                    || raw.equals("Movie")
                    || raw.equals("OVA")
                    || raw.equals("ONA")
                    || raw.equals("Special")
                    || raw.equals("Music")
                    || raw.equals("TV Special")) {
                return raw;
            }
        }

        return "N/A";
    }

    private String humanizePopularity(String rawRank) {
        try {
            int rank = Integer.parseInt(rawRank.trim());
            if (rank <= 50)   return "Legendary — everyone knows this one";
            if (rank <= 200)  return "Extremely popular";
            if (rank <= 500)  return "Very popular";
            if (rank <= 1000) return "Quite popular";
            if (rank <= 2500) return "Moderately popular";
            if (rank <= 5000) return "Somewhat known";
            return "Not widely known yet";
        } catch (NumberFormatException e) {
            return "N/A";
        }
    }

    private String detectOrigin(String chunk) {
        String lower = chunk.toLowerCase();

        if (lower.contains("bilibili")
                || lower.contains("tencent")
                || lower.contains("iqiyi")
                || lower.contains("youku")
                || lower.contains("chinese")
                || lower.contains("donghua")) {
            return "China";
        }

        if (lower.contains("korean") || lower.contains("korea")) {
            return "South Korea";
        }

        return "Japan";
    }
}