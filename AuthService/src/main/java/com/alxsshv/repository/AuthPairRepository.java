package com.alxsshv.repository;

import com.alxsshv.entity.AuthPair;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AuthPairRepository extends CrudRepository<AuthPair, String> {

    Set<AuthPair> findAllByEmail(String email);

    void deleteAllByEmail(String email);
}
