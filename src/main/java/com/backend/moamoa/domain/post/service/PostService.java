package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.*;
import com.backend.moamoa.domain.post.entity.*;
import com.backend.moamoa.domain.post.repository.post.*;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.s3.S3Uploader;
import com.backend.moamoa.global.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final ScrapRepository scrapRepository;
    private final S3Uploader s3Uploader;
    private final PostImageRepository postImageRepository;
    private final UserUtil userUtil;

    /**
     * 게시글 생성
     * TODO 카테고리 생성 로직 변경 예정
     */
    @Transactional
    public PostCreateResponse createPost(PostRequest postRequest) {
        PostCategory postCategory = postCategoryRepository.findByCategoryName(postRequest.getCategoryName())
                .orElseGet(() -> PostCategory.createCategory(postRequest.getCategoryName()));
        User user = userUtil.findCurrentUser();
        Post post = postRepository.save(Post.createPost(postRequest.getTitle(), postRequest.getContent(), user, postCategory));

        List<String> postImages = uploadPostImages(postRequest, post);

        addMoneyLogImages(postRequest, post, postImages);

        return new PostCreateResponse(post.getId(), postImages);

    }

    /**
     * 머니로그 공유 시 이미지 경로를 받아와서 게시글 이미지 추가
     */
    private void addMoneyLogImages(PostRequest postRequest, Post post, List<String> postImages) {
        postRequest.getMoneyLogImages().stream()
                .map(images -> createPostImage(post, images))
                .map(postImage -> postImage.getImageUrl())
                .forEach(url -> postImages.add(url));
    }

    /**
     * 이미지 파일 S3 저장 + PostImage 생성
     */
    private List<String> uploadPostImages(PostRequest postRequest, Post post) {
        return postRequest.getImageFiles().stream()
                .map(image -> s3Uploader.upload(image, "post"))
                .map(url -> createPostImage(post, url))
                .map(postImage -> postImage.getImageUrl())
                .collect(Collectors.toList());
    }

    /**
     * PostImage 생성 메서드
     */
    private PostImage createPostImage(Post post, String url) {
        return postImageRepository.save(PostImage.builder()
                .imageUrl(url)
                .storeFilename(StringUtils.getFilename(url))
                .post(post)
                .build());
    }

    @Transactional
    public PostUpdateResponse updatePost(PostUpdateRequest request) {
        User user = userUtil.findCurrentUser();
        Post post = postRepository.findByIdAndUser(request.getPostId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        validateDeletedImages(request);
        uploadPostImages(request, post);
        List<String> saveImages = getSaveImages(request);

        post.updatePost(request.getTitle(), request.getContent());

        return new PostUpdateResponse(post.getId(), saveImages);
    }

    /**
     * @Request 로 받아온 이미지 경로랑 저장 되어있던 이미지 경로랑 일치하지 않는다면 모두 삭제
     */
    private void validateDeletedImages(PostUpdateRequest request) {
        postImageRepository.findBySavedImageUrl(request.getPostId()).stream()
                .filter(image -> !request.getSaveImageUrl().stream().anyMatch(Predicate.isEqual(image.getImageUrl())))
                .forEach(url -> {
                    postImageRepository.delete(url);
                    s3Uploader.deleteImage(url.getImageUrl());
                });
    }

    /**
     * S3에 업로드 및 PostImage 생성
     */
    private void uploadPostImages(PostUpdateRequest request, Post post) {
        request.getImageFiles()
                .stream()
                .forEach(file -> {
                    String url = s3Uploader.upload(file, "post");
                    createPostImage(post, url);
                });
    }

    /**
     * PostImage 테이블에 저장 되어있는 이미지 경로를 추출
     */
    private List<String> getSaveImages(PostUpdateRequest request) {
        return postImageRepository.findBySavedImageUrl(request.getPostId())
                .stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePost(Long postId) {
        User user = userUtil.findCurrentUser();
        Post post = postRepository.findByIdAndUser(postId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        postRepository.delete(post);
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public PostOneResponse getOnePost(Long postId) {
        User user = userUtil.findCurrentUser();
        return postRepository.findOnePostById(postId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
    }


    /**
     * 중복 로직 findById Optional 처리
     */
    private Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
        return post;
    }

    @Transactional
    public boolean likePost(Long postId) {
        User user = userUtil.findCurrentUser();
        Post post = getPost(postId);
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);

        if (postLike.isEmpty()) {
            postLikeRepository.save(PostLike.createPostLike(user, post));
            return true;
        }
        postLikeRepository.delete(postLike.get());
        return false;
    }

    @Transactional
    public boolean scrapPost(Long postId) {
        User user = userUtil.findCurrentUser();
        Post post = getPost(postId);

        Optional<Scrap> scrap = scrapRepository.findByUserAndPost(user, post);
        if (scrap.isEmpty()) {
            scrapRepository.save(Scrap.createScrap(user, post));
            return true;
        }
        scrapRepository.delete(scrap.get());
        return false;
    }

    public Page<RecentPostResponse> getRecentPost(Pageable pageable, String categoryName) {
        return postRepository.findRecentPosts(pageable, categoryName);
    }

}
