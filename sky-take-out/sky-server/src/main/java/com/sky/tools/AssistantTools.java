package com.sky.tools;

import com.google.type.DateTime;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.entity.Employee;
import com.sky.mapper.EmployeeMapper;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AssistantTools {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private ReportService reportService;

    @Tool(name="获取所有员工信息", value = "获取系统已经注册的所有员工信息")
    public String getEmployee(){
        List<Employee> employees = employeeMapper.getAll();

        StringBuilder sb = new StringBuilder();
        sb.append("以下是系统中的员工信息概览：\n\n");
        // 修正1：调整表头，使其与下面的数据列完全对应
        sb.append("| ID | 姓名 | 用户名 | 手机号 | 状态 |\n");
        sb.append("|----|------|--------|--------|------|\n");

        for (Employee e : employees) {
            // 修正2：在后端直接将状态码转换为文本
            String statusText = "未知";
            if (e.getStatus() != null) {
                statusText = e.getStatus() == 1 ? "启用" : "禁用";
            }

            // 修正3：确保append的顺序与新表头一致
            sb.append("| ")
                    .append(e.getId()).append(" | ")
                    .append(e.getName()).append(" | ")
                    .append(e.getUsername()).append(" | ") // 假设您的Employee类中有getUsername()
                    .append(e.getPhone()).append(" | ")
                    .append(statusText).append(" |\n");
        }

        sb.append("\n**提示**：如果需要查看某个员工的详细信息, 请提供员工的 **ID**。");

        return sb.toString();
    }

    private String printFunc(Employee employee){
        if (employee == null) {
            return "员工不存在";
        }
        String statusText = "未知";
        if (employee.getStatus() != null) {
            statusText = employee.getStatus() == 1 ? "启用" : "禁用";
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createTime = employee.getCreateTime().format(dtf);
        String updateTime = employee.getUpdateTime().format(dtf);

        StringBuilder sb = new StringBuilder();
        sb.append("以下是id为4的员工详细信息概览：\n\n");
        // 修正1：调整表头，使其与下面的数据列完全对应
        sb.append("| ID | 姓名 | 用户名 | 手机号 | 状态 | 创建时间 | 更新时间 |\n");
        sb.append("|----|------|--------|--------|------|------|------|\n");
        sb.append("| ").append(employee.getId()).append("| ")
                .append(employee.getName()).append("| ")
                .append(employee.getUsername()).append("| ")
                .append(employee.getPhone()).append("| ")
                .append(statusText).append("| ")
                .append(createTime).append("| ")
                .append(updateTime).append("| \n");
        return sb.toString();
    }

    @Tool(name="根据id获取员工信息或者查询当前登录管理员信息", value = "根据员工id获取员工信息或者查询当前登录管理员信息，并展示除了密码以外所有的信息，包括id，姓名，用户名，电话号，sex，id_number,status，时间相关信息也需要加上，比如创建时间" +
            "{create_time}，可以是说该用户创建于XXXX时间，由谁{create_user}创建，上一次更新在{update_time}时间，被谁{update_user}更新；查询当前登录管理员信息时，id从用户发送的信息获取")
    public String getEmployeeById(Long id){
        Employee employee = employeeMapper.getById(id);
        String res = printFunc(employee);
        return res;
    }

//    @Tool(name = "查询当前登录管理员或者用户信息", value = "查询当前登录管理员或者用户的信息，并展示除了密码以外所有的信息，包括id，姓名，用户名，电话号，sex，id_number,status，时间相关信息也需要加上，比如创建时间")
//    public String getCurrentEmployee(){
//        Long adminId = BaseContext.getCurrentId();
//        Employee employee = employeeMapper.getById(adminId);
//        String res = printFunc(employee);
//        return res;
//    }

    @Tool(
            name = "查询时间段订单统计",
            value = "根据开始时间和结束时间查询订单总数和营业额。例如：从 2024-07-01 到 2024-07-03 共有 X 单，总收入 Y 元。如果用户只说今天或者昨天或者具体到只有一天的，startDate和endDate一样"
    )
    public String getOrderSummaryByDate(
            @P(value = "开始时间（格式yyyy-MM-dd）", required = true) LocalDate startDate,
            @P(value = "结束时间（格式yyyy-MM-dd）", required = true) LocalDate endDate) {


        TurnoverReportVO turnoverStatistics = reportService.getTurnoverStatistics(startDate, endDate);
        String[] datalist = turnoverStatistics.getDateList().split(",");
        int count = datalist.length;
        String[] turnoverlist = turnoverStatistics.getTurnoverList().split(",");

        float[] floats = new float[turnoverlist.length];
        float total = 0f;

        for (int i = 0; i < turnoverlist.length; i++) {
            floats[i] = Float.parseFloat(turnoverlist[i]);
            total += floats[i];
        }

        return String.format("从 %s 到 %s，共接到 %d 单，总营业额为 %.2f 元。", startDate, endDate, count, total);
    }

    @Tool(name = "添加员工信息", value = "根据用户输入的员工账号，员工姓名，员工手机号，员工性别，员工身份证号添加员工到数据库中")
    public String addEmployee(
            @P(value = "员工账号", required = true) String name,
            @P(value = "员工姓名", required = true) String username,
            @P(value = "员工手机号", required = true) String phone,
            @P(value = "员工性别", required = true) String sex,
            @P(value = "员工身份证号", required = true) String idNumber) {

        if(sex == "男"){
            sex = "1";
        }else {
            sex = "0";
        }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setUsername(username);
        employee.setPhone(phone);
        employee.setSex(sex);
        employee.setIdNumber(idNumber);

        // 设置账号的状态，默认正常状态，1表示正常，0表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        // 设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        employeeMapper.insert(employee);

        return "成功添加用户：" + employee.getName();
    }
}
