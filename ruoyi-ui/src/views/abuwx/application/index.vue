<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申报编号" prop="reportId">
        <el-input
          v-model="queryParams.reportId"
          placeholder="请输入申报编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申报人姓名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入申报人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号码" prop="idCard">
        <el-input
          v-model="queryParams.idCard"
          placeholder="请输入身份证号码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="家庭住址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入家庭住址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="家庭人口数" prop="familySize">
        <el-input
          v-model="queryParams.familySize"
          placeholder="请输入家庭人口数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="灾害发生时间" prop="disasterDate">
        <el-date-picker clearable
          v-model="queryParams.disasterDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择灾害发生时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="损失类型列表(JSON格式,包含house,crop,livestock,belongings,business,other)" prop="lossTypes">
        <el-input
          v-model="queryParams.lossTypes"
          placeholder="请输入损失类型列表(JSON格式,包含house,crop,livestock,belongings,business,other)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="房屋损失情况(collapse,severe,moderate,slight)" prop="houseDamage">
        <el-input
          v-model="queryParams.houseDamage"
          placeholder="请输入房屋损失情况(collapse,severe,moderate,slight)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="农作物受灾面积(亩)" prop="cropArea">
        <el-input
          v-model="queryParams.cropArea"
          placeholder="请输入农作物受灾面积(亩)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预估经济损失(元)" prop="estimatedLoss">
        <el-input
          v-model="queryParams.estimatedLoss"
          placeholder="请输入预估经济损失(元)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="救助类型列表(JSON格式,包含fund,material,housing,medical,employment,other)" prop="reliefTypes">
        <el-input
          v-model="queryParams.reliefTypes"
          placeholder="请输入救助类型列表(JSON格式,包含fund,material,housing,medical,employment,other)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请救助金额(元)" prop="requestAmount">
        <el-input
          v-model="queryParams.requestAmount"
          placeholder="请输入申请救助金额(元)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核人ID" prop="auditUserId">
        <el-input
          v-model="queryParams.auditUserId"
          placeholder="请输入审核人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核时间" prop="auditTime">
        <el-date-picker clearable
          v-model="queryParams.auditTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择审核时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input
          v-model="queryParams.auditRemark"
          placeholder="请输入审核备注"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申报人声明确认" prop="declaration">
        <el-input
          v-model="queryParams.declaration"
          placeholder="请输入申报人声明确认"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['abuwx:application:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['abuwx:application:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['abuwx:application:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['abuwx:application:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="applicationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申报ID" align="center" prop="id" />
      <el-table-column label="申报编号" align="center" prop="reportId" />
      <el-table-column label="申报人姓名" align="center" prop="name" />
      <el-table-column label="身份证号码" align="center" prop="idCard" />
      <el-table-column label="联系电话" align="center" prop="phone" />
      <el-table-column label="家庭住址" align="center" prop="address" />
      <el-table-column label="家庭人口数" align="center" prop="familySize" />
      <el-table-column label="灾害类型(flood,earthquake,drought,typhoon,landslide,fire,other)" align="center" prop="disasterType" />
      <el-table-column label="灾害发生时间" align="center" prop="disasterDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.disasterDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="灾害详情描述" align="center" prop="disasterDesc" />
      <el-table-column label="损失类型列表(JSON格式,包含house,crop,livestock,belongings,business,other)" align="center" prop="lossTypes" />
      <el-table-column label="房屋损失情况(collapse,severe,moderate,slight)" align="center" prop="houseDamage" />
      <el-table-column label="农作物受灾面积(亩)" align="center" prop="cropArea" />
      <el-table-column label="预估经济损失(元)" align="center" prop="estimatedLoss" />
      <el-table-column label="损失详细说明" align="center" prop="lossDesc" />
      <el-table-column label="救助类型列表(JSON格式,包含fund,material,housing,medical,employment,other)" align="center" prop="reliefTypes" />
      <el-table-column label="申请救助金额(元)" align="center" prop="requestAmount" />
      <el-table-column label="物资需求详情" align="center" prop="materialNeeds" />
      <el-table-column label="其他救助需求" align="center" prop="otherNeeds" />
      <el-table-column label="佐证材料文件信息(JSON格式,包含name,url,size)" align="center" prop="evidenceFiles" />
      <el-table-column label="佐证材料说明" align="center" prop="evidenceDesc" />
      <el-table-column label="申报状态:0待审核,1审核通过,2审核拒绝,3已发放" align="center" prop="status" />
      <el-table-column label="审核人ID" align="center" prop="auditUserId" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.auditTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核备注" align="center" prop="auditRemark" />
      <el-table-column label="申报人声明确认" align="center" prop="declaration" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['abuwx:application:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['abuwx:application:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改灾害救助申报对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="申报编号" prop="reportId">
          <el-input v-model="form.reportId" placeholder="请输入申报编号" />
        </el-form-item>
        <el-form-item label="申报人姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入申报人姓名" />
        </el-form-item>
        <el-form-item label="身份证号码" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号码" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="家庭住址" prop="address">
          <el-input v-model="form.address" placeholder="请输入家庭住址" />
        </el-form-item>
        <el-form-item label="家庭人口数" prop="familySize">
          <el-input v-model="form.familySize" placeholder="请输入家庭人口数" />
        </el-form-item>
        <el-form-item label="灾害发生时间" prop="disasterDate">
          <el-date-picker clearable
            v-model="form.disasterDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择灾害发生时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="灾害详情描述" prop="disasterDesc">
          <el-input v-model="form.disasterDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="损失类型列表(JSON格式,包含house,crop,livestock,belongings,business,other)" prop="lossTypes">
          <el-input v-model="form.lossTypes" placeholder="请输入损失类型列表(JSON格式,包含house,crop,livestock,belongings,business,other)" />
        </el-form-item>
        <el-form-item label="房屋损失情况(collapse,severe,moderate,slight)" prop="houseDamage">
          <el-input v-model="form.houseDamage" placeholder="请输入房屋损失情况(collapse,severe,moderate,slight)" />
        </el-form-item>
        <el-form-item label="农作物受灾面积(亩)" prop="cropArea">
          <el-input v-model="form.cropArea" placeholder="请输入农作物受灾面积(亩)" />
        </el-form-item>
        <el-form-item label="预估经济损失(元)" prop="estimatedLoss">
          <el-input v-model="form.estimatedLoss" placeholder="请输入预估经济损失(元)" />
        </el-form-item>
        <el-form-item label="损失详细说明" prop="lossDesc">
          <el-input v-model="form.lossDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="救助类型列表(JSON格式,包含fund,material,housing,medical,employment,other)" prop="reliefTypes">
          <el-input v-model="form.reliefTypes" placeholder="请输入救助类型列表(JSON格式,包含fund,material,housing,medical,employment,other)" />
        </el-form-item>
        <el-form-item label="申请救助金额(元)" prop="requestAmount">
          <el-input v-model="form.requestAmount" placeholder="请输入申请救助金额(元)" />
        </el-form-item>
        <el-form-item label="物资需求详情" prop="materialNeeds">
          <el-input v-model="form.materialNeeds" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="其他救助需求" prop="otherNeeds">
          <el-input v-model="form.otherNeeds" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="佐证材料文件信息(JSON格式,包含name,url,size)" prop="evidenceFiles">
          <el-input v-model="form.evidenceFiles" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="佐证材料说明" prop="evidenceDesc">
          <el-input v-model="form.evidenceDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="审核人ID" prop="auditUserId">
          <el-input v-model="form.auditUserId" placeholder="请输入审核人ID" />
        </el-form-item>
        <el-form-item label="审核时间" prop="auditTime">
          <el-date-picker clearable
            v-model="form.auditTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择审核时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input v-model="form.auditRemark" placeholder="请输入审核备注" />
        </el-form-item>
        <el-form-item label="申报人声明确认" prop="declaration">
          <el-input v-model="form.declaration" placeholder="请输入申报人声明确认" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listApplication, getApplication, delApplication, addApplication, updateApplication } from "@/api/abuwx/application";

export default {
  name: "Application",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 灾害救助申报表格数据
      applicationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        reportId: null,
        name: null,
        idCard: null,
        phone: null,
        address: null,
        familySize: null,
        disasterType: null,
        disasterDate: null,
        disasterDesc: null,
        lossTypes: null,
        houseDamage: null,
        cropArea: null,
        estimatedLoss: null,
        lossDesc: null,
        reliefTypes: null,
        requestAmount: null,
        materialNeeds: null,
        otherNeeds: null,
        evidenceFiles: null,
        evidenceDesc: null,
        status: null,
        auditUserId: null,
        auditTime: null,
        auditRemark: null,
        declaration: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        reportId: [
          { required: true, message: "申报编号不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "申报人姓名不能为空", trigger: "blur" }
        ],
        idCard: [
          { required: true, message: "身份证号码不能为空", trigger: "blur" }
        ],
        phone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ],
        address: [
          { required: true, message: "家庭住址不能为空", trigger: "blur" }
        ],
        familySize: [
          { required: true, message: "家庭人口数不能为空", trigger: "blur" }
        ],
        disasterType: [
          { required: true, message: "灾害类型(flood,earthquake,drought,typhoon,landslide,fire,other)不能为空", trigger: "change" }
        ],
        disasterDate: [
          { required: true, message: "灾害发生时间不能为空", trigger: "blur" }
        ],
        disasterDesc: [
          { required: true, message: "灾害详情描述不能为空", trigger: "blur" }
        ],
        estimatedLoss: [
          { required: true, message: "预估经济损失(元)不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "申报状态:0待审核,1审核通过,2审核拒绝,3已发放不能为空", trigger: "blur" }
        ],
        declaration: [
          { required: true, message: "申报人声明确认不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "更新时间不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询灾害救助申报列表 */
    getList() {
      this.loading = true;
      listApplication(this.queryParams).then(response => {
        this.applicationList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        reportId: null,
        name: null,
        idCard: null,
        phone: null,
        address: null,
        familySize: null,
        disasterType: null,
        disasterDate: null,
        disasterDesc: null,
        lossTypes: null,
        houseDamage: null,
        cropArea: null,
        estimatedLoss: null,
        lossDesc: null,
        reliefTypes: null,
        requestAmount: null,
        materialNeeds: null,
        otherNeeds: null,
        evidenceFiles: null,
        evidenceDesc: null,
        status: 0,
        auditUserId: null,
        auditTime: null,
        auditRemark: null,
        declaration: null,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加灾害救助申报";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getApplication(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改灾害救助申报";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateApplication(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addApplication(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除灾害救助申报编号为"' + ids + '"的数据项？').then(function() {
        return delApplication(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('abuwx/application/export', {
        ...this.queryParams
      }, `application_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
