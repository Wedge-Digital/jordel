package com.bloodbowlclub.lib.persistance.event_store.fake;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.persistance.event_store.EventEntity;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.services.Result;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FakeEventStore implements EventStore {

    private final List<EventEntity> eventEntities =  new ArrayList<>();
    @Override
    public void flush() {

    }

    @Override
    public <S extends EventEntity> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends EventEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<EventEntity> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public EventEntity getOne(String s) {
        return null;
    }

    @Override
    public EventEntity getById(String s) {
        return null;
    }

    @Override
    public EventEntity getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends EventEntity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends EventEntity> List<S> findAll(Example<S> example) {
        return (List<S>) this.eventEntities;
    }

    @Override
    public <S extends EventEntity> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends EventEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends EventEntity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends EventEntity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends EventEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends EventEntity> S save(S entity) {
        this.eventEntities.add(entity);
        return entity;
    }

    @Override
    public <S extends EventEntity> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this.eventEntities::add);
        return (List<S>) this.eventEntities;
    }


    @Override
    public Optional<EventEntity> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<EventEntity> findAll() {
        return this.eventEntities;
    }

    @Override
    public List<EventEntity> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(EventEntity entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends EventEntity> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<EventEntity> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<EventEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<EventEntity> findBySubject(String agregateId) {
        return this.eventEntities.stream().filter(evt -> evt.getSubject().equals(agregateId) ).toList();
    }

    @Override
    public List<EventEntity> findBySource(String userId) {
        return List.of();
    }

    @Override
    public List<EventEntity> findBySourceAndSubject(String source, String subject) {
        return List.of();
    }
}
