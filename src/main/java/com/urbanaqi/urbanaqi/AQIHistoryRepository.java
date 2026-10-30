
package com.urbanaqi.urbanaqi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AQIHistoryRepository
        extends JpaRepository<AQIHistory, Long> {

    List<AQIHistory> findAllByOrderByTimeDesc();

}
