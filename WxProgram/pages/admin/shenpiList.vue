<template>
	<view class="shenpiList-container">
		<!-- 顶部区域 -->
		<view class="header-area">
			<view class="header">
				<view class="title-section">
					<text class="page-title">灾情救助审批管理</text>
					<text class="subtitle">共{{ totalApplications }}个申请，{{ pendingCount }}个待处理</text>
				</view>
				
				<view class="refresh-btn" @click="loadApplicationList">
					<text>刷新</text>
				</view>
			</view>
			
			<!-- 过滤选项 -->
			<scroll-view class="filter-tabs" scroll-x>
				<view class="filter-tab" 
					v-for="(tab, index) in filterTabs" 
					:key="index"
					:class="{ active: currentFilter === tab.value }"
					@click="setFilter(tab.value)">
					<text>{{ tab.label }}</text>
					<text class="count" v-if="tab.count">{{ tab.count }}</text>
				</view>
			</scroll-view>
		</view>
		
		<!-- 搜索框 -->
		<view class="search-box">
			<input type="text" v-model="searchKeyword" placeholder="搜索申请人姓名或申请编号" @input="handleSearch" />
			<text class="search-icon">搜</text>
		</view>
		
		<!-- 申请列表 -->
		<view class="application-list" v-if="filteredApplications.length > 0">
			<view class="application-item" v-for="(item, index) in filteredApplications" :key="item.id" @click="viewApplication(item.id)">
				<view class="application-status" :class="'status-' + item.status">
					<text>{{ getStatusText(item.status) }}</text>
				</view>
				
				<view class="application-content">
					<view class="application-header">
						<text class="applicant-name">{{ item.name }}</text>
						<text class="application-id">编号: {{ item.reportId }}</text>
					</view>
					
					<view class="application-info">
						<text class="disaster-type">{{ item.disasterType }}</text>
						<text class="disaster-date">{{ item.disasterDate }}</text>
						<text class="estimated-loss">损失: {{ item.estimatedLoss }}元</text>
					</view>
					
					<view class="application-desc">
						<text class="desc-text">{{ truncateText(item.disasterDesc, 50) }}</text>
					</view>
					
					<view class="application-footer">
						<text class="submit-time">提交时间: {{ formatDate(item.createTime) }}</text>
						<view class="action-view">
							<text>查看详情</text>
							<text class="arrow">></text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<view class="empty-icon">!</view>
			<text class="empty-text">暂无{{ getFilterName(currentFilter) }}申请</text>
			<view class="empty-action" @click="loadApplicationList">
				<text>刷新列表</text>
			</view>
		</view>
		
		<!-- 底部统计 -->
		<view class="statistics-bar">
			<view class="stat-item">
				<text class="stat-value">{{ pendingCount }}</text>
				<text class="stat-label">待审批</text>
			</view>
			<view class="stat-item">
				<text class="stat-value">{{ approvedCount }}</text>
				<text class="stat-label">已通过</text>
			</view>
			<view class="stat-item">
				<text class="stat-value">{{ rejectedCount }}</text>
				<text class="stat-label">已驳回</text>
			</view>
			<view class="stat-item">
				<text class="stat-value">{{ issuedCount }}</text>
				<text class="stat-label">已发放</text>
			</view>
		</view>
	</view>
</template>

<script>
import { baseUrl } from "@/utils/apiconfig.js";
	export default {
		data() {
			return {
				// 过滤选项
				filterTabs: [
					{ label: '全部', value: 'all', count: 0 },
					{ label: '待审批', value: 'pending', count: 0 },
					{ label: '已通过', value: 'approved', count: 0 },
					{ label: '已驳回', value: 'rejected', count: 0 },
					{ label: '已发放', value: 'issued', count: 0 }
				],
				currentFilter: 'all', // 默认显示待审批
				
				// 搜索关键词
				searchKeyword: '',
				
				// 申请列表
				applications: [],
				
				// 分页参数
				pageNum: 1,
				pageSize: 10,
				total: 0,
				
				// 查询条件
				queryParams: {
					status: null,
					name: null,
					reportId: null
				},
				
				// 统计数据
				totalApplications: 0,
				pendingCount: 0,
				approvedCount: 0,
				rejectedCount: 0,
				issuedCount: 0
			}
		},
		computed: {
			// 过滤后的申请列表
			filteredApplications() {
				return this.applications;
			}
		},
		onShow() {
			// 每次页面显示时重新加载数据，确保审批后列表更新
			this.loadApplicationList();
		},
		// 添加下拉刷新事件
		onPullDownRefresh() {
			this.loadApplicationList().then(() => {
				uni.stopPullDownRefresh();
			});
		},
		methods: {
			// 加载申请列表
			loadApplicationList() {
				uni.showLoading({
					title: '加载中...'
				});
				
				// 从本地存储获取API基地址
				if (!baseUrl) {
					uni.hideLoading();
					// 使用模拟数据
					this.loadMockData();
					return Promise.resolve();
				}
				
				// 设置查询参数
				this.setQueryParams();
				
				// 构建请求参数 - 直接使用查询参数对象
				const requestData = {
					...this.queryParams,
					pageNum: this.pageNum,
					pageSize: this.pageSize
				};
				
				console.log('请求参数:', requestData);
				
				// 发送请求获取申请列表
				return new Promise((resolve, reject) => {
					uni.request({
						url: `${baseUrl}shebao/list`,
						method: 'POST', // 使用POST方法
						data: requestData,
						header: {
							'content-type': 'application/json', // 确保设置JSON内容类型
							'Authorization': uni.getStorageSync('token') || ''
						},
						success: (res) => {
							uni.hideLoading();
							
							// 检查响应状态
							if (res.statusCode !== 200) {
								uni.showToast({
									title: `请求失败: ${res.statusCode}`,
									icon: 'none'
								});
								this.loadMockData();
								resolve();
								return;
							}
							
							
							// 检查响应数据 - 适应新的接口格式
							if (res.data && res.data.code === 200 && res.data.data) {
								// 处理返回的数据 - 使用data字段直接获取列表
								const applications = res.data.data;
								console.log(applications)
								this.applications = this.formatApplicationData(applications);
								this.total = applications.length;
								// this.calculateStatistics();
							} else {
								uni.showToast({
									title: res.data.msg || '获取申请列表失败',
									icon: 'none'
								});
								
								// 使用模拟数据
								this.loadMockData();
							}
							resolve();
						},
						fail: (err) => {
							uni.hideLoading();
							uni.showToast({
								title: '网络请求失败',
								icon: 'none'
							});
							console.error('请求失败:', err);
							
							// 使用模拟数据
							this.loadMockData();
							resolve();
						}
					});
				});
			},
			
			
			loadGetNum() {
				uni.request({
					url: `${baseUrl}list/survey `,
					method: 'POST', // 使用POST方法
					header: {
						'content-type': 'application/json', // 确保设置JSON内容类型
						'Authorization': uni.getStorageSync('token') || ''
					},
					success: (res) => {
						uni.hideLoading();
						
						// 检查响应状态
						if (res.statusCode !== 200) {
							uni.showToast({
								title: `请求失败: ${res.statusCode}`,
								icon: 'none'
							});
							return;
						}
						const data = res.data
						this.totalApplications = data.all
						this.pendingCount  = data.pending
						this.approvedCount  = data.success
						this.rejectedCount  = data.fault
						this.issuedCount  = data.issued
						this.updateFilterCounts();
						
					},
					fail: (err) => {
						uni.hideLoading();
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
						console.error('请求失败:', err);
						
						// 使用模拟数据
						this.loadMockData();
						resolve();
					}
				});
			},
			// 设置查询参数
			setQueryParams() {
				// 重置查询参数
				this.queryParams = {};
				
				// 根据过滤条件设置状态参数
				if (this.currentFilter !== 'all') {
					const statusMap = {
						'pending': 0,
						'approved': 1,
						'rejected': 2,
						'issued': 3
					};
					
					this.queryParams.status = statusMap[this.currentFilter];
				}
				
				// 设置搜索关键词
				if (this.searchKeyword) {
					// 检查是否是申请编号格式
					if (this.searchKeyword.toUpperCase().startsWith('DR')) {
						this.queryParams.reportId = this.searchKeyword;
					} else {
						this.queryParams.name = this.searchKeyword;
					}
				}
			},
			
			// 格式化申请数据 - 确保字段映射正确
			formatApplicationData(applications) {
				return applications.map(item => ({
					id: item.id,
					reportId: item.reportId || `申请${item.id}`,
					name: item.name || '未知申请人',
					disasterType: item.disasterType || '未知灾害',
					disasterDate: item.disasterDate || '未知日期',
					disasterDesc: item.disasterDesc || '无灾情描述',
					estimatedLoss: item.estimatedLoss || 0,
					status: item.status !== undefined ? item.status : 0,
					createTime: item.createTime
				}));
			},
			
			// 获取各状态申请数量 - 简化为直接使用当前数据计算
			getApplicationCounts() {
				// 由于没有专门的统计API，我们直接使用列表数据进行计算
				this.calculateStatistics();
			},
			
			// 计算统计数据
			calculateStatistics() {
				this.totalApplications = this.applications.length;
				
				// 重置计数器
				this.pendingCount = 0;
				this.approvedCount = 0;
				this.rejectedCount = 0;
				this.issuedCount = 0;
				
				// 统计各状态的申请数量（仅限当前页面的数据）
				this.applications.forEach(item => {
					switch(item.status) {
						case 1:
							this.pendingCount++;
							break;
						case 2:
							this.rejectedCount++;
							break;
						case 3:
							this.approvedCount++;
							break;
						case 4:
							this.issuedCount++;
							break;
					}
				});
				
				// 更新过滤选项中的数量
				// this.updateFilterCounts();
			},
			
			// 更新过滤选项中的数量
			updateFilterCounts() {
				this.filterTabs[0].count = this.totalApplications;
				this.filterTabs[1].count = this.pendingCount;
				this.filterTabs[2].count = this.approvedCount;
				this.filterTabs[3].count = this.rejectedCount;
				this.filterTabs[4].count = this.issuedCount;
			},
			
			// 设置过滤条件
			setFilter(filter) {
				if (this.currentFilter === filter) return;
				
				this.currentFilter = filter;
				this.pageNum = 1; // 重置页码
				this.loadApplicationList();
			},
			
			// 搜索处理
			handleSearch() {
				this.pageNum = 1; // 重置页码
				this.loadApplicationList();
			},
			
			// 查看申请详情
			viewApplication(id) {
				if (!id) {
					uni.showToast({
						title: '申请ID无效',
						icon: 'none'
					});
					return;
				}
				
				
				// 导航到详情页，并传递ID参数
				uni.navigateTo({
					url: `/pages/admin/shenpi?id=${id}`,
					success: () => {
						console.log('成功跳转到详情页');
					},
					fail: (err) => {
						console.error('跳转失败:', err);
						uni.showToast({
							title: '页面跳转失败',
							icon: 'none'
						});
					}
				});
			},
			
			// 获取状态文本
			getStatusText(status) {
				const statusMap = {
					0: '待审核',
					1: '审核通过',
					2: '审核拒绝',
					3: '已发放',
				};
				return statusMap[status] || '未知状态';
			},
			
			// 获取过滤名称
			getFilterName(filter) {
				const filterMap = {
					'all': '相关',
					'pending': '待审批',
					'approved': '已通过',
					'rejected': '已驳回',
					'issued': '已发放'
				};
				return filterMap[filter] || '相关';
			},
			
			// 格式化日期
			formatDate(timestamp) {
				if (!timestamp) return '未知';
				
				const date = new Date(timestamp);
				return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
			},
			
			// 截断文本
			truncateText(text, maxLength) {
				if (!text) return '暂无描述';
				return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
			},
			
			// 加载模拟数据
			loadMockData() {
				// 创建10条模拟申请数据
				const mockData = [];
				
				for (let i = 1; i <= 10; i++) {
					const status = i % 5; // 生成0-4的状态码
					const createTime = Date.now() - (i * 24 * 60 * 60 * 1000); // 每条记录间隔一天
					
					mockData.push({
						id: i,
						reportId: 'DR' + (10000 + i),
						name: ['张三', '李四', '王五', '赵六', '钱七'][i % 5],
						disasterType: ['洪水', '地震', '台风', '火灾', '泥石流'][i % 5],
						disasterDate: this.formatDate(createTime - (5 * 24 * 60 * 60 * 1000)).split(' ')[0],
						disasterDesc: '受灾情况描述，这里是详细描述内容，描述受灾的具体情况和受损程度...',
						estimatedLoss: (i * 10000) + 5000,
						status: status,
						createTime: createTime
					});
				}
				
				this.applications = mockData;
				this.total = mockData.length;
				this.calculateStatistics();
			}
		},
		created() {
			this.loadGetNum()
		}
	}
	
</script>

<style lang="scss">
	page {
		background-color: #F5F7FA;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
	}

	.shenpiList-container {
		padding-bottom: 120rpx; /* 为底部统计栏留出空间 */
	}

	/* 顶部区域样式 */
	.header-area {
		background: linear-gradient(135deg, #1890FF, #36CBCB);
		padding: 40rpx 30rpx 20rpx;
		color: #FFFFFF;
		box-shadow: 0 4rpx 20rpx rgba(24, 144, 255, 0.2);
		width: 100%;
	}

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.title-section {
		flex: 1;
	}

	.page-title {
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 8rpx;
		display: block;
	}

	.subtitle {
		font-size: 24rpx;
		opacity: 0.8;
	}

	.refresh-btn {
		padding: 10rpx 30rpx;
		background-color: rgba(255, 255, 255, 0.2);
		border-radius: 30rpx;
		font-size: 24rpx;
	}

	/* 过滤选项卡 */
	.filter-tabs {
		display: flex;
		overflow-x: auto;
		width: 100%;
		white-space: nowrap;
	}

	.filter-tab {
		padding: 12rpx 24rpx;
		margin-right: 20rpx;
		font-size: 26rpx;
		border-radius: 30rpx;
		background-color: rgba(255, 255, 255, 0.1);
		transition: all 0.3s;
		display: inline-flex;
		align-items: center;
	}

	.filter-tab.active {
		background-color: #FFFFFF;
		color: #1890FF;
		font-weight: bold;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.count {
		font-size: 22rpx;
		background-color: rgba(255, 255, 255, 0.3);
		color: #FFFFFF;
		border-radius: 20rpx;
		padding: 2rpx 12rpx;
		margin-left: 10rpx;
		min-width: 40rpx;
		text-align: center;
	}

	.filter-tab.active .count {
		background-color: #1890FF;
		color: #FFFFFF;
	}

	/* 搜索框 */
	.search-box {
		margin: 30rpx;
		height: 80rpx;
		background-color: #FFFFFF;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.search-box input {
		flex: 1;
		height: 100%;
		font-size: 28rpx;
	}

	.search-icon {
		font-size: 28rpx;
		color: #999999;
		margin-left: 10rpx;
	}

	/* 申请列表样式 */
	.application-list {
		padding: 0 30rpx;
	}

	.application-item {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
		position: relative;
	}

	.application-status {
		position: absolute;
		top: 0;
		right: 0;
		padding: 8rpx 20rpx;
		font-size: 22rpx;
		font-weight: bold;
		color: #FFFFFF;
		border-radius: 0 0 0 16rpx;
	}

	.status-0 {
		background-color: #909399;
	}

	.status-1 {
		background-color: #67C23A;
		
	}

	.status-2 {
		background-color: #F56C6C;
	}

	.status-3 {
		background-color: #409EFF;
	}

	.status-4 {
		background-color: #409EFF;
	}

	.application-content {
		padding: 30rpx;
	}

	.application-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.applicant-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333333;
	}

	.application-id {
		font-size: 24rpx;
		color: #909399;
	}

	.application-info {
		display: flex;
		margin-bottom: 16rpx;
		flex-wrap: wrap;
	}

	.disaster-type {
		font-size: 24rpx;
		color: #FFFFFF;
		background-color: #1890FF;
		padding: 4rpx 16rpx;
		border-radius: 20rpx;
		margin-right: 16rpx;
		margin-bottom: 10rpx;
	}

	.disaster-date {
		font-size: 24rpx;
		color: #606266;
		margin-right: 16rpx;
		margin-bottom: 10rpx;
	}

	.estimated-loss {
		font-size: 24rpx;
		color: #F56C6C;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.application-desc {
		background-color: #F5F7FA;
		padding: 16rpx;
		border-radius: 8rpx;
		margin-bottom: 16rpx;
	}

	.desc-text {
		font-size: 26rpx;
		color: #606266;
		line-height: 1.5;
	}

	.application-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 16rpx;
		border-top: 1rpx dashed #EBEEF5;
	}

	.submit-time {
		font-size: 24rpx;
		color: #909399;
	}

	.action-view {
		display: flex;
		align-items: center;
		font-size: 26rpx;
		color: #1890FF;
	}

	.arrow {
		font-size: 24rpx;
		margin-left: 8rpx;
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
	}

	.empty-icon {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		background-color: #F0F2F5;
		color: #909399;
		font-size: 60rpx;
		font-weight: bold;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 20rpx;
	}

	.empty-text {
		font-size: 28rpx;
		color: #909399;
		margin-bottom: 30rpx;
	}

	.empty-action {
		padding: 16rpx 40rpx;
		background-color: #1890FF;
		color: #FFFFFF;
		font-size: 26rpx;
		border-radius: 30rpx;
	}

	/* 底部统计栏 */
	.statistics-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		background-color: #FFFFFF;
		padding: 20rpx 0;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.stat-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border-right: 1rpx solid #EBEEF5;
	}

	.stat-item:last-child {
		border-right: none;
	}

	.stat-value {
		font-size: 32rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 8rpx;
	}

	.stat-label {
		font-size: 24rpx;
		color: #909399;
	}
</style> 