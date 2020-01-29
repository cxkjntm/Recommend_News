package us.codecraft.webmagic.Job;
import us.codecraft.webmagic.processor.example.SpiderPageProcessor;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class SpiderJob {
    private static int count =0;
    public static void main(String[] args) {

        new Timer( ).schedule(new TimerTask() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String nowStr = now.format(format);
                count ++;
                System.out.println(nowStr + " "+count+ " times to run ");
               try {
                    SpiderPageProcessor.Start();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            }, 1000 , 1000*3*60);
    }
}
