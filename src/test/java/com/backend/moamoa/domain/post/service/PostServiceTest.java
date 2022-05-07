package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.post.dto.request.PostRequest;
import com.backend.moamoa.domain.post.dto.request.PostUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.PostCreateResponse;
import com.backend.moamoa.domain.post.dto.response.PostUpdateResponse;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.entity.PostCategory;
import com.backend.moamoa.domain.post.entity.PostImage;
import com.backend.moamoa.domain.post.repository.post.PostCategoryRepository;
import com.backend.moamoa.domain.post.repository.post.PostImageRepository;
import com.backend.moamoa.domain.post.repository.post.PostRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.exception.ErrorCode;
import com.backend.moamoa.global.s3.S3Uploader;
import com.backend.moamoa.global.utils.UserUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

}