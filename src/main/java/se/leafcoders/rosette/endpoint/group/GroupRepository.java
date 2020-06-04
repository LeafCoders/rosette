package se.leafcoders.rosette.endpoint.group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface GroupRepository extends ModelRepository<Group> {

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Group g INNER JOIN g.users u WHERE u.id = :userId and g.id = :groupId")
    boolean isUserInGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
