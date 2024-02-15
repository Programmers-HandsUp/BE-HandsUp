package dev.handsup.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.handsup.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByName(String name);

	Optional<User> findByEmail(String email);
}
