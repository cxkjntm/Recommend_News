package us.codecraft.webmagic.processor.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.MysqlPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.sql.NewsInfo;

import java.sql.SQLException;

/**
 * @author lxy <br>
 * @since 0.73
 */
public class SpiderPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    @Override
    public void process(Page page) throws SQLException {
        page.addTargetRequests(page.getHtml().links().regex("(https://[\\w\\-]+.huanqiu.com/article/[\\w\\-]+)").all());
        String author = page.getHtml().xpath("//span[@class='source']/span/allText()").toString();
        String title = page.getHtml().xpath("//div[@class='t-container-title']/allText()").toString();
        String news = page.getHtml().xpath("//section[@data-type='rtext']/tidyText()").toString();
        NewsInfo newsInfo = new NewsInfo();
        newsInfo.setAuthor(author);
        newsInfo.setTitle(title);
        newsInfo.setContent(news);
        page.putField("msg", newsInfo);
        //newsInfo.print();
        if (page.getResultItems().get("msg")==null){
            //skip this page
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void Start() throws SQLException {
        MysqlPipeline mysqlPipeline = new MysqlPipeline();
        Spider.create(new SpiderPageProcessor()).addUrl("https://www.huanqiu.com/").addPipeline(mysqlPipeline).thread(5).run();
    }
}
