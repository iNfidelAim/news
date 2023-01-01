package com.ruslan.news.controller;


import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ruslan.news.dto.CSVHelper;
import com.ruslan.news.dto.ResponseMessage;
import com.ruslan.news.model.News;
import com.ruslan.news.repository.NewsRepository;
import com.ruslan.news.security.PersonDetails;
import com.ruslan.news.service.CSVService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static java.sql.DriverManager.getConnection;


@CrossOrigin("http://localhost:8080")
@Controller
@RequestMapping("/api/csv")
public class CSVController   {


    //пагинация
    @RestController
    @RequestMapping("api/post")
    @RequiredArgsConstructor
    public class PostController<Post> {

        private final NewsRepository repository;

            //для Security. реализую метод чтобы он имел доступ к джава потоку
        @GetMapping("/showUserInfo")
        public String showUserInfo() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //даункастим чтобы вызвать getPerson
            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            System.out.println(personDetails.getPerson());

            return "hello";
        }

        @GetMapping
        public Page<Post> getAll(
                @RequestParam("offset") Integer offset,
                @RequestParam("limit") Integer limit
        ) {
            return (Page<Post>) repository.findAll(PageRequest.of(offset, limit));
        }

    }


    @Autowired
    CSVService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (CSVHelper.hasCSVFormat(file)) {
            try {
                fileService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();

                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/csv/download/")
                        .path(file.getOriginalFilename())
                        .toUriString();

                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message, fileDownloadUri));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message, ""));
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message, ""));
    }

    @GetMapping("/news")
    public ResponseEntity<List<News>> getAllNews() {
        try {
            List<News> news = fileService.getAllNews();

            if (news.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(news, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/download/{fileName:.+}")
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
            PreparedStatement fileName2 = getConnection("jdbc:postgresql://localhost:5432/news")
                    .prepareStatement("SELECT DISTINCT source FROM News WHERE source = irbis.plus");
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
/*
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFileSourceTwo(@PathVariable String fileName)  {

        InputStreamResource file = new InputStreamResource(fileService.load());

        try {
            //да тут дубликейт, по хорошему можно в методы сделать, но решил оставить так
            Connection con = getConnection("jdbc:postgresql://localhost:5432/news");
            PreparedStatement statement = con.prepareStatement("SELECT topic, COUNT(topic) FROM News WHERE source = 'irbis.plus' group by topic");


            ResultSet result = statement.executeQuery();

            ArrayList<String> array = new ArrayList<>();

            while (result.next()) {
                array.addAll((Collection<? extends String>) statement);
            }
            PreparedStatement fileName2 = getConnection("jdbc:postgresql://localhost:5432/news")
                    .prepareStatement("SELECT DISTINCT source FROM News WHERE source = 'praktika.irbis.plus'");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName2)
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .body(file);

            } catch (
                Exception e) {
            System.out.println(e);
            return null;
        }

    }*/

        //этот метод исключительно для проверки поиска по источнику и тематике
    @GetMapping
    public String testIndex(Model model) {
        model.addAttribute("news", fileService.getAllNews().get(0));

        fileService.findBySource("irbis.plus");
        fileService.findByTopic("Помощь юр. лицам");

        fileService.test();

        return "news/testIndex";
    }
}


















