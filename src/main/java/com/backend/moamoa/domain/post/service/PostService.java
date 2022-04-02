package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.domain.post.dto.request.CommentRequest;
import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.*;
import com.backend.moamoa.domain.post.entity.*;
import com.backend.moamoa.domain.post.repository.*;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.domain.user.repository.UserRepository;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public PostResponse createPost(PostRequest postRequest) {

        PostCategory postCategory = postCategoryRepository.findByCategoryName(postRequest.getCategoryName())
                .orElseGet(() -> PostCategory.createCategory(postRequest.getCategoryName()));
        User user = userRepository.findById(1L).get();
        Post post = Post.createPost(postRequest.getTitle(), postRequest.getContent(), user, postCategory);
        postRepository.save(post);
        return new PostResponse(post.getId(), "게시글 작성이 완료되었습니다.");
    }

    @Transactional
    public PostResponse updatePost(PostUpdateRequest request) {

        Post post = getPost(request.getPostId());

        post.updatePost(request.getTitle(), request.getContent());

        return new PostResponse(post.getId(), "게시글 변경이 완료되었습니다.");
    }

    @Transactional
    public PostResponse deletePost(Long postId) {
        postRepository.deleteById(postId);

        return new PostResponse(postId, "게시글 삭제가 완료되었습니다.");
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public PostOneResponse findById(Long postId) {
        return postRepository.findOnePostById(postId)
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

    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest) {
        User user = userRepository.findById(1L).get();
        Post post = getPost(commentRequest.getPostId());
        Long parentId = commentRequest.getParentId();

        if (Objects.isNull(parentId)) {
            Comment comment = commentRepository.save(Comment.builder()
                    .user(user)
                    .post(post)
                    .content(commentRequest.getContent())
                    .build());
            return new CommentResponse(comment.getId());
        }
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_COMMENT));
        Comment comment = commentRepository.save(Comment.createComment(parent, user, post, commentRequest.getContent()));
        commentRepository.save(comment);
            return new CommentResponse(comment.getId());
        }

    }


//    public void findMyPosts() {
//        User user = util.findCurrentUser();
//
//        postRepository.findMyPostsById(user.getId());
//    }

