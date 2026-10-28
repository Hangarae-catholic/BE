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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .approved(notice.isApproved())
                .division(division)
                .department(department)
                .imageUrls(imageUrls)
                .build();
        return response;
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
