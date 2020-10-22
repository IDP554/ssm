package controller;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pojo.Role;
import pojo.User;
import service.role.RoleService;
import service.role.RoleServiceImpl;
import service.user.UserServiceImpl;
import tools.Constants;
import tools.PageSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class LoginController extends BasicController {
    @Resource
    private UserServiceImpl userService;
    @Resource
    private RoleServiceImpl roleService;

    //跳转到 登录页面
    @RequestMapping("/login.html")
    public String login(){
        return "login";
    }

    //登录
    @RequestMapping(value = "/login.html",method = RequestMethod.POST)
    public String login(String userCode, String userPassword, HttpSession session){
        User user = userService.login(userCode,userPassword);
        if (user != null){
            session.setAttribute(Constants.USER_SESSION,user);
            return "redirect:/frame.html";
        }else {
            session.setAttribute("error","用户名或密码错误！");
            //int i = 5/0;
            return "login";
        }
    }

    //权限管理
    @RequestMapping("/frame.html")
    public String powerManager(HttpSession session){
        User user = (User) session.getAttribute(Constants.USER_SESSION);
        if (user == null){
            return "login";
        }
        return "frame";
    }

    //用户管理
    @RequestMapping("/userManager.html")
    public String userManager(Model model, String queryname,@RequestParam(value = "queryUserRole",defaultValue = "0") Integer queryUserRole,
                              @RequestParam(value = "pageIndex",defaultValue = "1") Integer pageIndex){
        //查询用户列表
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;

        /**
         * http://localhost:8090/SMBMS/userlist.do
         * ----queryUserName --NULL
         * http://localhost:8090/SMBMS/userlist.do?queryname=
         * --queryUserName ---""
         */
        System.out.println("=======Controller层=======");
        System.out.print("条件查询的姓名-->"+queryname+"\t\t-------");
        System.out.print("条件查询的角色ID-->"+queryUserRole + "\t\t---------");
        System.out.println("条件查询的页码--> " + pageIndex);

        //总数量（表）
        int totalCount	= userService.getUserCount(queryname,queryUserRole);
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(pageIndex);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if(pageIndex < 1){
            pageIndex = 1;
        }else if(pageIndex > totalPageCount){
            pageIndex = totalPageCount;
        }


        userList = userService.getUserList(queryname,queryUserRole,pageIndex, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        System.out.println("=====================》\n"+roleList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryname);
        model.addAttribute("queryUserRole", queryUserRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", pageIndex);
        return "userlist";
    }

    //用户注销
    @RequestMapping("/exitUser.html")
    public String exitUser(HttpSession session){
        session.removeAttribute(Constants.USER_SESSION);
        return "redirect:/login.html";
    }

    /**
     * 局部异常处理
     * 只能拦截到本类中的 Request 请求
     */
    @ExceptionHandler(value = {RuntimeException.class})
    public String ex(RuntimeException e, HttpServletRequest request){
        request.setAttribute("e",e.getMessage());
        return "error";
    }

    /**
     * 跳转添加页面
     */
    @RequestMapping("/userAdd.html")
    public String addUser(@ModelAttribute User user){
        return "useradd";
    }

    /**
     * 添加用户
     */
    @RequestMapping(value = "/userAdd.html",method = RequestMethod.POST)
    public String addUser(User user,HttpSession session){
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        if(userService.add(user)){
            return "redirect:/userManager.html";
        }else{
            return "useradd";
        }
    }

    /**
     * 使用 JSR 303 验证
     */
    @RequestMapping(value = "/jsrAdd.html",method = RequestMethod.POST)
    public String addUser(@Valid User user, BindingResult bindingResult, HttpSession session){
        //根据实体类的注解验证，有错误的话直接return
        if (bindingResult.hasErrors()){
            return "user/useradd";
        }
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        if(userService.add(user)){
            return "redirect:/userManager.html";
        }else{
            return "user/useradd";
        }
    }

    /**
     * 跳转到修改页面
     */
    @RequestMapping("/modify.html")
    public String modify(String uid,Model model){
        User user = userService.getUserById(uid);
        System.out.println(user);
        model.addAttribute("user",user);
        return "usermodify";
    }

    /**
     * 修改用户信息
     */
    @RequestMapping(value = "/modify.html",method = RequestMethod.POST)
    public String modify(User user,HttpSession session){
        user.setModifyDate(new Date());
        user.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        if(userService.modify(user)){
            return "redirect:/userManager.html";
        }else{
            return "usermodify";
        }
    }

    /**
     * 显示用户信息 使用REST风格
     */
    @RequestMapping(value = "/view.html/{id}",method = RequestMethod.GET)
    public String view(@PathVariable String id,Model model){
        //调用后台方法得到user对象
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "userview";
    }

    /**
     * 增加用户信息 含文件上传  必须为 post 提交
     */
    @RequestMapping(value = "upload.html",method = RequestMethod.POST)
    //@RequestParam(value = "upload",required = false) MultipartFile file
    //file 文件上传设置required为false表示不是必须的，可省
    public String uploadFile(User user, HttpServletRequest request,
                             @RequestParam(value = "upload",required = false) MultipartFile imgFile,
                             @RequestParam(value = "tou",required = false) MultipartFile headImg
    ){
        String imgPath = fileUpload(imgFile,request);
        String headImgPath = fileUpload(headImg,request);
        if (imgPath == null || headImgPath == null){
            return "user/useradd";
        }
        user.setPicPath(imgPath);
        user.setHeadImg(headImgPath);
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        if(userService.add(user)){
            return "redirect:/userManager.html";
        }else{
            return "user/useradd";
        }
    }

    /**
     * 文件上传
     */
    private String fileUpload(MultipartFile imgFile,HttpServletRequest request){
        String path = "";  //图片路径
        if (!imgFile.isEmpty()){  //如果文件上传为空
            // File.separator 自适应路径分割符，适应不同的系统
            String filePath = request.getServletContext().getRealPath("statics")+ File.separator+"uploadFile";  //文件上传的路径
            //判断文件大小
            if (imgFile.getSize() > 5120000) {  //单位 byte ，这里设定的大小是 5MB
                request.setAttribute("error","文件太大了，文件不得大于5MB！");
                return null;
            }
            List<String> suffexs = Arrays.asList(new String[]{".jpg",".png",".gif",".jpeg"});
            String oldFileName = imgFile.getOriginalFilename();  //获取文件名
            String suffex = oldFileName.substring(oldFileName.lastIndexOf("."),oldFileName.length()).toLowerCase();
            //System.out.println(suffex);
            if (!suffexs.contains(suffex)){
                request.setAttribute("error","上传失败，不支持此文件类型！");
                return null;
            }
            //文件重命名
            //规则：当前时间的毫秒数 + 1000000的随机数
            String newFileName = System.currentTimeMillis()+""+new Random().nextInt(1000000)+suffex;
            File file = new File(filePath,newFileName);  //上传的路径,文件的名字
            //判断文件是否存在
            if (!file.exists()){
                //不存在则创建
                file.mkdirs();
            }
            try {
                //文件上传
                imgFile.transferTo(file);
            } catch (IOException e) {
                e.printStackTrace();
                request.setAttribute("error","上传失败！");
                return null;
            }
            //上传完毕
            path = filePath+File.separator+newFileName;
            path = path.substring(path.lastIndexOf(File.separator)+1,path.length());
        }
        return path;
    }

    /**
     * 账号重复验证
     */
    @RequestMapping(value = "/isRepeat",produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public Object isRepeat(String userCode){
        User user = userService.selectUserCodeExist(userCode);
        Map<String,Object> map = new HashMap<String, Object>();
        if (user != null){
            map.put("userCode","exist");
        }else{
            map.put("userCode","noexist");
        }
        return JSONArray.toJSONString(map);
    }

    /**
     * 删除账号
     */
    @RequestMapping(value = "/delUser",produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public Object delUser(Integer uid){
        Map<String,Object> map = new HashMap<String, Object>();
        if (userService.getUserById(uid.toString()) == null){
            map.put("delResult","notexist");
        }else if (userService.deleteUserById(uid)){
            map.put("delResult","true");
        }else{
            map.put("delResult","false");
        }
        return JSONArray.toJSONString(map);
    }

    /**
     * 修改密码
     */
    @RequestMapping("/updatePwd.html")
    public String updatePwd(){
        return "pwdmodify";
    }

    /**
     * 多视图配置测试
     */
    @RequestMapping(value = "/view",method = RequestMethod.GET/*,produces = "application/json;charset=utf-8;"*/)
    @ResponseBody
    public User viewByAjax(String id){
        User user = userService.getUserById(id);
        if (user == null){
            System.out.println("================没拿到对象");
        }
        return user;
    }
}
