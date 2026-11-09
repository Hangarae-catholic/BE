package com.catholic.hangarae.domain.notice.service;

import com.catholic.hangarae.domain.department.vo.DepartmentType;
import com.catholic.hangarae.domain.department.vo.Division;
import com.catholic.hangarae.domain.image.entity.Image;
import com.catholic.hangarae.domain.image.repository.ImageRepository;
import com.catholic.hangarae.domain.notice.dto.request.NoticeCreateReq;
import com.catholic.hangarae.domain.notice.dto.request.NoticeReactionReq;
import com.catholic.hangarae.domain.notice.dto.response.NoticeDetailRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeListRes;
import com.catholic.hangarae.domain.notice.dto.response.NoticeReactionRes;
import com.catholic.hangarae.domain.notice.entity.Notice;
import com.catholic.hangarae.domain.notice.entity.NoticeImage;
import com.catholic.hangarae.domain.notice.entity.NoticeReaction;
import com.catholic.hangarae.domain.notice.repository.NoticeImageRepository;
import com.catholic.hangarae.domain.notice.repository.NoticeReactionRepository;
import com.catholic.hangarae.domain.notice.repository.NoticeRepository;
import com.catholic.hangarae.domain.notice.vo.NoticeReactionType;
import com.catholic.hangarae.domain.notice.vo.NoticeType;
import com.catholic.hangarae.domain.user.entity.User;
import com.catholic.hangarae.domain.user.repository.UserRepository;
import com.catholic.hangarae.global.apiPayLoad.error.NoticeErrorCode;
import com.catholic.hangarae.global.apiPayLoad.error.UserErrorCode;
import com.catholic.hangarae.global.apiPayLoad.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final NoticeReactionRepository noticeReactionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NoticeListRes> getNotices(LocalDate date) {
        // 날짜를 선택하지 않으면 오늘로 설정
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate selectedDate = date;
        if (selectedDate == null) {
            selectedDate = today;
        }
        // 오늘부터 7일 이내인지 확인
        if (selectedDate.isBefore(today) || selectedDate.isAfter(today.plusDays(6))) {
            throw new BusinessException(NoticeErrorCode.INVALID_NOTICE_DATE);
        }

        // 선택한 날짜의 행사 조회
        List<Notice> notices = noticeRepository.findByDate(
                selectedDate.atStartOfDay(), selectedDate.plusDays(1).atStartOfDay());

        // 목록 응답 생성
        List<NoticeListRes> response = new ArrayList<>();
        for (Notice notice : notices) {
            NoticeListRes item = NoticeListRes.builder()
                    .noticeId(notice.getId())
                    .name(notice.getName())
                    .startAt(notice.getStartAt())
                    .endAt(notice.getEndAt())
                    .location(notice.getLocation())
                    .summary(notice.getSummary())
                    .approved(notice.isApproved())
                    .build();
            response.add(item);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeListRes> getRecommendedNotices(Long userId, int size) {
        // 프로필을 만든 회원만 추천
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        if (user.getCharacter() == null) {
            throw new BusinessException(UserErrorCode.PROFILE_NOT_FOUND);
        }

        // 오늘 열리는 행사 중 관심 분야 점수 순으로 조회
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<Notice> notices = noticeRepository.findRecommendedNotices(
                userId, today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        // 여러 학과 게시판에 올라온 같은 공지는 제목과 기간이 같으면 하나만 추천
        List<NoticeListRes> response = new ArrayList<>();
        Set<String> shown = new HashSet<>();
        for (Notice notice : notices) {
            if (response.size() >= size) {
                break;
            }
            if (!shown.add(notice.getName() + "|" + notice.getStartAt() + "|" + notice.getEndAt())) {
                continue;
            }
            NoticeListRes item = NoticeListRes.builder()
                    .noticeId(notice.getId())
                    .name(notice.getName())
                    .startAt(notice.getStartAt())
                    .endAt(notice.getEndAt())
                    .location(notice.getLocation())
                    .summary(notice.getSummary())
                    .approved(notice.isApproved())
                    .build();
            response.add(item);
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDetailRes getNotice(Long noticeId) {
        // 행사 조회, 없으면 예외 처리
        Notice notice = noticeRepository.findById(noticeId).orElse(null);
        if (notice == null) {
            throw new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }

        // 학과가 있으면 계열과 학과 정보 설정
        Division division = null;
        DepartmentType department = null;
        if (notice.getDepartment() != null) {
            division = notice.getDepartment().getDivision();
            department = notice.getDepartment().getName();
        }

        // 행사 이미지 URL 조회
        List<String> imageUrls = noticeImageRepository.findAllByNoticeIdOrderByIdAsc(noticeId)
                .stream()
                .map(noticeImage -> noticeImage.getImage().getUrl())
                .toList();

        // 상세 응답 생성
        NoticeDetailRes response = NoticeDetailRes.builder()
                .noticeId(notice.getId())
                .name(notice.getName())
                .summary(notice.getSummary())
                .startAt(notice.getStartAt())
                .endAt(notice.getEndAt())
                .location(notice.getLocation())
                .responsible(notice.getResponsible())
                .information(notice.getInformation())
                .url(notice.getUrl())
                .type(notice.getType())
                .category(notice.getCategory())
                .scheduleType(notice.getScheduleType())
                .approved(notice.isApproved())
                .division(division)
                .department(department)
                .imageUrls(imageUrls)
                .build();
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public String getCalendarFile(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));
        if (notice.getStartAt() == null) {
            throw new BusinessException(NoticeErrorCode.NOTICE_DATE_NOT_FOUND);
        }

        // 캘린더의 학교 공지는 날짜만 있어 종일 일정으로 생성, iCalendar 종료일은 다음 날로 표기
        LocalDate start = notice.getStartAt().toLocalDate();
        LocalDate end = notice.getEndAt() == null ? start : notice.getEndAt().toLocalDate();
        if (end.isBefore(start)) {
            end = start;
        }

        List<String> lines = new ArrayList<>();
        lines.add("BEGIN:VCALENDAR");
        lines.add("VERSION:2.0");
        lines.add("PRODID:-//Hangarae//Calendar//KO");
        lines.add("CALSCALE:GREGORIAN");
        lines.add("BEGIN:VEVENT");
        lines.add("UID:notice-" + notice.getId() + "@hangarae");
        lines.add("DTSTAMP:" + DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").format(LocalDateTime.now(ZoneOffset.UTC)));
        lines.add("DTSTART;VALUE=DATE:" + DateTimeFormatter.BASIC_ISO_DATE.format(start));
        lines.add("DTEND;VALUE=DATE:" + DateTimeFormatter.BASIC_ISO_DATE.format(end.plusDays(1)));
        lines.add("SUMMARY:" + escapeCalendarText(notice.getName()));
        if (notice.getSummary() != null) {
            lines.add("DESCRIPTION:" + escapeCalendarText(notice.getSummary()));
        }
        if (notice.getUrl() != null) {
            lines.add("URL:" + notice.getUrl().replaceAll("[\\r\\n]", ""));
        }
        lines.add("END:VEVENT");
        lines.add("END:VCALENDAR");

        StringBuilder file = new StringBuilder();
        for (String line : lines) {
            file.append(foldCalendarLine(line)).append("\r\n");
        }
        return file.toString();
    }

    // iCalendar 예약 문자 이스케이프
    private String escapeCalendarText(String text) {
        return text.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("\n", "\\n");
    }

    // iCalendar는 한 줄 75바이트 제한, 한글이 잘리지 않게 문자 단위로 줄바꿈
    private String foldCalendarLine(String line) {
        StringBuilder folded = new StringBuilder();
        int bytes = 0;
        for (int codePoint : line.codePoints().toArray()) {
            String character = new String(Character.toChars(codePoint));
            int size = character.getBytes(StandardCharsets.UTF_8).length;
            if (bytes + size > 75) {
                folded.append("\r\n ");
                bytes = 1;
            }
            folded.append(character);
            bytes += size;
        }
        return folded.toString();
    }

    @Transactional
    @Override
    public Long createNotice(Long userId, NoticeCreateReq req) {

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));


        Notice notice = Notice.builder()
                .name(req.getName())
                .responsible(req.getResponsible())
                .startAt(req.getStartAt())
                .endAt(req.getEndAt())
                .location(req.getLocation())
                .summary(req.getSummary())
                .information(req.getInformation())
                .type(NoticeType.PRIVATE)
                .user(user)
                .build();
        Notice savedNotice = noticeRepository.save(notice);

        if (req.getImageUrls() != null) {
            req.getImageUrls().forEach(imageUrl -> {
                Image image = imageRepository.save(Image.builder().url(imageUrl).build());
                NoticeImage noticeImage = NoticeImage.builder().notice(savedNotice).image(image).build();
                noticeImageRepository.save(noticeImage);
            });
        }

        return savedNotice.getId();
    }

    @Transactional
    @Override
    public Void deleteNotice(Long userId, Long noticeId){
        Notice notice =  noticeRepository.findById(noticeId)
                .orElseThrow(()-> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));

        if (notice.getType() != NoticeType.PRIVATE) { // 사설 행사가 아니면
            throw new BusinessException(NoticeErrorCode.PRIVATE_NOTICE_ONLY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if(!notice.getUser().getId().equals(user.getId())){
            throw new BusinessException(NoticeErrorCode.WRITER_DIFFERENCE);
        }

        List<NoticeImage> noticeImages =
                noticeImageRepository.findAllByNoticeIdOrderByIdAsc(noticeId);
        noticeImageRepository.deleteAll(noticeImages);

        // 실제 이미지 삭제
        noticeImages.forEach(noticeImage ->
                imageRepository.delete(noticeImage.getImage())
        );
        noticeRepository.delete(notice);
        return null;
    }

    @Transactional
    @Override
    public Void reactToNotice(Long userId, Long noticeId, NoticeReactionReq req) {

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(()-> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));

        // 사설 행사만
        if (notice.getType() != NoticeType.PRIVATE) {
            throw new BusinessException(NoticeErrorCode.PRIVATE_NOTICE_ONLY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Optional<NoticeReaction> existingReaction = noticeReactionRepository.findByUserIdAndNoticeId(userId, noticeId);

        if (existingReaction.isEmpty()) { // 처음 선택한 경우

            NoticeReaction reaction = NoticeReaction.builder()
                    .type(req.getType())
                    .user(user)
                    .notice(notice)
                    .build();
            noticeReactionRepository.save(reaction);

        } else { // 선택된 적이 있는 경우

            NoticeReaction reaction = existingReaction.get();
            if (reaction.getType() == req.getType()) {
                // 같은 버튼 누른 경우 - 취소
                noticeReactionRepository.delete(reaction);
            } else {
                // 다른 버튼 누른 경우 - 변경
                reaction.updateType(req.getType());
            }
        }

        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public NoticeReactionRes getNoticeReaction(Long userId, Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(()-> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));

        // 사설 공지만
        if (notice.getType() != NoticeType.PRIVATE) {
            throw new BusinessException(NoticeErrorCode.PRIVATE_NOTICE_ONLY);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Long likeCount = noticeReactionRepository.countByNoticeIdAndType(noticeId, NoticeReactionType.LIKE);
        Long dislikeCount = noticeReactionRepository.countByNoticeIdAndType(noticeId, NoticeReactionType.DISLIKE);

        NoticeReactionType userReaction = noticeReactionRepository.findByUserIdAndNoticeId(userId, noticeId)
                .map(reaction -> reaction.getType())
                .orElse(null);

        NoticeReactionRes response = NoticeReactionRes.builder()
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .userReaction(userReaction)
                .build();
        return response;
    }


}
