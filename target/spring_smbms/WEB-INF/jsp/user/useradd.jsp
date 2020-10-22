<%--
  Created by IntelliJ IDEA.
  User: HP
  Date: 2020/10/15
  Time: 15:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="fm" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>添加用户</title>
</head>
<body>
    <fm:form modelAttribute="user" method="post" action="/upload.html" enctype="multipart/form-data">
        <p>用户编码：<fm:input path="userCode" /><fm:errors path="userCode" /></p>
        <p>用户名称：<fm:input path="userName" /></p>
        <p>用户密码：<fm:password path="userPassword" /><fm:errors path="userPassword" /></p>
<%--        <p>确认密码：<fm:password path="ruserPassword" /></p>--%>
        <p>用户性别：<fm:radiobutton path="gender" value="1" />男<fm:radiobutton path="gender" value="2" />女</p>
        <p>出生日期：<fm:input path="birthday" readonly="readonly" onclick="WdatePicker();"/><fm:errors path="birthday" /></p>
        <p>用户电话：<fm:input path="phone" /></p>
        <p>家庭住址：<fm:input path="address" /></p>
        <p>用户角色：<fm:select path="userRole">
            <fm:option value="1">管理员</fm:option>
            <fm:option value="2">经理</fm:option>
            <fm:option value="3">普通员工</fm:option>
        </fm:select></p>
        <!-- 文件上传 的 name 值不能和实体类字段名相同，绑定错误400 -->
        <p>上传证件照：<input type="file" name="upload" accept="image/*"></p>
        <p>上传头像：<input type="file" name="tou" accept="image/*"></p>
        <p><fm:button>提交</fm:button><span>${error}</span></p>
    </fm:form>
</body>
</html>
<script type="text/javascript" src="statics/js/useradd.js"></script>
