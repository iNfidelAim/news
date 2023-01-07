package com.ruslan.news.controller;

import com.ruslan.news.service.CSVService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import static java.sql.DriverManager.getConnection;


@RestController
@RequestMapping("api/post")
@RequiredArgsConstructor

public class DownloadSourceOne<Post> extends Thread {

    @Autowired
    CSVService fileService;


    @GetMapping("/download/{fileName:irbisPlus}")
    public ResponseEntity<Resource> downloadFileSource(@PathVariable String fileName)  {


        InputStreamResource file = new InputStreamResource(fileService.load());

        try {
            Connection con = getConnection("jdbc:postgresql://localhost:5432/news");
            PreparedStatement statement = con.prepareStatement("SELECT topic, COUNT(topic) FROM News WHERE source = 'irbis.plus' group by topic");


            ResultSet result = statement.executeQuery();

            ArrayList<String> array = new ArrayList<>();

            while (result.next()) {
                array.addAll((Collection<? extends String>) statement);
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .body(file);

        } catch (
                Exception e) {
            System.out.println(e);
            return null;
        }

    }

    @Override
    public void run() {
        downloadFileSource("irbisPlus");
    }
}

