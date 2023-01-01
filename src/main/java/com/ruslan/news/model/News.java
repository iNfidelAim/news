package com.ruslan.news.model;

import javax.persistence.*;

@Entity
@Table(name = "news")
public class News {
    //source topic title
    //source - источник, topic - тематика, title - новость (ну вот так ничего лучше не придумал, а транслитом писать не стал =))
        @Id
        @Column(name = "id")
        private long id;

        @Column(name = "source")
        private String source;

        @Column(name = "topic")
        private String topic;

        @Column(name = "title")
        private String title;

        @Column(name = "published")
        private boolean published;

        public News() {
        }

    public News(long id, String source, String topic, String title, boolean published) {
        this.id = id;
        this.source = source;
        this.title = title;
        this.published = published;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", topic='" + topic + '\'' +
                ", title='" + title + '\'' +
                ", published=" + published +
                '}';
    }
}
