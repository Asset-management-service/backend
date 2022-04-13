package com.backend.moamoa.domain.post.repository.comment;

import com.backend.moamoa.domain.post.entity.Comment;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentCustomRepository {
    // CommentId 내림차순(최신 순)으로 데이터 불러오기
    Page<Comment> findByUserOrderByIdDesc(User user, Pageable pageable);

}
