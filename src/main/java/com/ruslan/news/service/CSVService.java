package com.ruslan.news.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.ruslan.news.dto.CSVHelper;
import com.ruslan.news.model.News;
import com.ruslan.news.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class CSVService {
    @Autowired
    NewsRepository newsRepository;

    public void save(MultipartFile file) {
        try {
            List<News> news = CSVHelper.csvToNews(file.getInputStream());
            newsRepository.saveAll(news); //???
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load() {
        List<News> news = newsRepository.findAll();

        ByteArrayInputStream in = CSVHelper.newsToCSV(news);
        return in;
    }
    public boolean isExist(String newsTitle) {
        List<News> news = newsRepository.findAll();
        for (News n : news) {
            if (n.getTitle().equals(newsTitle)) {
                return true;
            }
        }
        return false;
    }

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }
    //поиск по источнику
    public List<News> findBySource(String source) {
        return newsRepository.findBySource(source);
    }
    //поиск по тематике
    public List<News> findByTopic(String topic) {
        return newsRepository.findByTopic(topic);
    }




    public void test() {
        System.out.println("Testing here with debug. Inside Hibernate Transaction");
    }



}