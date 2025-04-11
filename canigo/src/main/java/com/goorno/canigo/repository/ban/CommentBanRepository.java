package com.goorno.canigo.repository.ban;

import org.springframework.data.jpa.repository.JpaRepository;

import com.goorno.canigo.entity.ban.CommentBan;

public interface CommentBanRepository extends JpaRepository<CommentBan, Long> {

}
