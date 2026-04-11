package com.emailanalyzer.dao;

import com.emailanalyzer.entity.UrgencyScore;

public interface UrgencyScoreDAO {

    void save(UrgencyScore urgencyScore);

    void update(UrgencyScore urgencyScore);

    UrgencyScore findByEmailId(Long emailId);

    void delete(Long emailId);
}
