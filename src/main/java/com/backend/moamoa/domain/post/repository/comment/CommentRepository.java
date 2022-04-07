package com.backend.moamoa.domain.post.repository.comment;

import com.backend.moamoa.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long>, CommentCustomRepository {

}
