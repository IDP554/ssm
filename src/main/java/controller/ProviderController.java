package controller;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pojo.Provider;
import pojo.User;
import service.provider.ProviderServiceImpl;
import tools.Constants;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class ProviderController {

    @Resource
    ProviderServiceImpl providerService;

    //跳转到供应商
    @RequestMapping("/provider.html")
    public String provider(String queryProName, String queryProCode, Model model){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        model.addAttribute("providerList", providerList);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("queryProCode", queryProCode);
        return "providerlist";
    }

    //跳转到添加供应商
    @RequestMapping("/addProvider.html")
    public String add(){
        return "provideradd";
    }

    //添加供应商
    @RequestMapping(value = "/addProvider.html",method = RequestMethod.POST)
    public String add(Provider provider, HttpSession session){
        provider.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());
        boolean flag = false;
        flag = providerService.add(provider);
        if(flag){
            return "redirect:/provider.html";
        }else{
            return "provideradd";
        }
    }

    //删除供应商
    @RequestMapping("/delProvider")
    @ResponseBody
    public Object delete(String proid){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(proid)){
            int flag = providerService.deleteProviderById(proid);
            if(flag == 0){//删除成功
                resultMap.put("delResult", "true");
            }else if(flag == -1){//删除失败
                resultMap.put("delResult", "false");
            }else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        return JSONArray.toJSONString(resultMap);
    }

    //查看供应商
    @RequestMapping("/viewProvider.html")
    public String view(String proid,Model model){
        Provider provider = providerService.getProviderById(proid);
        model.addAttribute("provider", provider);
        return "providerview";
    }

    //跳转到修改供应商
    @RequestMapping("/providermodify.html")
    public String update(String proid,Model model){
        Provider provider = providerService.getProviderById(proid);
        model.addAttribute("provider", provider);
        return "providermodify";
    }

    //修改供应商
    @RequestMapping(value = "/providermodify.html",method = RequestMethod.POST)
    public String update(Provider provider,HttpSession session){
        provider.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        boolean flag = providerService.modify(provider);
        System.out.println("=================>" + flag);
        if(flag){
            return "redirect:/provider.html";
        }else{
            return "providermodify";
        }
    }
}
