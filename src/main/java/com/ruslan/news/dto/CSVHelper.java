package com.ruslan.news.dto;

import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import com.ruslan.news.model.News;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;


public class CSVHelper extends Thread {
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "Id", "Source", "Topic", "Title" };



    @Override
    public void run() {

    }

    public static boolean hasCSVFormat(MultipartFile file) {
        if (TYPE.equals(file.getContentType())
                || file.getContentType().equals("application/vnd.ms-excel")) {
            return true;
        }

        return false;
    }

    public static List<News> csvToNews(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<News> newsList = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                News news = new News(
                        Long.parseLong(csvRecord.get("Id")),
                        csvRecord.get("Source"),
                        csvRecord.get("Topic"),
                        csvRecord.get("Title"),
                        Boolean.parseBoolean(csvRecord.get("Published"))
                );

                newsList.add(news); //source topic title
            }

            return newsList;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }


    public static ByteArrayInputStream newsToCSV(List<News> newsList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (News news : newsList) {
                    List<String> data = Arrays.asList(
                            String.valueOf(news.getId()),
                            news.getSource(),
                            news.getTopic(),
                            news.getTitle(),
                            String.valueOf(news.isPublished())
                );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}