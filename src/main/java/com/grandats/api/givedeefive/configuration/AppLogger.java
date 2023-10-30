package com.grandats.api.givedeefive.configuration;


import com.grandats.api.givedeefive.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Component
public record AppLogger(String filePath) {

    //    @Value("${logging.file.path}")
//    String logingFilePath;

    @Autowired
    public AppLogger(@Value("${logging.file.path}") String filePath) {
        this.filePath = filePath;
    }
    public void log3(String fileName, String message) {
        //just to make our log file nicer :)
        Logger logger = Logger.getLogger("MyLog");
        SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        String loggerName = DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_FORMAT) + ".log";
        FileHandler fh = null;
        try {
            fh = new FileHandler(filePath +"/"+fileName+"_"+ loggerName);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        assert fh != null;
        assert fh != null;
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
//        logger.setUseParentHandlers(true);

        logger.info(DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_TIME_WITH_FACTION_OR_SECOND_FORMAT) + " " + message);
    }
    public void log(String fileName, String message) {
        //https://stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger
        String loggerName = DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_FORMAT) + ".log";
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;
        try {
//            logger.setUseParentHandlers(false);
            fh = new FileHandler(filePath + "/" + fileName + "-" + loggerName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        logger.info(DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_TIME_WITH_FACTION_OR_SECOND_FORMAT) + " " + message);
    }
    public void logBak(String fileName, String message) {
        String loggerName = DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_FORMAT) + ".log";
        //https://stackoverflow.com/questions/44988143/java-logger-wont-log-into-file
        System.out.println("Log file path : " + filePath);
        Logger logger = Logger.getLogger(loggerName);
        FileHandler fh;
        try {
            logger.setUseParentHandlers(true);
            fh = new FileHandler(filePath + "/" + fileName + "-" + loggerName, false);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.FINE);
            logger.addHandler(fh);
            logger.setLevel(Level.FINE);
        } catch (IOException ioe) {
            throw new ExceptionInInitializerError(ioe);
        }


        logger.info(DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_TIME_WITH_FACTION_OR_SECOND_FORMAT) + " " + message);


//        try {
//            fh = new FileAppender(new SimpleLayout(), filePath + "/" + fileName + "-" + DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_FORMAT) + ".log");
//            logger.addAppender(fh);
//            fh.setLayout(new SimpleLayout());
//        } catch (SecurityException | IOException e) {
//            e.printStackTrace();
//        }
//
//        logger.info(DateUtil.getLocalDateTimeByFormat(DateUtil.getLocalDateTimeNow(), DateUtil.DATE_TIME_WITH_FACTION_OR_SECOND_FORMAT)+" "+message);
    }
}
