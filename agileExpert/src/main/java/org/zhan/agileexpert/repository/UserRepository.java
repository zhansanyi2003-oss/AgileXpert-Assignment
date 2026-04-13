package org.zhan.agileexpert.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zhan.agileexpert.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByName(String name);
}
