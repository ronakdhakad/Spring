package com.emailanalyzer.dao;

import com.emailanalyzer.entity.Tag;
import java.util.List;

/**
 * Data Access Object for Tag entity.
 */
public interface TagDAO {

    void save(Tag tag);

    Tag findById(Long id);

    Tag findByName(String name);

    List<Tag> findAll();

    void delete(Long id);
}
