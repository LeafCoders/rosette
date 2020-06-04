package se.leafcoders.rosette.core.persistable;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ModelRepository<T> extends CrudRepository<T, Long>, JpaSpecificationExecutor<T> {

    List<T> findAll(Sort sort);
}
