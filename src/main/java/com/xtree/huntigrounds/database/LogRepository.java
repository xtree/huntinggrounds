package com.xtree.huntigrounds.database;

import com.xtree.huntigrounds.data.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    List<Log> findAll();

}
