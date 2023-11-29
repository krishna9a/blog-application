package com.mountblue.blogapplication.repositories;

import com.mountblue.blogapplication.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TagRepository extends JpaRepository<Tag,Integer> {
     Tag findByName(String tagName);

}
