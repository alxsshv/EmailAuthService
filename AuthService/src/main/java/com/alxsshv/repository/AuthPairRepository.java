package com.alxsshv.repository;

import com.alxsshv.entity.AuthPair;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthPairRepository extends CrudRepository<AuthPair, String> {

    Optional<AuthPair> findByEmail(String email);

    void deleteAllByEmail(String email);
}
