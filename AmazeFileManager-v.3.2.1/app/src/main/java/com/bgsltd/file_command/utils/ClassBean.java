package com.bgsltd.file_command.utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Yunwen on 7/10/2017.
 * Update by fox on 9/04/2017
 */

public class ClassBean implements Serializable {

    /**
     * teacherId : 596d96067c9c9058a177da1f
     * teacherName : Life Nuggin
     * coverImageUri : http://vode-test.oss-us-west-1.aliyuncs.com/596d96067c9c9058a177da1f/地球磁场颠倒.jpg
     * lesson : [{"num":"0","description":"Eventually, Earth's magnetic fields will flip. What will happen then?","title":"[life nuggin]地球磁場顛倒會發生什麼事"}]
     * description : Eventually, Earth's magnetic fields will flip. What will happen then?
     * id : 597c69997c9c9006ad8ebcd9
     * tag : ["磁场","地球","环境"]
     * title : [life nuggin]地球磁場顛倒會發生什麼事
     * category : ["理学"]
     */

    private String teacherId;

    private String teacherName;

    private String coverImageUri;

    private String description;

    private String id;

    private String title;

    private String tagString;

    private List<LessonBean> lesson;

    private List<String> tag;

    private List<String> category;

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCoverImageUri() {
        return coverImageUri;
    }

    public void setCoverImageUri(String coverImageUri) {
        this.coverImageUri = coverImageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTagString() {
        return tagString;
    }

    public void setTagString(String tagString) {
        this.tagString = tagString;
    }

    public List<LessonBean> getLesson() {
        return lesson;
    }

    public void setLesson(List<LessonBean> lesson) {
        this.lesson = lesson;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public static class LessonBean {

        /**
         * num : 0
         * description : Eventually, Earth's magnetic fields will flip. What will happen then?
         * title : [life nuggin]地球磁場顛倒會發生什麼事
         */

        private String num;

        private String description;

        private String title;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}