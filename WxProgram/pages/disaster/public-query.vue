<template>
  <view class="public-query-container">
    <!-- 页面标题 -->
    <view class="page-header">
      <view class="header-title">
        <text class="title-main">救助信息公示</text>
        <text class="title-sub">Relief Information Disclosure</text>
      </view>
    </view>
    
    <!-- 查询表单区域 -->
    <view class="form-container">
      <view class="form-section">
        <view class="section-title">
          <text class="section-icon cuIcon-searchlist"></text>
          <text class="section-text">信息筛选</text>
        </view>
        
        <!-- 筛选选项 -->
        <view class="filter-options">
          <view class="filter-row">
            <text class="filter-label">灾害类型</text>
            <picker class="filter-picker" mode="selector" :range="disasterTypeNames" @change="onDisasterTypeChange">
              <view class="picker-text">
                <text v-if="filterData.disasterType">{{getDisasterTypeName(filterData.disasterType)}}</text>
                <text v-else class="placeholder">全部</text>
                <text class="cuIcon-triangledownfill picker-icon"></text>
              </view>
            </picker>
          </view>
          
          <view class="filter-row">
            <text class="filter-label">区域筛选</text>
            <picker class="filter-picker" mode="selector" :range="regionNames" @change="onRegionChange">
              <view class="picker-text">
                <text v-if="filterData.regionCode">{{getRegionName(filterData.regionCode)}}</text>
                <text v-else class="placeholder">全部区域</text>
                <text class="cuIcon-triangledownfill picker-icon"></text>
              </view>
            </picker>
          </view>
          
          <view class="filter-row">
            <text class="filter-label">发放时间</text>
            <picker class="filter-picker" mode="date" fields="month" @change="onDateChange">
              <view class="picker-text">
                <text v-if="filterData.month">{{filterData.month}}</text>
                <text v-else class="placeholder">全部时间</text>
                <text class="cuIcon-triangledownfill picker-icon"></text>
              </view>
            </picker>
          </view>
        </view>
        
        <!-- 关键词搜索 -->
        <view class="search-box">
          <input class="search-input" type="text" v-model="filterData.keyword" placeholder="输入姓名/地址关键词搜索" confirm-type="search" @confirm="searchPublicInfo" />
          <button class="search-btn" @tap="searchPublicInfo">
            <text class="cuIcon-search"></text>
          </button>
        </view>
      </view>
      
      <!-- 公示信息列表 -->
      <view class="result-section">
        <view class="section-title">
          <text class="section-icon cuIcon-newsfill"></text>
          <text class="section-text">公示信息</text>
          <text class="result-count" v-if="publicInfoList.length > 0">(共{{publicInfoList.length}}条)</text>
        </view>
        
        <view v-if="isLoading" class="loading-box">
          <view class="loading-spinner"></view>
          <text class="loading-text">数据加载中...</text>
        </view>
        
        <view v-else-if="publicInfoList.length === 0" class="no-result">
          <text class="cuIcon-searchlist no-result-icon"></text>
          <text class="no-result-text">暂无公示信息</text>
          <text class="no-result-tip">可调整筛选条件重新查询</text>
        </view>
        
        <view v-else class="public-info-list">
          <view class="info-card" v-for="(item, index) in publicInfoList" :key="index" @tap="viewPublicDetail(item.id)">
            <view class="info-header">
              <view class="info-tag" :class="'tag-type-' + (item.disasterType || 'other')">
                {{getDisasterTypeName(item.disasterType)}}
              </view>
              <text class="info-date">发放日期: {{item.releaseDate}}</text>
            </view>
            
            <view class="info-body">
              <view class="info-row">
                <text class="info-label">受助人:</text>
                <text class="info-name">{{item.name}}</text>
                <text class="info-region">{{item.regionName}}</text>
              </view>
              
              <view class="info-row">
                <text class="info-label">受灾情况:</text>
                <text class="info-desc">{{item.disasterDesc}}</text>
              </view>
              
              <view class="info-row">
                <text class="info-label">救助方式:</text>
                <view class="info-relief-types">
                  <view class="relief-type-tag" v-for="(type, typeIndex) in item.reliefTypesList" :key="typeIndex">
                    {{getReliefTypeName(type)}}
                  </view>
                </view>
              </view>
              
              <view class="info-row" v-if="item.fundAmount">
                <text class="info-label">救助金额:</text>
                <text class="info-amount">¥ {{item.fundAmount}}</text>
              </view>
              
              <view class="info-row" v-if="item.materialValue">
                <text class="info-label">物资价值:</text>
                <text class="info-amount">¥ {{item.materialValue}}</text>
              </view>
            </view>
            
            <view class="info-footer">
              <text class="info-number">公示编号: {{item.publicNo}}</text>
              <view class="info-action">
                <text>查看详情</text>
                <text class="cuIcon-right"></text>
              </view>
            </view>
          </view>
        </view>
        
        <!-- 分页加载更多 -->
        <view class="load-more" v-if="publicInfoList.length > 0 && hasMoreData">
          <text class="load-more-text" @tap="loadMoreData">加载更多</text>
        </view>
      </view>
      
      <!-- 公示说明 -->
      <view class="guide-section">
        <view class="section-title">
          <text class="section-icon cuIcon-notice"></text>
          <text class="section-text">公示说明</text>
        </view>
        
        <view class="guide-content">
          <text class="guide-text">1. 本页面公示的信息是已完成审核并发放的救助记录</text>
          <text class="guide-text">2. 信息公示期为自发放之日起30天</text>
          <text class="guide-text">3. 如对公示信息有异议，请拨打监督电话：0123-4567890</text>
          <text class="guide-text">4. 更多救助政策详情，请查阅"救助政策"栏目</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      isLoading: false,
      hasMoreData: false,
      page: 1,
      pageSize: 10,
      filterData: {
        keyword: '',
        disasterType: '',
        regionCode: '',
        month: ''
      },
      publicInfoList: [],
      disasterTypes: [
        { value: '', text: '全部类型' },
        { value: 'flood', text: '洪涝灾害' },
        { value: 'earthquake', text: '地震灾害' },
        { value: 'drought', text: '干旱灾害' },
        { value: 'typhoon', text: '台风灾害' },
        { value: 'landslide', text: '滑坡灾害' },
        { value: 'fire', text: '火灾' },
        { value: 'other', text: '其他灾害' }
      ],
      regions: [
        { code: '', name: '全部区域' },
        { code: '110101', name: '东城区' },
        { code: '110102', name: '西城区' },
        { code: '110105', name: '朝阳区' },
        { code: '110106', name: '丰台区' },
        { code: '110107', name: '石景山区' },
        // 可根据实际需要扩展更多区域
      ],
      reliefTypes: [
        { value: 'fund', text: '资金救助' },
        { value: 'material', text: '物资救助' },
        { value: 'housing', text: '住房救助' },
        { value: 'medical', text: '医疗救助' },
        { value: 'employment', text: '就业援助' },
        { value: 'other', text: '其他援助' }
      ]
    }
  },
  computed: {
    disasterTypeNames() {
      return this.disasterTypes.map(item => item.text);
    },
    regionNames() {
      return this.regions.map(item => item.name);
    }
  },
  onLoad() {
    console.log('页面加载');
    // 获取baseUrl，确保存在
    const baseUrl = uni.getStorageSync('baseUrl');
    console.log('当前baseUrl:', baseUrl);
    if (!baseUrl) {
      // 如果没有设置，使用默认值
      uni.setStorageSync('baseUrl', 'http://localhost:8083');
      console.log('已设置默认baseUrl');
    }
    
    // 页面加载时获取数据
    this.searchPublicInfo();
  },
  methods: {
    // 灾害类型选择
    onDisasterTypeChange(e) {
      const index = e.detail.value;
      this.filterData.disasterType = this.disasterTypes[index].value;
      this.page = 1; // 重置页码
      this.searchPublicInfo();
    },
    
    // 获取灾害类型名称
    getDisasterTypeName(value) {
      const item = this.disasterTypes.find(item => item.value === value);
      return item ? item.text : '全部类型';
    },
    
    // 区域选择
    onRegionChange(e) {
      const index = e.detail.value;
      this.filterData.regionCode = this.regions[index].code;
      this.page = 1; // 重置页码
      this.searchPublicInfo();
    },
    
    // 获取区域名称
    getRegionName(code) {
      const item = this.regions.find(item => item.code === code);
      return item ? item.name : '全部区域';
    },
    
    // 日期选择
    onDateChange(e) {
      this.filterData.month = e.detail.value;
      this.page = 1; // 重置页码
      this.searchPublicInfo();
    },
    
    // 获取救助类型名称
    getReliefTypeName(value) {
      const item = this.reliefTypes.find(item => item.value === value);
      return item ? item.text : '其他援助';
    },
    
    // 查询公示信息
    searchPublicInfo() {
      this.isLoading = true;
      this.page = 1; // 重置页码
      
      // 构建查询参数
      const params = {
        page: this.page,
        pageSize: this.pageSize,
        keyword: this.filterData.keyword || '',
        disasterType: this.filterData.disasterType || '',
        regionCode: this.filterData.regionCode || '',
        month: this.filterData.month || '',
        status: 3 // 只查询状态为3（已发放）的记录
      };
      
      console.log("查询参数:", params); // 调试用
      
      // 发送查询请求
      uni.request({
        url: 'http://localhost:8083/wxapi/shebao/public',
        method: 'GET',
        data: params,
        header: {
          'Content-Type': 'application/json' // 确保请求头正确
        },
        success: (res) => {
          this.isLoading = false;
          console.log("请求结果:", res.data); // 调试用
          
          if (res.data.code === 200) {
            // 适配PageInfo返回格式
            const pageInfo = res.data.data;
            
            // 根据后端返回的PageInfo结构提取数据
            const records = pageInfo.list || [];
            const total = pageInfo.total || 0;
            
            console.log("解析得到记录:", records);
            
            this.publicInfoList = this.processPublicData(records);
            this.hasMoreData = this.publicInfoList.length < total;
            
            if (this.publicInfoList.length === 0) {
              uni.showToast({
                title: '暂无公示信息',
                icon: 'none'
              });
            }
          } else {
            this.publicInfoList = [];
            this.hasMoreData = false;
            uni.showToast({
              title: res.data.msg || '查询失败',
              icon: 'none'
            });
          }
        },
        fail: (err) => {
          this.isLoading = false;
          console.error('查询失败', err);
          uni.showToast({
            title: '网络异常，请稍后重试',
            icon: 'none'
          });
        }
      });
    },
    
    // 加载更多数据
    loadMoreData() {
      if (this.isLoading || !this.hasMoreData) return;
      
      this.isLoading = true;
      this.page += 1;
      
      // 构建查询参数
      const params = {
        page: this.page,
        pageSize: this.pageSize,
        keyword: this.filterData.keyword || '',
        disasterType: this.filterData.disasterType || '',
        regionCode: this.filterData.regionCode || '',
        month: this.filterData.month || '',
        status: 3 // 只查询状态为3（已发放）的记录
      };
      
      // 发送查询请求
      uni.request({
        url: 'http://localhost:8083/wxapi/shebao/public',
        method: 'GET',
        data: params,
        header: {
          'Content-Type': 'application/json'
        },
        success: (res) => {
          this.isLoading = false;
          
          if (res.data.code === 200) {
            // 适配PageInfo返回格式
            const pageInfo = res.data.data;
            
            // 根据后端返回的PageInfo结构提取数据
            const records = pageInfo.list || [];
            const total = pageInfo.total || 0;
            
            const newData = this.processPublicData(records);
            this.publicInfoList = [...this.publicInfoList, ...newData];
            this.hasMoreData = this.publicInfoList.length < total;
          } else {
            this.hasMoreData = false;
          }
        },
        fail: (err) => {
          this.isLoading = false;
          console.error('加载更多失败', err);
        }
      });
    },
    
    // 处理公示数据
    processPublicData(records) {
      if (!records || !Array.isArray(records)) {
        console.error('无效的记录数据', records);
        return [];
      }
      
      return records.map(record => {
        console.log("处理记录:", record);
        
        // 处理救助类型列表
        let reliefTypesList = [];
        if (record.reliefTypes) {
          try {
            // 如果已经是数组就直接使用
            if (Array.isArray(record.reliefTypes)) {
              reliefTypesList = record.reliefTypes;
            } else {
              reliefTypesList = JSON.parse(record.reliefTypes);
            }
          } catch (e) {
            console.error('解析救助类型失败', record.reliefTypes, e);
            // 尝试将字符串按逗号分割
            if (typeof record.reliefTypes === 'string') {
              reliefTypesList = record.reliefTypes.split(',');
            }
          }
        }
        
        // 格式化发放日期，如果没有则使用更新时间
        let releaseDate = record.releaseDate || record.updateTime || record.createTime || '未知日期';
        if (typeof releaseDate === 'string' && releaseDate.length > 10) {
          releaseDate = releaseDate.substring(0, 10); // 只取日期部分
        }
        
        // 构建区域名称
        const regionName = record.regionName || '未知区域';
        
        // 灾害类型处理
        let disasterType = record.disasterType || 'other';
        
        // 预估金额处理
        const fundAmount = record.requestAmount || record.fundAmount || record.estimatedLoss || 0;
        
        // 公示编号处理
        const publicNo = record.reportId || record.id || '未编号';
        
        return {
          ...record,
          id: record.id,
          reliefTypesList,
          releaseDate,
          regionName,
          disasterType,
          fundAmount,
          publicNo,
          disasterDesc: record.disasterDesc || '无详细描述'
        };
      });
    },
    
    // 查看公示详情
    viewPublicDetail(id) {
      uni.navigateTo({
        url: `./public-detail?id=${id}`
      });
    }
  }
}
</script>

<style lang="scss">
page {
  background-color: #f7f8fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', 'Roboto', sans-serif;
}

.public-query-container {
  padding-bottom: 40rpx;
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #0acf83 0%, #00e778 100%);
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

/* 表单分节 */
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
  color: #0acf83;
  font-size: 36rpx;
  margin-right: 16rpx;
}

.section-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.result-count {
  font-size: 26rpx;
  color: #999;
  margin-left: 16rpx;
  font-weight: normal;
}

/* 筛选选项 */
.filter-options {
  margin-bottom: 30rpx;
}

.filter-row {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.filter-label {
  width: 160rpx;
  font-size: 28rpx;
  color: #333;
}

.filter-picker {
  flex: 1;
}

.picker-text {
  height: 80rpx;
  line-height: 80rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.picker-icon {
  color: #999;
  font-size: 24rpx;
}

.placeholder {
  color: #999;
}

/* 搜索框 */
.search-box {
  display: flex;
  margin-top: 30rpx;
}

.search-input {
  flex: 1;
  height: 90rpx;
  background: #f8f9fa;
  border-radius: 12rpx 0 0 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
}

.search-btn {
  width: 100rpx;
  height: 90rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #0acf83;
  color: #fff;
  border-radius: 0 12rpx 12rpx 0;
  margin: 0;
  padding: 0;
}

.search-btn text {
  font-size: 36rpx;
}

/* 加载状态 */
.loading-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 6rpx solid #f3f3f3;
  border-top: 6rpx solid #0acf83;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20rpx;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  font-size: 28rpx;
  color: #999;
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
  margin-bottom: 10rpx;
}

.no-result-tip {
  font-size: 26rpx;
  color: #bbb;
}

/* 公示信息列表 */
.public-info-list {
  margin-top: 20rpx;
}

.info-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #f8f9fa;
  border-bottom: 1px solid #f0f0f0;
}

.info-tag {
  font-size: 24rpx;
  color: #fff;
  padding: 6rpx 16rpx;
  border-radius: 100rpx;
}

.tag-type-flood {
  background: linear-gradient(to right, #4facfe, #00f2fe);
}

.tag-type-earthquake {
  background: linear-gradient(to right, #ff6b6b, #ff8e8e);
}

.tag-type-drought {
  background: linear-gradient(to right, #ff9f43, #ffc93c);
}

.tag-type-typhoon {
  background: linear-gradient(to right, #6c5ce7, #a29bfe);
}

.tag-type-landslide {
  background: linear-gradient(to right, #0acf83, #00e778);
}

.tag-type-fire {
  background: linear-gradient(to right, #ee5253, #ff6b6b);
}

.tag-type-other {
  background: linear-gradient(to right, #808080, #a0a0a0);
}

.info-date {
  font-size: 24rpx;
  color: #999;
}

.info-body {
  padding: 24rpx;
}

.info-row {
  display: flex;
  margin-bottom: 16rpx;
  flex-wrap: wrap;
}

.info-label {
  width: 160rpx;
  font-size: 26rpx;
  color: #666;
  flex-shrink: 0;
}

.info-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-right: 20rpx;
}

.info-region {
  font-size: 24rpx;
  color: #666;
  background: #f8f9fa;
  padding: 4rpx 16rpx;
  border-radius: 100rpx;
}

.info-desc {
  flex: 1;
  font-size: 26rpx;
  color: #333;
}

.info-relief-types {
  display: flex;
  flex-wrap: wrap;
  flex: 1;
}

.relief-type-tag {
  background: rgba(10, 207, 131, 0.1);
  color: #0acf83;
  font-size: 24rpx;
  padding: 4rpx 16rpx;
  border-radius: 100rpx;
  margin-right: 16rpx;
  margin-bottom: 10rpx;
}

.info-amount {
  font-size: 28rpx;
  color: #ff6b6b;
  font-weight: bold;
}

.info-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  border-top: 1px solid #f0f0f0;
  background: #f8f9fa;
}

.info-number {
  font-size: 24rpx;
  color: #999;
}

.info-action {
  font-size: 26rpx;
  color: #0acf83;
  display: flex;
  align-items: center;
}

.info-action .cuIcon-right {
  font-size: 22rpx;
  margin-left: 6rpx;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 30rpx 0;
}

.load-more-text {
  font-size: 28rpx;
  color: #0acf83;
  background: rgba(10, 207, 131, 0.1);
  padding: 12rpx 40rpx;
  border-radius: 100rpx;
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
  background: #0acf83;
  border-radius: 50%;
}
</style> 