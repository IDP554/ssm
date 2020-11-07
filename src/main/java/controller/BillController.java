package controller;

import com.mysql.jdbc.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.Bill;
import pojo.Provider;
import service.bill.BillService;
import service.bill.BillServiceImpl;
import service.provider.ProviderService;
import service.provider.ProviderServiceImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BillController {

    @Resource
    ProviderService providerService;
    @Resource
    BillService billService;

    //跳转到订单管理页
    @RequestMapping("/billlist.html")
    public String bill(Model model,String queryProductName,String queryProviderId,String queryIsPayment){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("","");
        model.addAttribute("providerList", providerList);

        if(StringUtils.isNullOrEmpty(queryProductName)){
            queryProductName = "";
        }

        List<Bill> billList = new ArrayList<Bill>();
        Bill bill = new Bill();
        if(StringUtils.isNullOrEmpty(queryIsPayment)){
            bill.setIsPayment(0);
        }else{
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if(StringUtils.isNullOrEmpty(queryProviderId)){
            bill.setProviderId(0);
        }else{
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
        billList = billService.getBillList(bill);
        model.addAttribute("billList", billList);
        model.addAttribute("queryProductName", queryProductName);
        model.addAttribute("queryProviderId", queryProviderId);
        model.addAttribute("queryIsPayment", queryIsPayment);
        return "billlist";
    }
}
