package com.emailanalyzer.dao;

import com.emailanalyzer.entity.Rule;
import java.util.List;

public interface RuleDAO {

    void save(Rule rule);

    void update(Rule rule);

    Rule findById(Long id);

    List<Rule> findAll();

    List<Rule> findByType(String type);

    void delete(Long id);
}
