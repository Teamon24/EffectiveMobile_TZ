package org.effective_mobile.task_management_system.database.repository;

import com.google.common.collect.Sets;
import org.effective_mobile.task_management_system.database.entity.Privilege;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.FluentQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@NoRepositoryBean
public class PrivilegeRepositoryStub implements PrivilegeRepository {

    public static String STUB_FOR_PRIVILEGES = "STUB_FOR_PRIVILEGES";

    /**
     * @param userId id of user.
     * @return privileges entities stub
     */
    @Override public HashSet<Privilege> findByUserId(Long userId) {
        return Sets.newHashSet(new Privilege(STUB_FOR_PRIVILEGES));
    }

    @Override public void flush() { }
    @Override public <S extends Privilege> S saveAndFlush(S entity) { return null; }
    @Override public <S extends Privilege> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
    @Override public void deleteAllInBatch(Iterable<Privilege> entities) { }
    @Override public void deleteAllByIdInBatch(Iterable<Long> longs) { }
    @Override public void deleteAllInBatch() { }
    @Override public Privilege getOne(Long aLong) { return null; }
    @Override public Privilege getById(Long aLong) { return null; }
    @Override public Privilege getReferenceById(Long aLong) { return null; }
    @Override public <S extends Privilege> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
    @Override public <S extends Privilege> List<S> findAll(Example<S> example) { return null; }
    @Override public <S extends Privilege> List<S> findAll(Example<S> example, Sort sort) { return null; }
    @Override public <S extends Privilege> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
    @Override public <S extends Privilege> long count(Example<S> example) { return 0; }
    @Override public <S extends Privilege> boolean exists(Example<S> example) { return false; }
    @Override public <S extends Privilege> S save(S entity) { return null; }
    @Override public <S extends Privilege> List<S> saveAll(Iterable<S> entities) { return null; }
    @Override public Optional<Privilege> findById(Long aLong) { return Optional.empty(); }
    @Override public boolean existsById(Long aLong) { return false; }
    @Override public List<Privilege> findAll() { return null; }
    @Override public List<Privilege> findAllById(Iterable<Long> longs) { return null; }
    @Override public long count() { return 0; }
    @Override public void deleteById(Long aLong) { }
    @Override public void delete(Privilege entity) { }
    @Override public void deleteAllById(Iterable<? extends Long> longs) { }
    @Override public void deleteAll(Iterable<? extends Privilege> entities) { }
    @Override public void deleteAll() { }
    @Override public List<Privilege> findAll(Sort sort) { return null; }
    @Override public Page<Privilege> findAll(Pageable pageable) { return null; }
    @Override public <S extends Privilege, R> R findBy(
        Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) {
        return null;
    }
}
