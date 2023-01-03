package com.ruslan.news.controller;


import java.util.List;


import com.ruslan.news.dto.CSVHelper;
import com.ruslan.news.dto.ResponseMessage;
import com.ruslan.news.model.News;
import com.ruslan.news.repository.NewsRepository;
import com.ruslan.news.security.PersonDetails;
import com.ruslan.news.service.CSVService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;



@CrossOrigin("http://localhost:8080")
@Controller
@RequestMapping("/api/csv")
public class CSVController {


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












