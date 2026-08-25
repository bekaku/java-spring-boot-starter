package ${rootPackage}.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ${rootPackage}.model.${entityName};

@Repository
public interface ${entityName}Repository extends BaseRepository<${entityName}, Long>, JpaSpecificationExecutor<${entityName}> {
}