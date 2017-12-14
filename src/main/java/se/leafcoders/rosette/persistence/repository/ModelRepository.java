package se.leafcoders.rosette.persistence.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ModelRepository<T> extends CrudRepository<T, Long> {

    List<T> findAll(Sort sort);
}
