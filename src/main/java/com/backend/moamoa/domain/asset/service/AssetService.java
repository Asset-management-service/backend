package com.backend.moamoa.domain.asset.service;

import com.backend.moamoa.domain.asset.dto.request.*;
import com.backend.moamoa.domain.asset.dto.response.*;
import com.backend.moamoa.domain.asset.entity.*;
import com.backend.moamoa.domain.asset.repository.*;
import com.backend.moamoa.domain.post.entity.PostImage;
import com.backend.moamoa.domain.post.repository.post.PostImageRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.s3.S3Uploader;
import com.backend.moamoa.global.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetService {

    private static final String TYPE_REVENUE = "REVENUE";
    private static final String TYPE_EXPENDITURE = "EXPENDITURE";

    private final AssetCategoryRepository assetCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenditureRatioRepository expenditureRatioRepository;
    private final RevenueExpenditureRepository revenueExpenditureRepository;
    private final AssetGoalRepository assetGoalRepository;
    private final PostImageRepository postImageRepository;
    private final MoneyLogRepository moneyLogRepository;
    private final S3Uploader s3Uploader;
    private final UserUtil userUtil;

    /**
     * 카테고리 생성
     */
    @Transactional
    public Long addCategory(AssetCategoryRequest request) {
        User user = userUtil.findCurrentUser();
        return assetCategoryRepository.save(AssetCategory.createCategory(request.getCategoryType(), request.getCategoryName(), user)).getId();
    }

    /**
     * 예산 설정
     */
    @Transactional
    public Long addBudget(BudgetRequest request) {
        User user = userUtil.findCurrentUser();
        Budget budget = budgetRepository.findBudgetAmountByUser(user)
                .orElseGet(() -> budgetRepository.save(Budget.createBudget(request.getBudgetAmount(), user)));
        budget.updateBudgetAmount(request.getBudgetAmount());
        return budget.getId();
    }

    /**
     * 지출 비율 설정
     */
    @Transactional
    public Long addExpenditure(ExpenditureRequest request) {
        User user = userUtil.findCurrentUser();

        if (request.getFixed() + request.getVariable() != 100) {
            throw new CustomException(ErrorCode.BAD_REQUEST_EXPENDITURE);
        }
        ExpenditureRatio expenditureRatio = expenditureRatioRepository.findByUser(user)
                .orElseGet(() -> expenditureRatioRepository.save(ExpenditureRatio.createExpenditureRatio(request.getFixed(), request.getVariable(), user)));

        expenditureRatio.updateExpenditureRatio(request.getVariable(), request.getFixed());

        return expenditureRatio.getId();
    }

    /**
     * 카테고리 조회
     */
    public AssetCategoryDtoResponse getCategories(String categoryType) {
        User user = userUtil.findCurrentUser();
        List<AssetCategory> assetCategories = assetCategoryRepository.findByAssetCategoryTypeAndUserId(categoryType, user.getId());

        return AssetCategoryDtoResponse.of(assetCategories);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategoryName(Long categoryId) {
        User user = userUtil.findCurrentUser();
        AssetCategory category = assetCategoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ASSET_CATEGORY));

        assetCategoryRepository.delete(category);
    }

    /**
     * 수익 지출 설정
     */
    @Transactional
    public Long addRevenueExpenditure(CreateRevenueExpenditureRequest request) {
        User user = userUtil.findCurrentUser();

        return revenueExpenditureRepository.save(RevenueExpenditure.builder()
                .revenueExpenditureType(request.getRevenueExpenditureType())
                .content(request.getContent())
                .cost(request.getCost())
                .date(request.getDate())
                .categoryName(request.getCategoryName())
                .paymentMethod(request.getPaymentMethod())
                .user(user)
                .build()).getId();
    }

    /**
     * 가계부 수익 지출 조회
     */
    public RevenueExpenditureSumResponse findRevenueExpenditureByMonth(String month, Pageable pageable) {
        User user = userUtil.findCurrentUser();

        Page<RevenueExpenditureResponse> revenueExpenditure = revenueExpenditureRepository.findRevenueAndExpenditureByMonth(LocalDate.parse(month + "-01"), pageable, user.getId());

        Budget budget = budgetRepository.findBudgetAmountByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BUDGET));

        List<RevenueExpenditure> revenueExpenditureList = revenueExpenditureRepository.findRevenueExpenditure(LocalDate.parse(month + "-01"), user.getId());

        int revenue = getRevenueExpenditure(revenueExpenditureList, TYPE_REVENUE);
        int expenditure = getRevenueExpenditure(revenueExpenditureList, TYPE_EXPENDITURE);
        int remainingBudget = budget.getBudgetAmount() - expenditure;

        return RevenueExpenditureSumResponse.of(revenue, expenditure, remainingBudget, revenueExpenditure);

    }

    /**
     * 수익 지출 타입을 받아서 합을 반환해주는 메소드
     */
    private int getRevenueExpenditure(List<RevenueExpenditure> revenueExpenditureList, String type) {
        return revenueExpenditureList
                    .stream()
                    .filter(r -> r.getRevenueExpenditureType().toString().equals(type))
                    .mapToInt(RevenueExpenditure::getCost)
                    .sum();
    }

    /**
     * 자산 관리 목표 설정
     */
    @Transactional
    public Long addAssetGoal(CreateAssetGoalRequest request) {
        User user = userUtil.findCurrentUser();
        AssetGoal assetGoal = assetGoalRepository.findByUserAndDate(user, request.getDate())
                .orElseGet(() -> assetGoalRepository.save(AssetGoal.createAssetGoal(request.getContent(), user, request.getDate())));
        assetGoal.updateAssetGoal(request.getContent());
        return assetGoal.getId();
    }

    /**
     * 머니 로그 생성
     */
    @Transactional
    public CreateMoneyLogResponse createMoneyLog(CreateMoneyLogRequest request) {
        User user = userUtil.findCurrentUser();
        MoneyLog moneyLog = moneyLogRepository.save(MoneyLog.createMoneyLog(request.getDate(), request.getContent(), user));
        List<String> imageUrl = uploadMoneyLogImages(request.getImageFiles(), moneyLog);

        return new CreateMoneyLogResponse(moneyLog.getId(), imageUrl);
    }

    /**
     * 머니 로그 생성 시 이미지 파일을 을장하는 메서드
     */
    private List<String> uploadMoneyLogImages(List<MultipartFile> images, MoneyLog moneyLog) {
        return  images.stream()
                .map(image -> s3Uploader.upload(image, "moneyLog"))
                .map(url -> createPostImage(moneyLog, url))
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
    }

    /**
     * 이미지 경로 저장
     */
    private PostImage createPostImage(MoneyLog moneyLog, String url) {
        return postImageRepository.save(PostImage.builder()
                .imageUrl(url)
                .storeFilename(StringUtils.getFilename(url))
                .moneyLog(moneyLog)
                .build());
    }

    /**
     * 수익 지출 내역 수정
     */
    @Transactional
    public void updateRevenueExpenditure(UpdateRevenueExpenditure request) {
        User user = userUtil.findCurrentUser();

        RevenueExpenditure revenueExpenditure = getRevenueExpenditure(request.getRevenueExpenditureId(), user);

        revenueExpenditure.updateRevenueExpenditure(request.getRevenueExpenditureType(), request.getContent(),
                request.getDate(), request.getPaymentMethod(), request.getCategoryName(), request.getCost());
    }

    /**
     * 수익 지출 내역 삭제
     */
    @Transactional
    public void deleteRevenueExpenditure(Long revenueExpenditureId) {
        User user = userUtil.findCurrentUser();
        RevenueExpenditure revenueExpenditure = getRevenueExpenditure(revenueExpenditureId, user);
        revenueExpenditureRepository.delete(revenueExpenditure);
    }

    /**
     * 수익 지출 공통 로직
     */
    private RevenueExpenditure getRevenueExpenditure(Long revenueExpenditureId, User user) {
        return revenueExpenditureRepository.findByUserAndId(user, revenueExpenditureId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVENUE_EXPENDITURE));
    }

    /**
     * 머니 로그 수정
     */
    @Transactional
    public UpdateMoneyLogResponse updateMoneyLog(UpdateMoneyLogRequest request) {
        User user = userUtil.findCurrentUser();

        MoneyLog moneyLog = moneyLogRepository.findByUserAndId(user, request.getMoneyLogId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MONEY_LOG));

        moneyLog.updateMoneyLog(request.getDate(), request.getContent());

        validateDeleteImages(request, moneyLog);
        uploadImages(request, moneyLog);

        List<String> saveImages = postImageRepository.findBySavedMoneyLogImageUrl(moneyLog.getId())
                .stream().map(image -> image.getImageUrl()).collect(Collectors.toList());

        return new UpdateMoneyLogResponse(moneyLog.getId(), saveImages);
    }

    /**
     * 머니 로그 수정 시 이미지를 S3에 저장하는 메서드
     */
    private void uploadImages(UpdateMoneyLogRequest request, MoneyLog moneyLog) {
        request.getImageFiles()
                .stream()
                .map(image -> s3Uploader.upload(image, "moneyLog"))
                .forEach(url -> createPostImage(moneyLog, url));
    }

    private void validateDeleteImages(UpdateMoneyLogRequest request, MoneyLog moneyLog) {
        postImageRepository.findBySavedMoneyLogImageUrl(moneyLog.getId())
                .stream()
                .filter(image -> !request.getSaveImageUrl().stream().anyMatch(Predicate.isEqual(image.getImageUrl())))
                .forEach(url -> {
                    postImageRepository.delete(url);
                    s3Uploader.deleteImage(url.getImageUrl());
                });
    }

    /**
     * 머니 로그 수익 지출 조회
     */
    public MoneyLogRevenueExpenditureResponse getRevenueExpenditure(String date) {
        User user = userUtil.findCurrentUser();
        List<RevenueExpenditureResponse> moneyLogRevenueExpenditure = revenueExpenditureRepository.findMoneyLogRevenueExpenditure(user.getId(), LocalDate.parse(date));
        int revenue = revenueExpenditureExtractor(moneyLogRevenueExpenditure, TYPE_REVENUE);
        int expenditure = revenueExpenditureExtractor(moneyLogRevenueExpenditure, TYPE_EXPENDITURE);
        int remainingBudget = revenue - expenditure;

        return new MoneyLogRevenueExpenditureResponse(revenue, expenditure, remainingBudget, moneyLogRevenueExpenditure);
    }

    /**
     * 타입을 받아서 합계를 return 해주는 메소드
     */
    private int revenueExpenditureExtractor(List<RevenueExpenditureResponse> moneyLogRevenueExpenditure, String type) {
        return moneyLogRevenueExpenditure
                .stream()
                .filter(revenueExpenditure -> revenueExpenditure.getRevenueExpenditureType().toString().equals(type))
                .mapToInt(RevenueExpenditureResponse::getCost)
                .sum();
    }

    /**
     * 머니 로그 조회
     */
    public MoneyLogResponse getMoneyLog(String date) {
        User user = userUtil.findCurrentUser();
        MoneyLog moneyLog = moneyLogRepository.findByUserAndDate(user, LocalDate.parse(date))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MONEY_LOG));
        List<String> imageUrl = postImageRepository.findBySavedMoneyLogImageUrl(moneyLog.getId())
                .stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
        return new MoneyLogResponse(moneyLog.getId(), moneyLog.getContent(), imageUrl);
    }

    /**
     * 자산 관리 목표 조회
     */
    public AssetGoalResponse getAssetGoal(String date) {
        User user = userUtil.findCurrentUser();
        AssetGoal assetGoal = assetGoalRepository.findByUserAndDate(user, LocalDate.parse(date))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ASSET_GOAL));
        return new AssetGoalResponse(assetGoal.getId(), assetGoal.getContent());
    }

    /**
     * 한달 예산 금액 조회
     */
    public BudgetResponse getBudget() {
        User user = userUtil.findCurrentUser();
        Budget budget = budgetRepository.findBudgetAmountByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BUDGET));

        return new BudgetResponse(budget.getId(), budget.getBudgetAmount());
    }

    /**
     * 해당 회원의 지출 비율을 조회
     */
    public ExpenditureResponse getExpenditure() {
        User user = userUtil.findCurrentUser();
        ExpenditureRatio expenditureRatio = expenditureRatioRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RATIO));

        return ExpenditureResponse.of(expenditureRatio);
    }
}
