package com.example.tingesoM.Repositorie;

import com.example.tingesoM.Dtos.GroupToolsDto;
import com.example.tingesoM.Dtos.InitialCondition;
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

    @Query("""
            SELECT COUNT(t) FROM Tool t 
            WHERE t.name = :name 
            AND t.loanFee = :loanFee 
            AND t.category = :category 
            AND t.status = true 
            AND t.underRepair = false 
            AND t.deleteStatus = false
                        """)
    int countAvailableByNameAndCategory(@Param("name") String name, @Param("category") String category, @Param("loanFee") Integer loanFee);


    @Query("""
            SELECT t FROM Tool t WHERE 
            (:name IS NULL OR t.name = :name) AND 
            (:category IS NULL OR t.category = :category) AND 
            (:loanFee IS NULL OR t.loanFee = :loanFee)
                        """)
    List<Tool> findByNameCategoryAndLoanFee(@Param("name") String name, @Param("category") String category, @Param("loanFee") Integer loanFee);

    @Query("""
            SELECT new com.example.tingesoM.Dtos.GroupToolsDto(
                        t.name, t.category, t.initialCondition, t.loanFee, t.penaltyForDelay, t.replacementValue, t.damageValue, COUNT(t))
            FROM Tool t
            WHERE t.status = true
            AND t.deleteStatus = false
            AND t.underRepair = false
            GROUP BY t.name, t.category, t.initialCondition, t.loanFee, t.penaltyForDelay, t.replacementValue, t.damageValue
            """)
    List<GroupToolsDto> agroupByNameCategoryAndLoanFee();


    @Query("""
            SELECT t
            FROM Tool t
            WHERE t.name =: name
            AND t.category =: category
            AND t.loanFee =: loanFee
            AND t.initialCondition =: initialCondition
            AND t.penaltyForDelay =: penaltyForDelay
            AND t.replacementValue =: replacementValue
            AND t.damageValue =: damegeValue
            AND t.status = true
            AND t.deleteStatus = false
            AND t.underRepair = false
            """)
    Tool getByAtributs(@Param("name") String name, @Param("category") String category, @Param("loanFee") Integer loanFee,
                                          @Param("initialCondition") InitialCondition initialCondition, @Param("penaltyForDelay") Integer penaltyForDelay,
                                          @Param("replacementValue") Integer replacementValue, @Param("damageValue") Integer damageValue);

}

