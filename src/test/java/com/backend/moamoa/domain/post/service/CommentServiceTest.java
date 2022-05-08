package com.backend.moamoa.domain.post.service;

import com.backend.moamoa.builder.UserBuilder;
import com.backend.moamoa.domain.post.dto.request.CommentRequest;
import com.backend.moamoa.domain.post.dto.request.CommentUpdateRequest;
import com.backend.moamoa.domain.post.dto.response.CommentResponse;
import com.backend.moamoa.domain.post.entity.Comment;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.post.repository.comment.CommentRepository;
import com.backend.moamoa.domain.post.repository.post.PostRepository;
import com.backend.moamoa.domain.user.entity.User;
import com.backend.moamoa.global.audit.TimeEntity;
import com.backend.moamoa.global.exception.CustomException;
import com.backend.moamoa.global.utils.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserUtil userUtil;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("게시글 댓글 생성 - 성공")
    void createComment() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().title("test1").content("test1").user(user).build();
        Comment comment = Comment.builder().parent(null).user(user).post(post).build();
        comment.setTimeEntity(new TimeEntity());

        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        //when
        CommentResponse response = commentService.createComment(new CommentRequest(1L, null, "test"));

        //then
        assertThat(response.getUsername()).isEqualTo(user.getNickname());

        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시글 대댓글 생성 - ParentId 값이 있다면 대댓글 생성")
    void createChildComment() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().title("test1").content("test1").user(user).build();
        Comment parent = Comment.builder().user(user).post(post).build();
        Comment child = Comment.builder().user(user).parent(parent).post(post).build();
        child.setTimeEntity(new TimeEntity());

        given(userUtil.findCurrentUser()).willReturn(user);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(parent));
        given(commentRepository.save(any(Comment.class))).willReturn(child);

        //when
        CommentResponse response = commentService.createComment(new CommentRequest(2L, 1L, "test"));

        //then
        assertThat(response.getUsername()).isEqualTo(user.getNickname());

        verify(userUtil, times(1)).findCurrentUser();
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("게시글 댓글 삭제 - 성공")
    void deleteComment() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().title("test1").content("test1").user(user).build();
        Comment comment = Comment.builder().user(user).post(post).build();
        given(userUtil.findCurrentUser()).willReturn(user);
        given(commentRepository.findWithPostAndMemberById(anyLong(), anyLong())).willReturn(Optional.of(comment));

        //when
        commentService.deleteComment(1L);

        //then
        verify(userUtil, times(1)).findCurrentUser();
        verify(commentRepository, times(1)).delete(any(Comment.class));
        verify(commentRepository, times(1)).findWithPostAndMemberById(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 댓글 삭제 - Comment PK를 찾지 못한 경우 실패")
    void deleteCommentFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(commentRepository.findWithPostAndMemberById(anyLong(), anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> commentService.deleteComment(1L))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(commentRepository, times(1)).findWithPostAndMemberById(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 댓글 수정 - 성공")
    void updateComment() {
        //given
        User user = UserBuilder.dummyUser();
        Post post = Post.builder().title("test1").content("test1").user(user).build();
        Comment comment = Comment.builder().user(user).post(post).build();

        given(userUtil.findCurrentUser()).willReturn(user);
        given(commentRepository.findWithPostAndMemberById(anyLong(), anyLong())).willReturn(Optional.of(comment));

        //when
        commentService.updateComment(new CommentUpdateRequest(1L, "test"));

        //then
        verify(userUtil, times(1)).findCurrentUser();
        verify(commentRepository, times(1)).findWithPostAndMemberById(anyLong(), anyLong());
    }

    @Test
    @DisplayName("게시글 댓글 수정 - 해당 회원의 권한이 없는 경우 실패")
    void updateCommentFail() {
        //given
        given(userUtil.findCurrentUser()).willReturn(UserBuilder.dummyUser());
        given(commentRepository.findWithPostAndMemberById(anyLong(), anyLong())).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> commentService.updateComment(new CommentUpdateRequest(1L, "test")))
                .isInstanceOf(CustomException.class);

        verify(userUtil, times(1)).findCurrentUser();
        verify(commentRepository, times(1)).findWithPostAndMemberById(anyLong(), anyLong());
    }

}