package ${business_package}.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ${business_package}.model.${classname};

@Repository
public interface ${tablename}Repository extends JpaRepository<${classname}, Long>{
    
}