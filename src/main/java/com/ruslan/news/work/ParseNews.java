package com.ruslan.news.work;

import com.ruslan.news.model.News;
import com.ruslan.news.service.CSVService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ParseNews {

    CSVService csvService;

    @Scheduled(cron = "00 2 * * * *")

    public void parseNewNews() {
//тут будет исключение, unknown protocol: localhost.  MalformedURLException, IllegalArgumentException
        String url = "localhost:8080/api/csv/tutorials";

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Chrome")
                    .timeout(2000)
                    .get();
            Elements news = doc.getElementsByAttribute("source");
            for (Element el : news) {
                String title = el.ownText();
                if (!csvService.isExist(title)) {
                    News obj = new News();
                    obj.setTitle(title);
                    csvService.save((MultipartFile) obj);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
