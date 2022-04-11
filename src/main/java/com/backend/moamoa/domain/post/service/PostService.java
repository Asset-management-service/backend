package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.*;
import com.backend.moamoa.domain.post.entity.*;
import com.backend.moamoa.domain.post.repository.comment.CommentRepository;
import com.backend.moamoa.domain.post.repository.post.*;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.s3.S3Uploader;
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
    private final UserRepository userRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;
    private final S3Uploader s3Uploader;
    private final PostImageRepository postImageRepository;


    @Transactional
    public PostCreateResponse createPost(PostRequest postRequest) {
        PostCategory postCategory = postCategoryRepository.findByCategoryName(postRequest.getCategoryName())
                .orElseGet(() -> PostCategory.createCategory(postRequest.getCategoryName()));
        User user = userRepository.findById(1L).get();
        Post post = postRepository.save(Post.createPost(postRequest.getTitle(), postRequest.getContent(), user, postCategory));
        List<String> postImages = uploadPostImages(postRequest, post);

        return new PostCreateResponse(post.getId(), "게시글 작성이 완료되었습니다.", postImages);
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

        User user = userRepository.findById(1L).get();
        Post post = postRepository.findByIdAndUser(request.getPostId(), user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        validateDeletedImages(request);
        List<String> newPostImages = uploadPostImages(request, post);
        saveImages(request, newPostImages);

        post.updatePost(request.getTitle(), request.getContent());

        return new PostUpdateResponse(post.getId(), "게시글 변경이 완료되었습니다.", newPostImages);
    }

    /**
     * @Request 로 받아온 이미지 경로랑 저장 되어있던 이미지 경로랑 일치하지 않는다면 모두 삭제
     */
    private void validateDeletedImages(PostUpdateRequest request) {
        postImageRepository.findBySavedImageUrl(request.getPostId()).stream()
                .filter(image -> !request.getSavedImageUrl().stream().anyMatch(Predicate.isEqual(image.getImageUrl())))
                .forEach(url -> {
                    postImageRepository.delete(url);
                    s3Uploader.deleteImage(url.getImageUrl());
                });
    }

    /**
     * S3에 업로드 및 PostImage 생성
     */
    private List<String> uploadPostImages(PostUpdateRequest request, Post post) {
        List<String> newPostImages = request.getImageFiles()
                .stream()
                .map(file -> s3Uploader.upload(file, "post"))
                .map(url -> createPostImage(post, url))
                .map(images -> images.getImageUrl())
                .collect(Collectors.toList());
        return newPostImages;
    }

    /**
     * 업로드한 이미지 파일 경로 + @Request 로 받아온 저장된 이미지를 추가하는 메서드
     */
    private void saveImages(PostUpdateRequest request, List<String> newPostImages) {
        request.getSavedImageUrl()
                .stream()
                .forEach(savedUrl -> newPostImages.add(savedUrl));
    }

    @Transactional
    public PostResponse deletePost(Long postId) {
        User user = userRepository.findById(1L).get();
        Post post = postRepository.findByIdAndUser(postId, user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));

        postRepository.delete(post);

        return new PostResponse(postId, "게시글 삭제가 완료되었습니다.");
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public PostOneResponse getOnePost(Long postId) {
        PostOneResponse postOneResponse = postRepository.findOnePostById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_POST));
        commentsExtractor(postId, postOneResponse);
        return postOneResponse;
    }

    private void commentsExtractor(Long postId, PostOneResponse postOneResponse) {
        postOneResponse.getComments()
                .forEach(comment -> {
                            List<CommentsChildrenResponse> comments = commentRepository.findPostComments(postId, comment.getCommentId());
                            comment.setChildren(comments);
                });
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
    public LikeResponse likePost(Long postId) {
        User user = userRepository.findById(1L).get();
        Post post = getPost(postId);
        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);

        if (postLike.isEmpty()) {
            postLikeRepository.save(PostLike.createPostLike(user, post));
            return new LikeResponse(true);
        }
        postLikeRepository.delete(postLike.get());
        return new LikeResponse(false);
    }

    @Transactional
    public ScrapResponse scrapPost(Long postId) {
        User user = userRepository.findById(1L).get();
        Post post = getPost(postId);

        Optional<Scrap> scrap = scrapRepository.findByUserAndPost(user, post);
        if (scrap.isEmpty()) {
            scrapRepository.save(Scrap.createScrap(user, post));
            return new ScrapResponse(true);
        }
        scrapRepository.delete(scrap.get());
        return new ScrapResponse(false);
    }

    public Page<RecentPostResponse> getRecentPost(Pageable pageable, String categoryName) {
        return postRepository.findRecentPosts(pageable, categoryName);
    }

}
