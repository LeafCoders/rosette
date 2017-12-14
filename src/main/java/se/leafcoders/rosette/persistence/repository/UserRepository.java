package se.leafcoders.rosette.persistence.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.leafcoders.rosette.persistence.model.Group;
import se.leafcoders.rosette.persistence.model.User;

@Repository
public interface UserRepository extends ModelRepository<User> {
    User findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u INNER JOIN u.groups g WHERE g IN (:groups)")
    List<User> findUsersInGroups(@Param("groups") Collection<Group> groups);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :time WHERE u.id = :id")
    int setLastLoginTime(@Param("id") Long id, @Param("time") LocalDateTime time);
}
