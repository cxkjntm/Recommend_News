package us.codecraft.webmagic.processor.example;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
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
public class AjaxPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("www.huanqiu.com").setSleepTime(3000);

    private static final String LIST_URL = "https://www.huanqiu.com/article/.*";
    private static final String Help_URL = "https://[a-z]+.huanqiu.com/api/list";
    public  final int count = 0;
    @Override
    public void process(Page page) {
        NewsInfo newsInfo = new NewsInfo();
       if (page.getUrl().regex(LIST_URL).match()) {
            System.out.println("进入目标页");
            newsInfo.setContent(page.getHtml().xpath("//section[@data-type='rtext']/tidyText()").toString());
            newsInfo.setTitle(page.getHtml().xpath("//div[@class='t-container-title']/allText()").toString());
            newsInfo.setAuthor(page.getHtml().xpath("//span[@class='source']/span/allText()").toString());
            page.putField("msg", newsInfo);
        } else {
            if(page.getUrl().regex(Help_URL).match()) {
                System.out.println("进入列表页");
                List<String> aids = new JsonPathSelector("$.list[*].aid").selectList(page.getRawText());
                for(String a:aids){
                   String NEXTURL = "https://www.huanqiu.com/article/";
                   NEXTURL += a;
                   System.out.println(NEXTURL);
                    page.addTargetRequest(NEXTURL);
                }
            }
            else {
                List<String> nodes = new JsonPathSelector("$.children[*].children[*].node").selectList(page.getRawText());
                if (CollectionUtils.isNotEmpty(nodes)) {
                    List<String> URL = new JsonPathSelector("$.children[*].url").selectList(page.getRawText());
                    //截取请求前部

                   String NEXTURL = "list?node=";
                    //添加节点
                    for (String n : nodes) {
                        NEXTURL = NEXTURL + "%22" + n +"%22,";
                    }
                    //去除最后一个逗号
                    NEXTURL = NEXTURL.substring(0, NEXTURL.length()-1);
                    //添加数据库限制值

                    for(int i = 0 ; i < 100 ; i += 20) {
                        String nexturl = NEXTURL;
                        nexturl += "&offset=" + i + "&limit=" + (i + 20);
                        System.out.println("page for "+i+" to "+ (i+20));
                        page.addTargetRequest(nexturl);
                    }
                    NEXTURL=null;
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
        StringArray urls = new StringArray();
        urls.add("//world.huanqiu.com");
        urls.add("//china.huanqiu.com");
        urls.add("//mil.huanqiu.com");
        urls.add("//taiwan.huanqiu.com");
        urls.add("//opinion.huanqiu.com");
        urls.add("//finance.huanqiu.com");
        urls.add("//tech.huanqiu.com");
        urls.add("//auto.huanqiu.com");
        urls.add("//art.huanqiu.com");
        urls.add("//go.huanqiu.com");
        urls.add("//health.huanqiu.com");
        urls.add("//sports.huanqiu.com");
        urls.add("//quality.huanqiu.com");
        urls.add("//bigdata.huanqiu.com");
        urls.add("//look.huanqiu.com");
        urls.add("//chamber.huanqiu.com");
        urls.add("//biz.huanqiu.com");

        for(int i = 0 ; i < 17 ;i ++) {
            request.setUrl("https:"+urls.get(i)+"/api/channel_pc");
            request.setMethod("get");
            //System.out.println("now crawer for page "+i);
            Spider.create(new AjaxPageProcessor()).addRequest(request).addPipeline(mysqlPipeline).thread(20).run();
        }
    }
}