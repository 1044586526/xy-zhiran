<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="申请ID" prop="applicationId">
        <el-input
          v-model="queryParams.applicationId"
          placeholder="请输入申请ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评议人ID" prop="evaluatorId">
        <el-input
          v-model="queryParams.evaluatorId"
          placeholder="请输入评议人ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评议人姓名" prop="evaluatorName">
        <el-input
          v-model="queryParams.evaluatorName"
          placeholder="请输入评议人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="真实性评分(1-5分)" prop="truthScore">
        <el-input
          v-model="queryParams.truthScore"
          placeholder="请输入真实性评分(1-5分)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="严重程度评分(1-5分)" prop="severityScore">
        <el-input
          v-model="queryParams.severityScore"
          placeholder="请输入严重程度评分(1-5分)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="救助必要性评分(1-5分)" prop="necessityScore">
        <el-input
          v-model="queryParams.necessityScore"
          placeholder="请输入救助必要性评分(1-5分)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总评分" prop="totalScore">
        <el-input
          v-model="queryParams.totalScore"
          placeholder="请输入总评分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评议时间" prop="evaluationTime">
        <el-date-picker clearable
          v-model="queryParams.evaluationTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择评议时间">
        </el-date-picker>
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
          v-hasPermi="['abuwx:evaluation:add']"
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
          v-hasPermi="['abuwx:evaluation:edit']"
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
          v-hasPermi="['abuwx:evaluation:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['abuwx:evaluation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="evaluationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评议记录ID" align="center" prop="id" />
      <el-table-column label="申请ID" align="center" prop="applicationId" />
      <el-table-column label="评议人ID" align="center" prop="evaluatorId" />
      <el-table-column label="评议人姓名" align="center" prop="evaluatorName" />
      <el-table-column label="真实性评分(1-5分)" align="center" prop="truthScore" />
      <el-table-column label="严重程度评分(1-5分)" align="center" prop="severityScore" />
      <el-table-column label="救助必要性评分(1-5分)" align="center" prop="necessityScore" />
      <el-table-column label="总评分" align="center" prop="totalScore" />
      <el-table-column label="评议意见" align="center" prop="opinion" />
      <el-table-column label="评议时间" align="center" prop="evaluationTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.evaluationTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态：0-无效，1-有效" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['abuwx:evaluation:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['abuwx:evaluation:remove']"
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

    <!-- 添加或修改社保民主评议记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="申请ID" prop="applicationId">
          <el-input v-model="form.applicationId" placeholder="请输入申请ID" />
        </el-form-item>
        <el-form-item label="评议人ID" prop="evaluatorId">
          <el-input v-model="form.evaluatorId" placeholder="请输入评议人ID" />
        </el-form-item>
        <el-form-item label="评议人姓名" prop="evaluatorName">
          <el-input v-model="form.evaluatorName" placeholder="请输入评议人姓名" />
        </el-form-item>
        <el-form-item label="真实性评分(1-5分)" prop="truthScore">
          <el-input v-model="form.truthScore" placeholder="请输入真实性评分(1-5分)" />
        </el-form-item>
        <el-form-item label="严重程度评分(1-5分)" prop="severityScore">
          <el-input v-model="form.severityScore" placeholder="请输入严重程度评分(1-5分)" />
        </el-form-item>
        <el-form-item label="救助必要性评分(1-5分)" prop="necessityScore">
          <el-input v-model="form.necessityScore" placeholder="请输入救助必要性评分(1-5分)" />
        </el-form-item>
        <el-form-item label="总评分" prop="totalScore">
          <el-input v-model="form.totalScore" placeholder="请输入总评分" />
        </el-form-item>
        <el-form-item label="评议意见" prop="opinion">
          <el-input v-model="form.opinion" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="评议时间" prop="evaluationTime">
          <el-date-picker clearable
            v-model="form.evaluationTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择评议时间">
          </el-date-picker>
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
import { listEvaluation, getEvaluation, delEvaluation, addEvaluation, updateEvaluation } from "@/api/abuwx/evaluation";

export default {
  name: "Evaluation",
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
      // 社保民主评议记录表格数据
      evaluationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applicationId: null,
        evaluatorId: null,
        evaluatorName: null,
        truthScore: null,
        severityScore: null,
        necessityScore: null,
        totalScore: null,
        opinion: null,
        evaluationTime: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        applicationId: [
          { required: true, message: "申请ID不能为空", trigger: "blur" }
        ],
        evaluatorId: [
          { required: true, message: "评议人ID不能为空", trigger: "blur" }
        ],
        truthScore: [
          { required: true, message: "真实性评分(1-5分)不能为空", trigger: "blur" }
        ],
        severityScore: [
          { required: true, message: "严重程度评分(1-5分)不能为空", trigger: "blur" }
        ],
        necessityScore: [
          { required: true, message: "救助必要性评分(1-5分)不能为空", trigger: "blur" }
        ],
        totalScore: [
          { required: true, message: "总评分不能为空", trigger: "blur" }
        ],
        evaluationTime: [
          { required: true, message: "评议时间不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态：0-无效，1-有效不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询社保民主评议记录列表 */
    getList() {
      this.loading = true;
      listEvaluation(this.queryParams).then(response => {
        this.evaluationList = response.rows;
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
        applicationId: null,
        evaluatorId: null,
        evaluatorName: null,
        truthScore: null,
        severityScore: null,
        necessityScore: null,
        totalScore: null,
        opinion: null,
        evaluationTime: null,
        status: 0,
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
      this.title = "添加社保民主评议记录";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getEvaluation(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改社保民主评议记录";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateEvaluation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addEvaluation(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除社保民主评议记录编号为"' + ids + '"的数据项？').then(function() {
        return delEvaluation(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('abuwx/evaluation/export', {
        ...this.queryParams
      }, `evaluation_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
