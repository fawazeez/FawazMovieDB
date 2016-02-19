package com.adupp.administrator.fawazmoviedb;

import java.util.List;

/**
 * Created by fawaz on 2/19/2016.
 */
public class Review {
    Integer id;
     Integer page;
     List<Result> results ;
    public class Result
    {
         String id;
         String author;
         String content;
         String url;
    }
}
