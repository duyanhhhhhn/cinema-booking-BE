package CinemaBooking.Group2.helpers;

public class SearchLogicMovie {

	public SearchLogicMovie() {
		super();
		// TODO Auto-generated constructor stub
	}


    public static String normalizeVietnameseKeepBaseVowel(String s) {
        if (s == null) return "";
        String x = s.toLowerCase();

        x = x.replace('á', 'a').replace('à', 'a').replace('ả', 'a').replace('ã', 'a').replace('ạ', 'a');
        x = x.replace('ắ', 'ă').replace('ằ', 'ă').replace('ẳ', 'ă').replace('ẵ', 'ă').replace('ặ', 'ă');
        x = x.replace('ấ', 'â').replace('ầ', 'â').replace('ẩ', 'â').replace('ẫ', 'â').replace('ậ', 'â');

        x = x.replace('é', 'e').replace('è', 'e').replace('ẻ', 'e').replace('ẽ', 'e').replace('ẹ', 'e');
        x = x.replace('ế', 'ê').replace('ề', 'ê').replace('ể', 'ê').replace('ễ', 'ê').replace('ệ', 'ê');

        x = x.replace('í', 'i').replace('ì', 'i').replace('ỉ', 'i').replace('ĩ', 'i').replace('ị', 'i');

        x = x.replace('ó', 'o').replace('ò', 'o').replace('ỏ', 'o').replace('õ', 'o').replace('ọ', 'o');
        x = x.replace('ố', 'ô').replace('ồ', 'ô').replace('ổ', 'ô').replace('ỗ', 'ô').replace('ộ', 'ô');
        x = x.replace('ớ', 'ơ').replace('ờ', 'ơ').replace('ở', 'ơ').replace('ỡ', 'ơ').replace('ợ', 'ơ');

        x = x.replace('ú', 'u').replace('ù', 'u').replace('ủ', 'u').replace('ũ', 'u').replace('ụ', 'u');
        x = x.replace('ứ', 'ư').replace('ừ', 'ư').replace('ử', 'ư').replace('ữ', 'ư').replace('ự', 'ư');

        x = x.replace('ý', 'y').replace('ỳ', 'y').replace('ỷ', 'y').replace('ỹ', 'y').replace('ỵ', 'y');

        x = x.replaceAll("\\s+", " ").trim();
        return x;
    }

    public static String normalizeVietnameseAscii(String s) {
        if (s == null) return "";
        String x = normalizeVietnameseKeepBaseVowel(s);
        x = x.replace('ă', 'a')
             .replace('â', 'a')
             .replace('ê', 'e')
             .replace('ô', 'o')
             .replace('ơ', 'o')
             .replace('ư', 'u')
             .replace('đ', 'd');
        return x;
    }

    public static String normalizeRaw(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("\\s+", " ").trim();
    }

    public static int scoreMovieTitle(String title, String keyword) {
        String rawTitle = normalizeRaw(title);
        String rawKeyword = normalizeRaw(keyword);

        String keepTitle = normalizeVietnameseKeepBaseVowel(title);
        String keepKeyword = normalizeVietnameseKeepBaseVowel(keyword);

        String asciiTitle = normalizeVietnameseAscii(title);
        String asciiKeyword = normalizeVietnameseAscii(keyword);

        if (rawKeyword.isBlank()) return Integer.MAX_VALUE;

        if (rawTitle.startsWith(rawKeyword)) return 0;
        if (keepTitle.startsWith(keepKeyword)) return 1;
        if (asciiTitle.startsWith(asciiKeyword)) return 2;

        if (rawTitle.contains(" " + rawKeyword)) return 3;
        if (keepTitle.contains(" " + keepKeyword)) return 4;
        if (asciiTitle.contains(" " + asciiKeyword)) return 5;

        if (rawTitle.contains(rawKeyword)) return 6;
        if (keepTitle.contains(keepKeyword)) return 7;
        if (asciiTitle.contains(asciiKeyword)) return 8;

        return 99;
    }

    public static boolean isStrongPrefixMatch(String title, String keyword) {
        String rawTitle = normalizeRaw(title);
        String rawKeyword = normalizeRaw(keyword);

        String keepTitle = normalizeVietnameseKeepBaseVowel(title);
        String keepKeyword = normalizeVietnameseKeepBaseVowel(keyword);

        String asciiTitle = normalizeVietnameseAscii(title);
        String asciiKeyword = normalizeVietnameseAscii(keyword);

        return rawTitle.startsWith(rawKeyword)
            || keepTitle.startsWith(keepKeyword)
            || asciiTitle.startsWith(asciiKeyword);
    }

    public static boolean isWeakMatch(String title, String keyword) {
        String rawTitle = normalizeRaw(title);
        String rawKeyword = normalizeRaw(keyword);

        String keepTitle = normalizeVietnameseKeepBaseVowel(title);
        String keepKeyword = normalizeVietnameseKeepBaseVowel(keyword);

        String asciiTitle = normalizeVietnameseAscii(title);
        String asciiKeyword = normalizeVietnameseAscii(keyword);

        return rawTitle.contains(rawKeyword)
            || keepTitle.contains(keepKeyword)
            || asciiTitle.contains(asciiKeyword);
    }

}
