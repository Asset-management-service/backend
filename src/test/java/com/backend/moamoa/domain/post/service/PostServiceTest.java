package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.PostCreateResponse;
import com.backend.moamoa.domain.post.dto.response.PostOneResponse;
import com.backend.moamoa.domain.post.dto.response.PostUpdateResponse;
import com.backend.moamoa.domain.post.entity.*;
import com.backend.moamoa.domain.post.repository.post.*;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.s3.S3Uploader;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserUtil userUtil;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCategoryRepository postCategoryRepository;

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private ScrapRepository scrapRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 생성 - 성공")
    void createPost() {
        //given
        User user = UserBuilder.dummyUser();
        PostCategory postCategory = PostCategory.builder().categoryName("자유게시판").build();
        Post post = Post.builder().postCategory(postCategory).content("test").title("test").user(user).build();

        List<String> imageUrl = List.of("https://s3uploader.Moamoa1/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc",
                "https://s3uploader.moamoa2/ezzzyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as");

        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "모아모아1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "모아모아2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));

        PostImage postImage1 = PostImage.builder().imageUrl(imageUrl.get(0)).post(post).storeFilename(StringUtils.getFilename(imageUrl.get(0))).build();
        PostImage postImage2 = PostImage.builder().imageUrl(imageUrl.get(1)).post(post).storeFilename(StringUtils.getFilename(imageUrl.get(1))).build();

        given(postCategoryRepository.findByCategoryName(anyString())).willReturn(Optional.of(postCategory));
        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.save(any(Post.class))).willReturn(post);
        given(s3Uploader.upload(any(MultipartFile.class), anyString())).willReturn(imageUrl.get(0), imageUrl.get(1));
        given(postImageRepository.save(any(PostImage.class))).willReturn(postImage1, postImage2);


        //when
        PostCreateResponse response = postService.createPost(new PostRequest("test", "test", "모아모아", imageFiles, imageUrl));

        //then
        assertThat(response.getImageUrl().size()).isEqualTo(4);

        verify(postCategoryRepository, times(1)).findByCategoryName(anyString());
        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).save(any(Post.class));
        verify(s3Uploader, times(2)).upload(any(MultipartFile.class), anyString());
        verify(postImageRepository, times(4)).save(any(PostImage.class));
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updatePost() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().user(user).title("모아모아").content("모아모앙!").build();

        List<String> imageUrl = List.of("https://s3uploader.Moamoa1/eyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdnsajkcjksahdkjakjcsashc",
                "https://s3uploader.moamoa2/ezzzyjcnlzkam1aznaklmcmz.xccakljlkjljll1zeqwjeqwjkdndsalkdjsalkmcxz,as");

        PostImage postImage1 = PostImage.builder().imageUrl(imageUrl.get(0)).post(post).storeFilename(StringUtils.getFilename(imageUrl.get(0))).build();
        PostImage postImage2 = PostImage.builder().imageUrl(imageUrl.get(1)).post(post).storeFilename(StringUtils.getFilename(imageUrl.get(1))).build();

        List<MultipartFile> imageFiles = List.of(new MockMultipartFile("test1", "모아모아1.jpg", MediaType.IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "모아모아2.jpg", MediaType.IMAGE_PNG_VALUE, "test2".getBytes()));


        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findByIdAndUser(anyLong(), anyLong())).willReturn(Optional.of(post));
        given(postImageRepository.findBySavedImageUrl(anyLong())).willReturn(List.of(postImage1, postImage2));

        //when
        PostUpdateResponse response = postService.updatePost(new PostUpdateRequest(1L, "test1", "test1", imageUrl, imageFiles));

        //then
        assertThat(response.getSaveImages().size()).isEqualTo(2);
        assertThat(response.getSaveImages()).isEqualTo(imageUrl);

        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findByIdAndUser(anyLong(), anyLong());
        verify(postImageRepository, times(2)).findBySavedImageUrl(anyLong());
    }

    @Test
    @DisplayName("게시글 수정 - Post PK를 찾지 못한 경우")
    void updatePostFail() {
        //given
        User user = UserBuilder.dummyUser();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findByIdAndUser(anyLong(), anyLong())).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> postService.updatePost(new PostUpdateRequest(1L, "test1", "test1", null, null)))
                .isInstanceOf(CustomException.class);

        //then

        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findByIdAndUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().title("test1").content("test1").user(user).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findByIdAndUser(anyLong(), anyLong())).willReturn(Optional.of(post));

        //when
        postService.deletePost(1L);

        //then
        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findByIdAndUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 삭제 - Post PK를 찾지 못한 경우 실패")
    void deletePostFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());

        //when
        //then
        assertThatThrownBy(() -> postService.deletePost(1L))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
    }

    @Test
    @DisplayName("게시글 단건 조회 - 성공")
    void getOnePost() {
        //given
        User user = UserBuilder.dummyUser();
        PostOneResponse postOneResponse = new PostOneResponse(1L, "test", "test1", 5, 5, 5, LocalDateTime.now(), null,
                60, "ehgns5668", true, true, false);
        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findOnePostById(anyLong(), anyLong())).willReturn(Optional.of(postOneResponse));

        //when
        PostOneResponse response = postService.getOnePost(1L);

        //then
        assertThat(response.getTitle()).isEqualTo(postOneResponse.getTitle());
        assertThat(response.getContent()).isEqualTo(postOneResponse.getContent());
        assertThat(response.getLikeCount()).isEqualTo(5);

        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findOnePostById(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 단건 조회 - Post PK를 찾지 못한 경우 실패")
    void getOnePostFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());

        //when
        //then
        assertThatThrownBy(() -> postService.getOnePost(1L))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
    }

    @Test
    @DisplayName("게시글 좋아요 - 성공")
    void likePost() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().title("test").content("test1").user(user).build();
        PostLike postLike = PostLike.createPostLike(user, post);

        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(postLikeRepository.findByUserAndPost(any(User.class), any(Post.class))).willReturn(Optional.empty());
        given(postLikeRepository.save(any(PostLike.class))).willReturn(postLike);

        //when
        boolean response = postService.likePost(1L);

        //then
        assertThat(response).isEqualTo(true);

        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findById(anyLong());
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    @Test
    @DisplayName("게시글 좋아요 - Post PK를 찾지 못한 경우 실패")
    void likePostfail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());

        //when
        //then
        assertThatThrownBy(() -> postService.likePost(1L))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
    }

    @Test
    @DisplayName("게시글 스크랩 - 성공")
    void scrapPost() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().title("test").content("test1").user(user).build();
        Scrap scrap = Scrap.createScrap(user, post);

        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(scrapRepository.findByUserAndPost(any(User.class), any(Post.class))).willReturn(Optional.empty());
        given(scrapRepository.save(any(Scrap.class))).willReturn(scrap);

        //when
        boolean response = postService.scrapPost(1L);

        //then
        assertThat(response).isEqualTo(true);

        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findById(anyLong());
        verify(scrapRepository, times(1)).findByUserAndPost(any(User.class), any(Post.class));
        verify(scrapRepository, times(1)).save(any(Scrap.class));
    }

}