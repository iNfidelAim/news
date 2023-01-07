package com.ruslan.news.service;

import com.ruslan.news.controller.DownloadSourceOne;
import com.ruslan.news.controller.DownloadSourceTwo;


public class DownloadCSVService extends Thread {
    public static void main(String[] args) throws InterruptedException {

        DownloadSourceOne downloadSourceOne = new DownloadSourceOne<>();
        downloadSourceOne.start();

        DownloadSourceTwo downloadSourceTwo = new DownloadSourceTwo();
        downloadSourceTwo.start();


    }
    }
