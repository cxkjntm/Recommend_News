package us.codecraft.webmagic.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.sql.Dbconn;
import us.codecraft.webmagic.sql.NewsInfo;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class MysqlPipeline implements Pipeline {
    Statement statement= Dbconn.ReturnStatement();

    public MysqlPipeline() throws SQLException {
    }

    @Override
    public void process(ResultItems resultItems, Task task) throws SQLException {
        NewsInfo newsInfo = resultItems.get("msg");
        //System.out.println(newsInfo.getContent());
        if(newsInfo.getContent() != null){
            System.out.println("save sucessfully");
            newsInfo.save(statement);
        }
    }
}
