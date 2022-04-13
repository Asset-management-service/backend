package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
    // PostId 내림차순으로 데이터 불러오기
    Page<Post> findByUserOrderByIdDesc(User user, Pageable pageable);

}
