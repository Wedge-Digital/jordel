package com.bloodbowlclub.auth.use_cases.projectors;

import com.bloodbowlclub.lib.domain.AggregateRoot;
import com.bloodbowlclub.lib.domain.events.DomainEvent;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.persistance.read_cache.ReadRepository;
import com.bloodbowlclub.lib.services.Result;

import java.util.Optional;

public class Projector {
    private final ReadRepository readRepository;
    public Projector(ReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public Result<Void> project(DomainEvent event) {
        Optional<ReadEntity> target = readRepository.findById(event.getAggregateId());
        if (target.isPresent()) {
            ReadEntity readEntity = target.get();
            Result<AggregateRoot> updatedAgregate = readEntity.getData().apply(event);
            if (updatedAgregate.isSuccess()) {
                return Result.failure(updatedAgregate.getError());
            }
            readEntity.setData(updatedAgregate.getValue());
            readRepository.save(readEntity);
        }
        return Result.success(null);
    }

}
