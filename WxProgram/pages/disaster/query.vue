<template>
  <view class="query-container">
    <!-- 页面标题 -->
    <view class="page-header">
      <view class="header-title">
        <text class="title-main">申报进度查询</text>
        <text class="title-sub">Application Status Query</text>
      </view>
    </view>
    
    <!-- 查询表单区域 -->
    <view class="form-container">
      <view class="form-section">
        <view class="section-title">
          <text class="section-icon cuIcon-search"></text>
          <text class="section-text">查询信息</text>
        </view>
        
        <!-- 查询方式选择 -->
        <view class="query-tabs">
          <view class="tab" :class="{'tab-active': queryType === 'idCard'}" @tap="queryType = 'idCard'">
            <text class="tab-icon cuIcon-card"></text>
            <text class="tab-text">身份证查询</text>
          </view>
          <view class="tab" :class="{'tab-active': queryType === 'phone'}" @tap="queryType = 'phone'">
            <text class="tab-icon cuIcon-phone"></text>
            <text class="tab-text">手机号查询</text>
          </view>
          <view class="tab" :class="{'tab-active': queryType === 'reportId'}" @tap="queryType = 'reportId'">
            <text class="tab-icon cuIcon-form"></text>
            <text class="tab-text">申报编号查询</text>
          </view>
        </view>
        
        <!-- 输入区域 -->
        <view class="form-item">
          <text class="label required">{{queryTypeLabel}}</text>
          <input class="input" :type="inputType" v-model="queryValue" :placeholder="placeholder" :maxlength="maxLength" />
        </view>
        
        <!-- 提交按钮 -->
        <button class="btn-query" @tap="queryApplication">查询申报进度</button>
      </view>
      
      <!-- 查询结果区域 -->
      <view class="result-section" v-if="hasResult">
        <view class="section-title">
          <text class="section-icon cuIcon-info"></text>
          <text class="section-text">查询结果</text>
        </view>
        
        <view v-if="applications.length === 0" class="no-result">
          <text class="cuIcon-searchlist no-result-icon"></text>
          <text class="no-result-text">未查询到相关申报记录</text>
        </view>
        
        <view v-else class="application-list">
          <view class="application-item" v-for="(item, index) in applications" :key="index">
            <view class="application-header">
              <text class="application-id">申报编号：{{item.reportId}}</text>
              <text class="application-date">申报时间：{{item.createTime}}</text>
            </view>
            
            <view class="application-info">
              <view class="info-row">
                <text class="info-label">申报人：</text>
                <text class="info-value">{{item.name}}</text>
              </view>
              <view class="info-row">
                <text class="info-label">灾害类型：</text>
                <text class="info-value">{{getDisasterTypeName(item.disasterType)}}</text>
              </view>
              <view class="info-row">
                <text class="info-label">灾害日期：</text>
                <text class="info-value">{{item.disasterDate}}</text>
              </view>
              <view class="info-row">
                <text class="info-label">申报状态：</text>
                <text class="info-value status" :class="'status-' + item.status">{{getStatusName(item.status)}}</text>
              </view>
            </view>
            
            <!-- 进度轴 -->
            <view class="progress-timeline">
              <view class="timeline-item" 
                v-for="(step, stepIndex) in statusSteps" 
                :key="stepIndex"
                :class="{'timeline-active': stepIndex <= getStatusIndex(item.status), 'timeline-current': stepIndex === getStatusIndex(item.status)}">
                <view class="timeline-dot"></view>
                <view class="timeline-line" v-if="stepIndex < statusSteps.length - 1"></view>
                <view class="timeline-label">{{step.label}}</view>
                <view class="timeline-date" v-if="stepIndex <= getStatusIndex(item.status)">
                  {{stepIndex === getStatusIndex(item.status) ? item.updateTime : '已完成'}}
                </view>
              </view>
            </view>
            
            <view class="application-footer">
              <view class="application-remark" v-if="item.remark">
                <text class="remark-label">审核备注：</text>
                <text class="remark-content">{{item.remark}}</text>
              </view>
              <button class="btn-detail" @tap="viewDetail(item)">查看详情</button>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 查询说明 -->
      <view class="guide-section">
        <view class="section-title">
          <text class="section-icon cuIcon-notice"></text>
          <text class="section-text">查询说明</text>
        </view>
        
        <view class="guide-content">
          <text class="guide-text">1. 可使用身份证号码、手机号或申报编号进行查询</text>
          <text class="guide-text">2. 查询结果将显示您的申报状态和处理进度</text>
          <text class="guide-text">3. 如申报已审核，可查看详细的审核意见</text>
          <text class="guide-text">4. 如需帮助，请拨打救灾服务热线：0123-4567890</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      queryType: 'idCard', // 查询方式：idCard-身份证, phone-手机号, reportId-申报编号
      queryValue: '', // 查询值
      hasResult: false, // 是否有查询结果
      applications: [], // 申报列表
      disasterTypes: [
        { value: 'flood', text: '洪涝灾害' },
        { value: 'earthquake', text: '地震灾害' },
        { value: 'drought', text: '干旱灾害' },
        { value: 'typhoon', text: '台风灾害' },
        { value: 'landslide', text: '滑坡灾害' },
        { value: 'fire', text: '火灾' },
        { value: 'other', text: '其他灾害' }
      ],
      statusSteps: [
        { status: 0, label: '已提交' },
        { status: 1, label: '审核中' },
        { status: 2, label: '已通过' },
        { status: 3, label: '已发放' }
      ]
    }
  },
  computed: {
    // 查询类型标签
    queryTypeLabel() {
      switch(this.queryType) {
        case 'idCard': return '身份证号码';
        case 'phone': return '手机号码';
        case 'reportId': return '申报编号';
        default: return '查询信息';
      }
    },
    // 输入框类型
    inputType() {
      switch(this.queryType) {
        case 'idCard': return 'idcard';
        case 'phone': return 'number';
        case 'reportId': return 'text';
        default: return 'text';
      }
    },
    // 占位符文本
    placeholder() {
      switch(this.queryType) {
        case 'idCard': return '请输入18位身份证号码';
        case 'phone': return '请输入11位手机号码';
        case 'reportId': return '请输入申报编号';
        default: return '请输入查询信息';
      }
    },
    // 最大输入长度
    maxLength() {
      switch(this.queryType) {
        case 'idCard': return 18;
        case 'phone': return 11;
        case 'reportId': return 20;
        default: return 50;
      }
    }
  },
  methods: {
    // 查询申报
    queryApplication() {
      if (!this.validateQuery()) return;
      
      // 显示加载
      uni.showLoading({
        title: '查询中...'
      });
      
      // 准备查询参数
      const queryParams = {};
      switch(this.queryType) {
        case 'idCard':
          queryParams.idCard = this.queryValue;
          break;
        case 'phone':
          queryParams.phone = this.queryValue;
          break;
        case 'reportId':
          queryParams.reportId = this.queryValue;
          break;
      }
      
      // 发送查询请求
      uni.request({
        url: 'http://localhost:8083/wxapi/query',
        method: 'POST',
        data: queryParams,
        success: (res) => {
          uni.hideLoading();
          this.hasResult = true;
          
          if (res.data.code === 200 && res.data.data) {
            // 查询成功，设置申报列表
            this.applications = Array.isArray(res.data.data) ? res.data.data : [res.data.data];
          } else {
            // 未查询到记录或查询失败
            this.applications = [];
            uni.showToast({
              title: res.data.msg || '未查询到相关申报记录',
              icon: 'none'
            });
          }
        },
        fail: (err) => {
          uni.hideLoading();
          console.error('查询失败', err);
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
    },
    
    // 验证查询输入
    validateQuery() {
      if (!this.queryValue) {
        uni.showToast({
          title: `请输入${this.queryTypeLabel}`,
          icon: 'none'
        });
        return false;
      }
      
      if (this.queryType === 'idCard' && this.queryValue.length !== 18) {
        uni.showToast({
          title: '请输入18位身份证号码',
          icon: 'none'
        });
        return false;
      }
      
      if (this.queryType === 'phone' && this.queryValue.length !== 11) {
        uni.showToast({
          title: '请输入11位手机号码',
          icon: 'none'
        });
        return false;
      }
      
      return true;
    },
    
    // 获取灾害类型名称
    getDisasterTypeName(value) {
      const item = this.disasterTypes.find(item => item.value === value);
      return item ? item.text : '未知灾害';
    },
    
    // 获取状态名称
    getStatusName(status) {
      const statusMap = {
        '-1': '已驳回',
        '0': '已提交',
        '1': '审核中',
        '2': '已通过',
        '3': '已发放',
        '4': '已完成'
      };
      return statusMap[status] || '未知状态';
    },
    
    // 获取状态步骤索引
    getStatusIndex(status) {
      status = parseInt(status);
      if (status === -1) return 0; // 驳回状态显示在提交位置
      const step = this.statusSteps.findIndex(item => item.status === status);
      return step !== -1 ? step : 0;
    },
    
    // 查看申报详情
    viewDetail(item) {
      // 可以跳转到详情页或者弹出详情弹窗
      uni.showToast({
        title: '详情页面开发中',
        icon: 'none'
      });
      // 或者跳转到详情页
      // uni.navigateTo({
      //   url: `/pages/disaster/detail?id=${item.id}`
      // });
    }
  }
}
</script>

<style lang="scss">
page {
  background-color: #f7f8fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', 'Roboto', sans-serif;
}

.query-container {
  padding-bottom: 40rpx;
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  padding: 60rpx 40rpx;
  color: #fff;
}

.header-title {
  display: flex;
  flex-direction: column;
}

.title-main {
  font-size: 44rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.title-sub {
  font-size: 28rpx;
  opacity: 0.8;
}

/* 表单容器 */
.form-container {
  padding: 30rpx;
}

/* 查询表单 */
.form-section, .result-section, .guide-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20rpx;
}

.section-icon {
  color: #4facfe;
  font-size: 36rpx;
  margin-right: 16rpx;
}

.section-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

/* 查询方式选择 */
.query-tabs {
  display: flex;
  margin-bottom: 30rpx;
  background-color: #f8f9fa;
  border-radius: 12rpx;
  overflow: hidden;
}

.tab {
  flex: 1;
  height: 120rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.tab-active {
  background: rgba(79, 172, 254, 0.1);
}

.tab-active:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 6rpx;
  background: #4facfe;
  border-radius: 3rpx;
}

.tab-icon {
  font-size: 48rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.tab-active .tab-icon {
  color: #4facfe;
}

.tab-text {
  font-size: 26rpx;
  color: #666;
}

.tab-active .tab-text {
  color: #4facfe;
  font-weight: bold;
}

/* 表单项 */
.form-item {
  margin-bottom: 30rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 12rpx;
}

.required:after {
  content: '*';
  color: #ff6b6b;
  margin-left: 4rpx;
}

.input {
  width: 100%;
  height: 90rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

/* 提交按钮 */
.btn-query {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  border-radius: 45rpx;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  box-shadow: 0 6rpx 20rpx rgba(79, 172, 254, 0.3);
  margin-top: 20rpx;
}

/* 无结果提示 */
.no-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
}

.no-result-icon {
  font-size: 120rpx;
  color: #ccc;
  margin-bottom: 20rpx;
}

.no-result-text {
  font-size: 30rpx;
  color: #999;
}

/* 申报列表 */
.application-list {
  margin-top: 20rpx;
}

.application-item {
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.application-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1px dashed #e0e0e0;
}

.application-id {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
}

.application-date {
  font-size: 24rpx;
  color: #999;
}

.application-info {
  margin-bottom: 30rpx;
}

.info-row {
  display: flex;
  margin-bottom: 10rpx;
}

.info-label {
  font-size: 26rpx;
  color: #666;
  width: 160rpx;
}

.info-value {
  font-size: 26rpx;
  color: #333;
  flex: 1;
}

.status {
  font-weight: bold;
}

.status-0 {
  color: #ff9800; /* 已提交 - 橙色 */
}

.status-1 {
  color: #2196f3; /* 审核中 - 蓝色 */
}

.status-2 {
  color: #4caf50; /* 已通过 - 绿色 */
}

.status-3 {
  color: #9c27b0; /* 已发放 - 紫色 */
}

.status-4 {
  color: #3f51b5; /* 已完成 - 靛蓝色 */
}

.status--1 {
  color: #f44336; /* 已驳回 - 红色 */
}

/* 进度轴 */
.progress-timeline {
  display: flex;
  justify-content: space-between;
  margin: 40rpx 0;
  position: relative;
}

.timeline-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
}

.timeline-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background: #ddd;
  margin-bottom: 16rpx;
  z-index: 1;
}

.timeline-line {
  position: absolute;
  top: 10rpx;
  left: 50%;
  width: 100%;
  height: 2rpx;
  background: #ddd;
  z-index: 0;
}

.timeline-active .timeline-dot {
  background: #4facfe;
  width: 24rpx;
  height: 24rpx;
  box-shadow: 0 0 0 6rpx rgba(79, 172, 254, 0.2);
}

.timeline-current .timeline-dot {
  background: #4facfe;
  width: 32rpx;
  height: 32rpx;
  box-shadow: 0 0 0 8rpx rgba(79, 172, 254, 0.3);
}

.timeline-active .timeline-line {
  background: #4facfe;
}

.timeline-label {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 8rpx;
}

.timeline-active .timeline-label {
  color: #333;
  font-weight: bold;
}

.timeline-date {
  font-size: 22rpx;
  color: #999;
}

.timeline-current .timeline-date {
  color: #4facfe;
}

/* 申报页脚 */
.application-footer {
  display: flex;
  flex-direction: column;
}

.application-remark {
  background: rgba(255, 152, 0, 0.1);
  border-radius: 8rpx;
  padding: 16rpx;
  margin-bottom: 20rpx;
}

.remark-label {
  font-size: 24rpx;
  color: #ff9800;
  font-weight: bold;
  margin-right: 10rpx;
}

.remark-content {
  font-size: 24rpx;
  color: #666;
}

.btn-detail {
  align-self: flex-end;
  width: 200rpx;
  height: 70rpx;
  line-height: 70rpx;
  background: #4facfe;
  color: #fff;
  font-size: 26rpx;
  border-radius: 35rpx;
}

/* 查询说明 */
.guide-content {
  padding: 10rpx 0;
}

.guide-text {
  display: block;
  font-size: 26rpx;
  color: #666;
  line-height: 1.8;
  margin-bottom: 10rpx;
  padding-left: 20rpx;
  position: relative;
}

.guide-text:before {
  content: '';
  position: absolute;
  left: 0;
  top: 16rpx;
  width: 8rpx;
  height: 8rpx;
  background: #4facfe;
  border-radius: 50%;
}
</style> 