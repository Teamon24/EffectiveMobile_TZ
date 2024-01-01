package org.effective_mobile.task_management_system.database.repository;

import org.effective_mobile.task_management_system.database.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends AbstractJpaRepository<Comment, Long> {
}
