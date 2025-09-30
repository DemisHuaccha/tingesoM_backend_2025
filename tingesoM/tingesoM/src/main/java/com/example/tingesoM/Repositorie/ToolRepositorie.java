package com.example.tingesoM.Repositorie;

import com.example.tingesoM.Entities.Cardex;
import com.example.tingesoM.Entities.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolRepositorie extends JpaRepository<Tool,Long> {
    Optional<Tool> findByName(String name);
    Optional<Tool> findById(Long id);

    @Query("SELECT COUNT(t) FROM Tool t " +
            "WHERE t.name = :name " +
            "AND t.loanFee = :loanFee " +
            "AND t.category = :category " +
            "AND t.status = true " +
            "AND t.underRepair = false " +
            "AND t.deleteStatus = false")
    int countAvailableByNameAndCategory(@Param("name") String name, @Param("category") String category, @Param("loanFee") Integer loanFee);


    @Query("SELECT t FROM Tool t WHERE " +
            "(:name IS NULL OR t.name = :name) AND " +
            "(:category IS NULL OR t.category = :category) AND " +
            "(:loanFee IS NULL OR t.loanFee = :loanFee)")
    List<Tool> findByNameCategoryAndLoanFee(@Param("name") String name, @Param("category") String category, @Param("loanFee") Integer loanFee);

}

