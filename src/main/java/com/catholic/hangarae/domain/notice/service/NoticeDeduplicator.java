package com.catholic.hangarae.domain.notice.service;

import com.catholic.hangarae.domain.notice.entity.Notice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 여러 게시판에 제목을 조금씩 바꿔 올라온 같은 공지를 응답에서 하나만 남김
public final class NoticeDeduplicator {

    private static final double THRESHOLD = 0.6;
    private static final Set<String> STOP_WORDS = Set.of(
            "안내", "신청", "이수", "교내", "실시", "관련", "공지", "알림", "모집", "개최", "및", "재학생", "학생",
            "대상", "마감", "재안내", "필수", "필독", "공고", "참여", "참가", "프로그램", "운영", "결과", "일정",
            "방법", "의", "등", "위한", "관한", "기간", "연장", "기간연장");
    private static final Pattern CHINESE = Pattern.compile("[\\u3400-\\u9fff\\uf900-\\ufaff]+");
    private static final Pattern BRACKET_TAG = Pattern.compile("\\[[^\\]]*\\]|【[^】]*】");
    private static final Pattern YEAR = Pattern.compile("(\\d{4})학?년도?");
    private static final Pattern WORD = Pattern.compile("[0-9A-Za-z가-힣]+");
    private static final Pattern PARTICLE = Pattern.compile("(을|를|은|는|이|가|의|에|와|과)$");

    private NoticeDeduplicator() {
    }

    // 목록 순서대로 앞에 나온 공지를 남김
    public static List<Notice> distinct(List<Notice> notices) {
        List<Notice> result = new ArrayList<>();
        List<Set<String>> shown = new ArrayList<>();
        for (Notice notice : notices) {
            Set<String> words = words(notice.getName());
            if (shown.stream().noneMatch(other -> isSame(words, other))) {
                shown.add(words);
                result.add(notice);
            }
        }
        return result;
    }

    static boolean isSame(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        long common = a.stream().filter(b::contains).count();
        return (double) common / Math.min(a.size(), b.size()) >= THRESHOLD;
    }

    // 한자 병기·말머리 태그 제거, 연도 표기 통일, 흔한 단어와 조사 제거
    static Set<String> words(String title) {
        String text = CHINESE.matcher(title == null ? "" : title).replaceAll(" ");
        text = BRACKET_TAG.matcher(text).replaceAll(" ");
        text = YEAR.matcher(text).replaceAll("$1");
        Set<String> words = new HashSet<>();
        Matcher matcher = WORD.matcher(text);
        while (matcher.find()) {
            String word = matcher.group();
            if (word.length() > 2) {
                word = PARTICLE.matcher(word).replaceAll("");
            }
            if (!word.isEmpty() && !STOP_WORDS.contains(word)) {
                words.add(word.toLowerCase());
            }
        }
        return words;
    }
}
