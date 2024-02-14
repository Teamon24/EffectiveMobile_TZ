package org.effective_mobile.task_management_system.database.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.EntityManager;
import org.effective_mobile.task_management_system.database.entity.QTask;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>Репозиторий, сделан для того, чтобы обойти проблему фильтрации при использовании spring-репозитория, когда отсутствует один из фильтров поиска.
 * <p>Допустим есть два фильтра: ... where f1 = F1 AND f2 = F2.
 * Если они оба заданы, то выполняется корректный запрос.
 * Если F1 = null и F2 != null, то фильтр where f1 = null AND f2 = F2
 * уже имеет другой смысл - не надо фильтровать по f1.
 * Но из выборки исключаются результаты, где поле f1 не null, что уже не верно.
 *
 * <p>Решается проблема при помощи динамического формирования запроса.
 */
@Repository
public class FilteredAndPagedTaskRepositoryImpl implements FilteredAndPagedTaskRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public FilteredAndPagedTaskRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public Page<Task> findByCreatorAndExecutor(
        @Nullable String creator,
        @Nullable String executor,
        Pageable pageable
    ) {
        QTask task = QTask.task;

        Long totalCount = filter(selectCount(task), task, creator, executor).fetchOne();
        List<Task> fetch = filter(selectFrom(task), task, creator, executor)
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

        return PageableExecutionUtils.getPage(fetch, pageable, () -> totalCount);
    }

    private JPAQuery<Task> selectFrom(QTask task) {
        return jpaQueryFactory.selectFrom(task);
    }

    private JPAQuery<Long> selectCount(QTask task) {
        return jpaQueryFactory.select(task.id.count()).from(task);
    }

    private <T> JPAQuery<T> filter(
        JPAQuery<T> from, QTask task,
        String creator,
        String executor
    ) {
        JPAQuery<T> query = from;
        if (creator != null) query = query.where(task.creator.username.eq(creator));
        if (executor != null) query = query.where(task.executor.username.eq(executor));
        return query;
    }
}