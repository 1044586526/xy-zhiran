<template>
	<view class="admin-container">
		<!-- 顶部标题与状态概览 -->
		<view class="header">
			<view class="title-section">
				<text class="page-title">自然灾害救助资金申报审批管理平台</text>
				<text class="subtitle">欢迎回来，{{ userName || '管理员' }}</text>
			</view>
			
			<view class="quick-stats">
				<view class="stat-item">
					<text class="stat-value">{{ stats.pending }}</text>
					<text class="stat-label">待处理</text>
				</view>
				<view class="stat-item">
					<text class="stat-value">{{ stats.today }}</text>
					<text class="stat-label">今日申报</text>
				</view>
			</view>
		</view>
		
		<!-- 功能模块卡片区 -->
		<view class="section-title-row">
			<text class="section-title-text">功能模块</text>
		</view>
		
		<view class="function-grid">
			<!-- 申报审批 -->
			<view class="function-card" @tap="navigateTo('approval')">
				<view class="card-content">
					<view class="card-icon approval-icon">
						<text class="icon-text">审</text>
					</view>
					<view class="card-info">
						<view class="card-title">申报审批</view>
						<view class="card-desc">审批申请</view>
					</view>
				</view>
				<view class="badge" v-if="stats.pending > 0">{{ stats.pending }}</view>
			</view>
			
			<!-- 民主评议 -->
			<view class="function-card" @tap="navigateTo('review')">
				<view class="card-content">
					<view class="card-icon review-icon">
						<text class="icon-text">评</text>
					</view>
					<view class="card-info">
						<view class="card-title">民主评议</view>
						<view class="card-desc">意见评议</view>
					</view>
				</view>
				<view class="badge" v-if="stats.reviewPending > 0">{{ stats.reviewPending }}</view>
			</view>
			
			<!-- 阳光公示 -->
			<view class="function-card" @tap="navigateTo('display')">
				<view class="card-content">
					<view class="card-icon display-icon">
						<text class="icon-text">公</text>
					</view>
					<view class="card-info">
						<view class="card-title">阳光公示</view>
						<view class="card-desc">接受监督</view>
					</view>
				</view>
			</view>
			
			<!-- 审批查询 -->
			<view class="function-card" @tap="navigateTo('records')">
				<view class="card-content">
					<view class="card-icon records-icon">
						<text class="icon-text">查</text>
					</view>
					<view class="card-info">
						<view class="card-title">审批查询</view>
						<view class="card-desc">记录查询</view>
					</view>
				</view>
			</view>
			
			<!-- 用户管理 -->
			<view class="function-card" @tap="navigateTo('users')">
				<view class="card-content">
					<view class="card-icon users-icon">
						<text class="icon-text">用</text>
					</view>
					<view class="card-info">
						<view class="card-title">用户管理</view>
						<view class="card-desc">信息管理</view>
					</view>
				</view>
			</view>
			
			<!-- 数据统计 -->
			<view class="function-card" @tap="navigateTo('stats')">
				<view class="card-content">
					<view class="card-icon stats-icon">
						<text class="icon-text">统</text>
					</view>
					<view class="card-info">
						<view class="card-title">数据统计</view>
						<view class="card-desc">统计报表</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 数据概览区域 -->
		<view class="section-title-row">
			<text class="section-title-text">数据概览</text>
			<text class="refresh-btn" @tap="refreshStats">刷新</text>
		</view>
		
		<view class="statistics-section">
			<view class="stats-grid">
				<view class="stat-block">
					<text class="stat-block-value">{{ stats.total }}</text>
					<text class="stat-block-label">总申报数</text>
				</view>
				<view class="stat-block">
					<text class="stat-block-value">{{ stats.approved }}</text>
					<text class="stat-block-label">已批准</text>
				</view>
				<view class="stat-block">
					<text class="stat-block-value">{{ stats.rejected }}</text>
					<text class="stat-block-label">已驳回</text>
				</view>
				<view class="stat-block">
					<text class="stat-block-value">{{ stats.pending }}</text>
					<text class="stat-block-label">待处理</text>
				</view>
			</view>
			
			<!-- 简化图表展示 -->
			<view class="simple-chart">
				<view class="chart-bar approved" :style="{ width: getPercentage(stats.approved) }"></view>
				<view class="chart-bar pending" :style="{ width: getPercentage(stats.pending) }"></view>
				<view class="chart-bar rejected" :style="{ width: getPercentage(stats.rejected) }"></view>
			</view>
			
			<view class="chart-legend">
				<view class="legend-item">
					<view class="legend-color approved"></view>
					<text class="legend-text">已批准</text>
				</view>
				<view class="legend-item">
					<view class="legend-color pending"></view>
					<text class="legend-text">待处理</text>
				</view>
				<view class="legend-item">
					<view class="legend-color rejected"></view>
					<text class="legend-text">已驳回</text>
				</view>
			</view>
		</view>
		
		<!-- 待办事项区域 -->
		<view class="section-title-row">
			<text class="section-title-text">待办事项</text>
			<text class="view-all" @tap="viewAllTasks">查看全部</text>
		</view>
		
		<view class="tasks-section">
			<view class="empty-tip" v-if="pendingTasks.length === 0">
				<text class="empty-icon">i</text>
				<text>暂无待办事项</text>
			</view>
			
			<view class="task-list" v-else>
				<view class="task-item" v-for="(task, index) in pendingTasks" :key="index" @tap="handleTask(task)">
					<view class="task-icon" 
						:class="{
							'task-icon-approval': task.type === 'approval',
							'task-icon-review': task.type === 'review',
							'task-icon-publish': task.type === 'publish'
						}">
						<text class="task-icon-text">{{ task.type === 'approval' ? '审' : task.type === 'review' ? '评' : '公' }}</text>
					</view>
					<view class="task-content">
						<view class="task-title">{{ task.title }}</view>
						<view class="task-info">
							<text class="task-applicant">申请人: {{ task.applicant }}</text>
							<text class="task-time">{{ task.time }}</text>
						</view>
					</view>
					<text class="arrow-right">›</text>
				</view>
			</view>
		</view>
		
		<!-- 底部工具栏 -->
		<view class="bottom-toolbar">
			<view class="toolbar-item" @tap="navigateTo('approval')">
				<view class="toolbar-icon approval-icon">
					<text class="toolbar-icon-text">审</text>
				</view>
				<text class="toolbar-text">审批</text>
			</view>
			<view class="toolbar-item" @tap="navigateTo('review')">
				<view class="toolbar-icon review-icon">
					<text class="toolbar-icon-text">评</text>
				</view>
				<text class="toolbar-text">评议</text>
			</view>
			<view class="toolbar-item" @tap="navigateTo('display')">
				<view class="toolbar-icon display-icon">
					<text class="toolbar-icon-text">公</text>
				</view>
				<text class="toolbar-text">公示</text>
			</view>
			<view class="toolbar-item" @tap="refreshData">
				<view class="toolbar-icon refresh-icon">
					<text class="toolbar-icon-text">我</text>
				</view>
				<text class="toolbar-text">我的</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { baseUrl } from "@/utils/apiconfig.js";
	export default {
		data() {
			return {
				userName: '', // 管理员姓名，从用户信息中获取
				stats: {
					total: 10,
					pending: 2,
					approved: 2,
					rejected: 2,
					today: 5,
					reviewPending: 3
				},
				pendingTasks: [
					{
						id: 1,
						type: 'approval',
						title: '需审批张三的住房重建申请',
						applicant: '张三',
						time: '2小时前'
					},
					{
						id: 2,
						type: 'review',
						title: '需评议李四的农业设施修复申请',
						applicant: '李四',
						time: '3小时前'
					},
					{
						id: 3,
						type: 'approval',
						title: '需审批王五的生活物资申请',
						applicant: '王五',
						time: '1天前'
					}
				]
			}
		},
		onShow() {
			this.loadUserInfo();
			this.fetchStatistics();
		},
		methods: {
			// 加载用户信息
			loadUserInfo() {
				try {
					const userInfo = uni.getStorageSync('user_info');
					if (userInfo) {
						if (Array.isArray(userInfo) && userInfo.length > 0) {
							this.userName = userInfo[0].realName || '管理员';
						} else if (typeof userInfo === 'object') {
							this.userName = userInfo.realName || '管理员';
						}
					}
				} catch (e) {
					console.error('获取用户信息失败', e);
				}
			},
			
			// 获取统计数据
			fetchStatistics() {
				// 实际项目中应调用API获取统计数据
				console.log('加载统计数据');
			},
			
			// 计算百分比
			getPercentage(value) {
				const total = this.stats.approved + this.stats.pending + this.stats.rejected;
				if (total === 0) return '0%';
				return Math.round((value / total) * 100) + '%';
			},
			
			// 页面导航
			navigateTo(target) {
				const pageMap = {
					'approval': '/pages/admin/shenpiList',
					'review': '/pages/admin/minzhu',
					'display': '/pages/admin/yangguang',
					'records': '/pages/disaster/query',
					'users': '/pages/admin/user',
					'stats': '/pages/admin/dataStat'
				};
				
				if (pageMap[target]) {
					uni.navigateTo({
						url: pageMap[target],
						fail: (err) => {
							console.error('导航失败:', err);
							uni.showToast({
								title: '页面跳转失败',
								icon: 'none'
							});
						}
					});
				} else {
					uni.showToast({
						title: '功能开发中',
						icon: 'none'
					});
				}
			},
			
			// 查看所有待办
			viewAllTasks() {
				uni.showToast({
					title: '待办事项列表开发中',
					icon: 'none'
				});
			},
			
			// 处理待办任务
			handleTask(task) {
				if (task.type === 'approval') {
					this.navigateTo('approval');
				} else if (task.type === 'review') {
					this.navigateTo('review');
				} else {
					this.navigateTo('display');
				}
			},
			
			// 刷新统计数据
			refreshStats() {
				uni.showLoading({
					title: '刷新中...'
				});
				
				setTimeout(() => {
					this.stats.pending = Math.floor(Math.random() * 5) + 5;
					this.stats.today = Math.floor(Math.random() * 3) + 3;
					this.stats.total = this.stats.approved + this.stats.rejected + this.stats.pending;
					
					uni.hideLoading();
					uni.showToast({
						title: '数据已更新',
						icon: 'success'
					});
				}, 800);
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
						this.stats.total = data.all
						this.stats.pending = data.pending
						this.stats.rejected = data.fault
						this.stats.approved = data.success
						this.totalApplications = data.all
						this.stats.reviewPending = data.comment
						
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
			
			// 刷新所有数据
			refreshData() {
				// uni.showLoading({
				// 	title: '刷新中...'
				// });
				
				// setTimeout(() => {
				// 	this.fetchStatistics();
				// 	uni.hideLoading();
				// 	uni.showToast({
				// 		title: '数据已更新',
				// 		icon: 'success'
				// 	});
				// }, 800);
				uni.reLaunch({
					url: '/pages/me/me',
					success: (res) => {
						console.log('跳转成功:', res);
					},
					fail: (err) => {
						console.error('跳转失败:', err);
						uni.showToast({
							title: '跳转失败，请稍后重试',
							icon: 'none',
							duration: 2000
						});
					}
				});
			}
		},
		created() {
			this.loadGetNum()
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #F7F8FA;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
	}

	.admin-container {
		padding: 20rpx;
		padding-bottom: 130rpx;
	}

	/* 顶部区域样式 */
	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
		background: linear-gradient(135deg, #1890FF, #36CBCB);
		border-radius: 16rpx;
		padding: 30rpx;
		color: #FFFFFF;
		box-shadow: 0 4rpx 15rpx rgba(24, 144, 255, 0.2);
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

	.quick-stats {
		display: flex;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 12rpx;
		padding: 10rpx 20rpx;
	}

	.stat-item {
		margin-left: 30rpx;
		text-align: center;
	}

	.stat-value {
		font-size: 32rpx;
		font-weight: bold;
		display: block;
	}

	.stat-label {
		font-size: 22rpx;
		opacity: 0.8;
	}
	
	/* 区域标题 */
	.section-title-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin: 30rpx 0 20rpx;
		padding: 0 10rpx;
	}
	
	.section-title-text {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		position: relative;
		padding-left: 20rpx;
	}
	
	.section-title-text::before {
		content: '';
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 6rpx;
		height: 24rpx;
		background: #1890FF;
		border-radius: 3rpx;
	}
	
	.view-all, .refresh-btn {
		font-size: 24rpx;
		color: #1890FF;
	}

	/* 功能模块网格 - 简化为更紧凑的布局 */
	.function-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 15rpx;
		margin-bottom: 30rpx;
	}

	.function-card {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 20rpx;
		position: relative;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		transition: all 0.3s ease;
	}
	
	.function-card:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	.card-content {
		display: flex;
		align-items: center;
	}

	.card-icon {
		width: 50rpx;
		height: 50rpx;
		border-radius: 10rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 15rpx;
	}
    
    .icon-text {
        color: #FFFFFF;
        font-size: 24rpx;
        font-weight: bold;
    }
	
	.card-info {
		flex: 1;
	}

	.card-title {
		font-size: 26rpx;
		font-weight: bold;
		color: #333333;
	}

	.card-desc {
		font-size: 20rpx;
		color: #999999;
	}

	.badge {
		position: absolute;
		top: 10rpx;
		right: 10rpx;
		min-width: 30rpx;
		height: 30rpx;
		border-radius: 15rpx;
		background-color: #FF4D4F;
		color: #FFFFFF;
		font-size: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 8rpx;
	}

	.approval-icon {
		background: linear-gradient(135deg, #1890FF, #52C41A);
	}

	.review-icon {
		background: linear-gradient(135deg, #FAAD14, #FF7A45);
	}

	.display-icon {
		background: linear-gradient(135deg, #722ED1, #9254DE);
	}

	.records-icon {
		background: linear-gradient(135deg, #13C2C2, #36CFC9);
	}

	.users-icon {
		background: linear-gradient(135deg, #F759AB, #EB2F96);
	}

	.stats-icon {
		background: linear-gradient(135deg, #2F54EB, #597EF7);
	}
	
	.refresh-icon {
		background: linear-gradient(135deg, #1890FF, #36CBCB);
	}

	/* 统计数据区域 - 简化 */
	.statistics-section {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 20rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.stats-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 10rpx;
		margin-bottom: 20rpx;
	}

	.stat-block {
		text-align: center;
		padding: 15rpx 0;
	}

	.stat-block-value {
		font-size: 32rpx;
		font-weight: bold;
		color: #333333;
		display: block;
	}

	.stat-block-label {
		font-size: 20rpx;
		color: #999999;
	}
	
	/* 简化图表 */
	.simple-chart {
		height: 16rpx;
		display: flex;
		border-radius: 8rpx;
		overflow: hidden;
		background-color: #F0F0F0;
		margin: 20rpx 0;
	}

	.chart-bar {
		height: 100%;
	}

	.chart-legend {
		display: flex;
		justify-content: center;
		flex-wrap: wrap;
	}
	
	.legend-item {
		display: flex;
		align-items: center;
		margin: 0 15rpx;
	}
	
	.legend-color {
		width: 16rpx;
		height: 16rpx;
		border-radius: 4rpx;
		margin-right: 6rpx;
	}
	
	.legend-text {
		font-size: 20rpx;
		color: #666;
	}

	.approved {
		background-color: #52C41A;
	}

	.pending {
		background-color: #FAAD14;
	}

	.rejected {
		background-color: #FF4D4F;
	}

	/* 待办事项区域 - 简化 */
	.tasks-section {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 20rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.task-list {
		max-height: 300rpx;
		overflow-y: auto;
	}

	.task-item {
		display: flex;
		align-items: center;
		padding: 15rpx 0;
		border-bottom: 1rpx solid #F0F0F0;
	}

	.task-item:last-child {
		border-bottom: none;
	}

	.task-icon {
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 15rpx;
		flex-shrink: 0;
	}
    
    .task-icon-text {
        font-size: 18rpx;
        color: #FFFFFF;
        font-weight: bold;
    }

	.task-content {
		flex: 1;
		overflow: hidden;
	}

	.task-title {
		font-size: 24rpx;
		color: #333333;
		margin-bottom: 4rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.task-info {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.task-applicant {
		font-size: 20rpx;
		color: #666666;
	}

	.task-time {
		font-size: 20rpx;
		color: #999999;
	}
    
    .arrow-right {
        font-size: 28rpx;
        color: #CCCCCC;
        margin-left: 10rpx;
    }

	/* 空状态提示 */
	.empty-tip {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 30rpx 0;
		color: #999999;
		font-size: 24rpx;
	}

	.empty-icon {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		background-color: #EEEEEE;
		color: #999999;
		display: flex;
		align-items: center;
		justify-content: center;
		font-style: italic;
		font-weight: bold;
		margin-bottom: 15rpx;
	}
	
	/* 底部工具栏 */
	.bottom-toolbar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 100rpx;
		background: #FFFFFF;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
		display: flex;
		align-items: center;
		justify-content: space-around;
		padding-bottom: env(safe-area-inset-bottom);
	}
	
	.toolbar-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 10rpx 0;
	}
	
	.toolbar-icon {
		width: 40rpx;
		height: 40rpx;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 6rpx;
	}
	
	.toolbar-icon-text {
		color: #FFFFFF;
		font-size: 20rpx;
		font-weight: bold;
	}
	
	.toolbar-text {
		font-size: 20rpx;
		color: #666;
	}
</style>
