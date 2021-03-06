package net.logcd.ws.svc.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author 水木年华
 * @title: HelloController
 * @projectName springboot-svc
 * @description: TODO
 * @date 2019/8/411:26
 */
@Controller
public class RestFulController {
    @RequestMapping("/")
    @ResponseBody
    public  String  returnIndex(){
        StringBuffer list = new StringBuffer();
        list.append("<br>");
        list.append("<h1>同步数据服务测试用例</h1>");
        list.append("<h2><a href='/xfire/services/XfireService?wsdl'>Xfire-WebService</a></h2>");
        list.append("<br>");
        list.append("<h2><a href='/cxf/services/CxfWebService?wsdl'>CXF-WebService</a></h2>");
        list.append("<br>");
        list.append("<h2><a href='/axis/services/axisWebService?wsdl'>Axis-WebService</a></h2>");
        list.append("<br>");
        list.append("<h2><a href='/RESTJSON'>RESTFUL-JSON</a></h2>");
        list.append("<h2><a href='/RESTXML'>RESTFUL-XML</a></h2>");
        list.append("<h2><a href='/RESTJSONXML'>RESTFUL-JSONXML</a></h2>");
        return list.toString();
    }
    @RequestMapping("RESTJSON")
    @ResponseBody
    public String JSON(@RequestParam(name = "appSerialNumber") String appSerialNumber, @RequestParam(name = "utsNodeInfo") String utsNodeInfo) {
        JSONObject result = new JSONObject();
        builJson(utsNodeInfo, result);
        return result.toString();
    }
    private void builJson(@WebParam(name = "utsNodeInfo") String utsNodeInfo, JSONObject result) {
        JSONObject jsonObject = JSONObject.parseObject(utsNodeInfo);
        Object datas = jsonObject.get("datas");
        if (!datas.equals("[]")) {
            JSONArray JSONArray = (JSONArray) jsonObject.get("datas");
            JSONObject message = new JSONObject();
            JSONArray array = new JSONArray();
            for (Object o : JSONArray) {
                JSONObject jsonObject1 = (JSONObject) o;
                JSONObject obj = new JSONObject();
                obj.put("id", jsonObject1.get("id"));
                obj.put("code", "0");
                obj.put("message", jsonObject1.get("success"));
                array.add(obj);
            }
            message.put("datas", array);
            result.put("message", message);
            result.put("status", "0x0000");
            System.out.println(result.toJSONString());
        }
    }

    @RequestMapping("RESTXML")
    @ResponseBody
    public String XML(@RequestParam(name = "appSerialNumber") String appSerialNumber, @RequestParam(name = "utsNodeInfo") String utsNodeInfo) throws IOException, DocumentException {
        ByteArrayOutputStream stream = getByteArrayOutputStream(utsNodeInfo);
        String restXml = new String(stream.toByteArray(), "UTF-8");
        return restXml;
    }
    @RequestMapping("RESTJSONXML")
    @ResponseBody
    public String JSONXML(@RequestParam(name = "appSerialNumber") String appSerialNumber, @RequestParam(name = "utsNodeInfo") String utsNodeInfo) throws IOException, DocumentException {
        ByteArrayOutputStream stream = getByteArrayOutputStream(utsNodeInfo);
        String xml = new String(stream.toByteArray(), "UTF-8");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status","0x0000");
        jsonObject.put("message",xml);
        return jsonObject.toString();
    }

    private ByteArrayOutputStream getByteArrayOutputStream(@WebParam(name = "utsNodeInfo") String utsNodeInfo) throws DocumentException, IOException {
        Document document = DocumentHelper.createDocument();
        Element roots = document.addElement("datas");
        document.setRootElement(roots);
        Document document1 = DocumentHelper.parseText(utsNodeInfo);
        List<Element> elements = document1.getRootElement().elements("data");
        for (Element e : elements) {
            Element data = DocumentHelper.createElement("data");
            Element id = e.element("id");
            if (id == null || !id.isTextOnly() || id.getTextTrim().length() == 0) {
                continue;
            }else{
                String textTrim = id.getTextTrim();
                data.addElement("id").setText(textTrim);
            }
            data.addElement("code").setText("0");
            data.addElement("message").setText("success");
            roots.add(data);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputFormat format = new OutputFormat("\t", true);// 设置缩进为4个空格，并且另起一行为true
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(stream, format);
        writer.write(document);
        return stream;
    }

}
