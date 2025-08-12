package com.crm.user.data.repository;

import com.crm.user.data.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive repository for {@link User} entity.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    /**
     * Kullanıcı adını benzersiz olarak arar.
     *
     * @param userName kullanıcı adı
     * @return Mono<User>
     */
    Mono<User> findByUserName(String userName);

    /**
     * Finds a user by the stored refresh token hash
     */
    Mono<User> findByRefreshTokenHash(String refreshTokenHash);

}
