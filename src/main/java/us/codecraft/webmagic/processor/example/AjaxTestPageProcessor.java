package us.codecraft.webmagic.processor.example;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.MysqlPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.sql.NewsInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public class AjaxTestPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("3w.huanqiu.com").setSleepTime(3000);

    private static final String LIST_URL = "https://3w.huanqiu.com/a/.*";
    private static final String Help_URL = "https://3w.huanqiu.com/api/list.*";

    @Override
    public void process(Page page) {
        NewsInfo newsInfo = new NewsInfo();
        if (page.getUrl().regex(LIST_URL).match()) {
            newsInfo.setContent(page.getHtml().xpath("//div[@class='a-con']/tidyText()").toString());
            newsInfo.setTitle(page.getHtml().xpath("//h1[@class='a-title']/tidyText()").toString());
            newsInfo.setAuthor(page.getHtml().xpath("//a[@class='s-name']/text()").toString());
            page.putField("msg", newsInfo);
        } else {
            if(page.getUrl().regex(Help_URL).match()) {
                List<String> ids = new JsonPathSelector("$.data[*].path").selectList(page.getRawText());
                System.out.println(ids.size());
                if (CollectionUtils.isNotEmpty(ids)) {
                    for (String n : ids) {
                        String NEXTURL = "https://3w.huanqiu.com" + n;
                        System.out.println(NEXTURL);
                        page.addTargetRequest(NEXTURL);
                    }
                }
            }

        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws SQLException {
        MysqlPipeline mysqlPipeline = new MysqlPipeline();
        Request request = new Request();
        //真实请求地址
        String url = "https://3w.huanqiu.com/api/m/c36dc8?page=";
        for(int i = 1 ; i <= 100 ; i ++) {
            request.setUrl(url+i);
            request.setMethod("get");
            System.out.println("now crawer for page "+i);
            Spider.create(new AjaxTestPageProcessor()).addRequest(request).addPipeline(mysqlPipeline).run();
        }
    }
}