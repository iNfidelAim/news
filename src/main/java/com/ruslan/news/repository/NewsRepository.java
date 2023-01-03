package com.ruslan.news.repository;

import com.ruslan.news.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.xml.transform.Source;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAll();

    //поиск по источнику
    List<News> findBySource(String source);

    //поиск по тематике
    List<News> findByTopic(String topic);
}


