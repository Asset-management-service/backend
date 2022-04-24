package com.backend.moamoa.domain.post.repository.post;

import com.backend.moamoa.domain.post.entity.PostImage;

import java.util.List;

public interface PostImageRepositoryCustom {
    List<PostImage> findBySavedImageUrl(Long postId);

    List<PostImage> findBySavedMoneyLogImageUrl(Long moneyLogId);

}
