<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br/>
<#--遍历数据模型中的list的学生信息(stus)-->
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
    </tr>
    <#if stus??>
    <#list stus as stu>
        <tr>
            <td>${stu_index + 1}</td>
            <td <#if stu.name =='小明'>style="background: red"</#if>>${stu.name}</td>
            <td>${stu.age}</td>
            <td <#if (stu.mondy > 300)> style="background: red"</#if> >${stu.mondy}</td>
        </tr>
    </#list>
    </#if>
</table>
<br/>
<#--遍历数据模型中的map数据
    1.在中括号中填写map的key
    2.在map后直接加 .key
    3.遍历map中的stuMap?keys 就是key列表(是一个list)
-->
姓名：${(stuMap['stu1'].name)!''}<br/>
年龄：${(stuMap['stu1'].age)!''}<br/>
姓名：${(stuMap.stu1.name)!''}<br/>
年龄：${(stuMap.stu1.age)!''}<br/>
遍历map中的stuMap?keys 就是key列表(是一个list)
<#list stuMap?keys as k>
    姓名：${stuMap[k].name}<br/>
    年龄：${stuMap[k].age}<br/>
</#list>
</body>
</html>