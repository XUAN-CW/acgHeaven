import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参考：
 * https://www.cnblogs.com/sam-uncle/p/10908567.html
 * 遇到的问题:
 *  * 记得添加更改 pom.xml ,引入依赖
 *  * Error:java: 不再支持源选项 5。请使用 6 或更高版本：
 *    https://blog.csdn.net/qq_40960093/article/details/103230762?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.nonecase
 */

public class SimplyCrawlTheHTML {

    /**
     *
     * @param url
     * @return
     */
    public static String getHTML(String url){
        System.out.println("当前 url: "+url);
        String html=null;
        //1.生成httpclient，相当于该打开一个浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            //2.创建get请求，相当于在浏览器地址栏输入网址
            HttpGet request = new HttpGet(url);
            //3.执行get请求，相当于在输入地址栏后敲回车键
            response = httpClient.execute(request);
            response.addHeader("currentURL",url);
            //4.判断响应状态为200，进行处理
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //5.获取响应内容
                HttpEntity httpEntity = response.getEntity();
                html = EntityUtils.toString(httpEntity, "utf-8");
//                System.out.println(html);
            } else {
                //如果返回状态不是200，比如404（页面不存在）等，根据情况做处理，这里略
                System.out.println("返回状态不是200");
//                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return html;
    }

    /**
     * 判断给定的百度云链接是否可用
     * @param url
     * @return
     */
    public static boolean isAvailableOnBaiduCloudLink(String url){
        boolean available=false;
        String html=getHTML(url);
        if(html!=null && html.contains("请输入提取码")){
            available=true;
        }
        return available;
    }

    public static void main(String[] args) {
        //创建根目录////////////////////////////////////////////
        String rootOfHTML="./allHTML";//HTML 根目录
        File allHTML=new File(rootOfHTML);
        if (!allHTML.exists()){
            allHTML.mkdir();
        }
        for (int i=3000,j=0;i<=10000;i++){
            //创建子目录、查看当前 HTML 文件是否已下载////////////////////////////
            String url="http://acgheaven.cc/archives/"+i;
            String currentChildDirectoryPath=rootOfHTML+"/"+i/100;//每个子目录下最多有 100 个 HTML 文件
            String currentFileName= "["+i+"]" +".html";
            String currentFilePath=currentChildDirectoryPath+"/"+currentFileName;
            File currentChildDirectory = new File(currentChildDirectoryPath);
            File currentFile=new File(currentFilePath);
            if (!currentChildDirectory.exists()){
                currentChildDirectory.mkdir();
            }
            if (currentFile.exists()){
                continue;
            }
            //爬取页面///////////////////////////////////////////////////////
            String html=getHTML(url);
            if (null!=html){
                //用文件名区分文件,百度云链接可用的加上 [百度云可用]///////////////////////////
                if (true){
                    //去掉 .html 后缀
                    currentFileName=currentFileName.substring(0,currentFileName.length()-5);
                    //匹配所有网址
                    String regEx = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(html);
                    String lastURL="";
                    while (m.find()) {
                        String baiduCloudLink=m.group();
                        if (null!=baiduCloudLink){
                            //找出百度云链接
                            if (baiduCloudLink.contains("pan.baidu.com")){
                                if (lastURL.equals(baiduCloudLink)){
                                    boolean available=isAvailableOnBaiduCloudLink(baiduCloudLink);
                                    if (available){
                                        currentFileName+="[百度云可用]";
                                    }
                                }
                            }
                            lastURL=baiduCloudLink;
                        }
                    }
                    //把后缀加回来
                    currentFileName+=".html";
                }
                //保存文件////////////////////////////////////////////////////////////////
                SaveAndRead saveAndRead=new SaveAndRead();
                String currentURL="currentURL";
                currentFilePath=currentChildDirectoryPath+"/"+currentFileName;
                saveAndRead.save(currentFilePath, html);
//                System.out.println("保存 "+url+" 到 "+currentFilePath);
            }
        }
    }



}