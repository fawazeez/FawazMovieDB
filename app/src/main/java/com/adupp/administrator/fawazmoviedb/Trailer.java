package com.adupp.administrator.fawazmoviedb;

import java.util.List;

/**
 * Created by fawaz on 2/19/2016.
 */
public class Trailer {

    public Integer id;
    public List<Result> results ;
    public class Result
    {
         String id;
         String iso6391;
         String key;
         String name;
         String site;
         Integer size;
         String type;
    }
}
