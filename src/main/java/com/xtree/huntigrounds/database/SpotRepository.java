package com.xtree.huntigrounds.database;

import com.xtree.huntigrounds.data.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    Spot findByCode(String code);

    List<Spot> findAll();

    @Modifying
    @Transactional
    @Query("update Spot s set s.might = ?1, s.address = ?2 , s.description = ?3 , s.enabled = ?4 where s.code = ?5")
    void setPlaceDetailsByCode(int might, String address, String description, boolean enabled, String code);
}
