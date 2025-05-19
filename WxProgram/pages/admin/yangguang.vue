<template>
	<view class="yangguang-container">
		<view class="header">
			<text class="title">阳光公示</text>
			<text class="subtitle">灾情救助资金使用公开透明</text>
		</view>
		
		<view class="search-box">
			<input type="text" v-model="searchKeyword" placeholder="搜索申请人姓名或申请编号" @input="handleSearch" />
			<text class="search-icon">搜</text>
		</view>
		
		<view class="content" v-if="publicList.length > 0">
			<!-- 公示列表 -->
			<view class="public-list">
				<view class="public-item" v-for="(item, index) in filteredList" :key="item.id" @tap="showDetail(item)">
					<view class="public-header">
						<text class="applicant-name">{{ item.name }}</text>
						<text class="public-status">已通过</text>
					</view>
					
					<view class="public-info">
						<text class="disaster-type">{{ formatDisasterType(item.disasterType) }}</text>
						<text class="disaster-date">{{ item.disasterDate }}</text>
						<text class="aid-amount">救助金额: {{ item.approvedAmount || 0 }}元</text>
					</view>
					
					<view class="public-footer">
						<text class="submit-time">申报时间: {{ formatDate(item.createTime) }}</text>
						<text class="approve-time">审批时间: {{ formatDate(item.approveTime) }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else>
			<view class="empty-icon">!</view>
			<text class="empty-text">{{ loading ? '加载中...' : '暂无公示内容' }}</text>
			<text class="empty-desc" v-if="!loading">目前没有已审批通过的灾情救助申请</text>
			<view class="empty-action" v-if="!loading" @tap="getPublicData">
				<text>刷新</text>
			</view>
		</view>
		
		<!-- 详情弹窗 -->
		<view class="modal-overlay" v-if="showDetailModal" @tap="closeDetail"></view>
		<view class="detail-modal" v-if="showDetailModal">
			<view class="modal-header">
				<text class="modal-title">救助详情</text>
				<text class="modal-close" @tap="closeDetail">×</text>
			</view>
			
			<scroll-view class="modal-body" scroll-y="true">
				<view class="applicant-info">
					<text class="detail-item-label">申请人:</text>
					<text class="detail-item-value">{{ currentItem.name }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-item-label">申请人户籍地:</text>
					<text class="detail-item-value">{{ formatDisasterType(currentItem.address) }}</text>
				</view>
				<view class="detail-item">
					<text class="detail-item-label">申请人身份证号:</text>
					<text class="detail-item-value">{{ formatDisasterType(currentItem.cardNo) }}</text>
				</view>
				
				<view class="detail-item">
					<text class="detail-item-label">灾害类型:</text>
					<text class="detail-item-value">{{ formatDisasterType(currentItem.disasterType) }}</text>
				</view>
				
				<view class="detail-item">
					<text class="detail-item-label">灾害时间:</text>
					<text class="detail-item-value">{{ currentItem.disasterDate }}</text>
				</view>
				
				<view class="detail-item">
					<text class="detail-item-label">受灾描述:</text>
					<text class="detail-item-value">{{ currentItem.disasterDesc || '无描述' }}</text>
				</view>
				
				<view class="detail-item">
					<text class="detail-item-label">估计损失:</text>
					<text class="detail-item-value">{{ currentItem.estimatedLoss || 0 }}元</text>
				</view>
				
				<view class="detail-item">
					<text class="detail-item-label">申请金额:</text>
					<text class="detail-item-value">{{ currentItem.requestAmount || 0 }}元</text>
				</view>
				
				<view class="detail-item highlight">
					<text class="detail-item-label">批准金额:</text>
					<text class="detail-item-value">{{ currentItem.approvedAmount || 0 }}元</text>
				</view>
				
				<view class="detail-item">
					<text class="detail-item-label">审批意见:</text>
					<text class="detail-item-value">{{ currentItem.approveRemark || '无审批意见' }}</text>
				</view>
				
				<view class="detail-item">
					<text class="detail-item-label">审批时间:</text>
					<text class="detail-item-value">{{ formatDate(currentItem.approveTime) }}</text>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	import { baseUrl } from "@/utils/apiconfig.js";
	export default {
		data() {
			return {
				publicList: [], // 公示数据列表
				searchKeyword: '', // 搜索关键词
				loading: true, // 加载状态
				showDetailModal: false, // 是否显示详情弹窗
				currentItem: {} // 当前查看的详情项
			}
		},
		computed: {
			// 过滤后的列表
			filteredList() {
				if (!this.searchKeyword) {
					return this.publicList;
				}
				
				const keyword = this.searchKeyword.toLowerCase();
				return this.publicList.filter(item => {
					return (item.name && item.name.toLowerCase().includes(keyword)) || 
						   (item.reportId && item.reportId.toLowerCase().includes(keyword));
				});
			}
		},
		onShow() {
			// 页面显示时获取公示数据
			this.getPublicData();
		},
		// 添加下拉刷新
		onPullDownRefresh() {
			this.getPublicData().then(() => {
				uni.stopPullDownRefresh();
			});
		},
		methods: {
			// 获取公示数据
			getPublicData() {
				this.loading = true;
				
				return new Promise((resolve, reject) => {
					// 构建请求参数 - 只获取审批通过的申请
					const requestData = {
						status: 3 // 审批通过的状态码，根据实际情况调整
					};
					
					uni.request({
						url: `${baseUrl}shebao/list`,
						method: 'POST',
						data: requestData,
						header: {
							'content-type': 'application/json',
							'Authorization': uni.getStorageSync('token') || ''
						},
						success: (res) => {
							this.loading = false;
							
							if (res.statusCode === 200 && res.data.code === 200) {
								// 处理返回的数据
								let applications = [];
								console.log(res.data)
								// 兼容多种数据结构
								if (res.data.data) {
									if (Array.isArray(res.data.data)) {
										applications = res.data.data;
									} else if (res.data.data.rows && Array.isArray(res.data.data.rows)) {
										applications = res.data.data.rows;
									} else if (typeof res.data.data === 'object') {
										applications = [res.data.data];
									}
								}
								
								// 格式化数据
								this.publicList = this.formatApplicationData(applications);
							} else {
								uni.showToast({
									title: res.data.msg || '获取公示数据失败',
									icon: 'none'
								});
								this.publicList = [];
							}
							resolve();
						},
						fail: (err) => {
							this.loading = false;
							console.error('请求失败:', err);
							uni.showToast({
								title: '网络请求失败',
								icon: 'none'
							});
							this.publicList = [];
							resolve();
						}
					});
				});
			},
			
			// 处理搜索
			handleSearch() {
				// 已通过计算属性实现
			},
			
			// 显示详情
			showDetail(item) {
				console.log(item, "item")
				this.currentItem = {...item};
				this.showDetailModal = true;
			},
			
			// 关闭详情
			closeDetail() {
				this.showDetailModal = false;
			},
			
			//
			desensitizeIDCard(idCard) {
			  if (!idCard || idCard.length < 6) {
			    return '—';
			  }
			
			  // 中国大陆的身份证号码长度为18位
			  const idLength = idCard.length;
			  // 保留前6位和后4位，中间替换为星号
			  const prefix = idCard.substr(0, 4);
			  const suffix = idCard.substr(idLength - 4);
			  const middle = '*'.repeat(idLength - 8);
			
			  return prefix + middle + suffix;
			},
			
			
			// 格式化灾害类型
			formatDisasterType(type) {
				if (!type) return '未知灾害';
				
				const typeMap = {
					'earthquake': '地震',
					'flood': '洪水',
					'typhoon': '台风',
					'drought': '干旱',
					'fire': '火灾',
					'landslide': '滑坡',
					'other': '其他灾害'
				};
				return typeMap[type] || type;
			},
			
			// 格式化日期
			formatDate(timestamp) {
				if (!timestamp) return '未知';
				
				try {
					const date = new Date(timestamp);
					return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
				} catch (error) {
					console.error('格式化日期出错:', error);
					return '未知日期';
				}
			},
			
			// 格式化申请数据
			formatApplicationData(applications) {
				if (!Array.isArray(applications)) {
					console.error('应用数据不是数组:', applications);
					return [];
				}
				
				return applications.map(item => {
					if (!item) return null;
					
					return {
						id: item.id,
						reportId: item.reportId || `申请${item.id || '未知'}`,
						name: item.name || '未知申请人',
						disasterType: item.disasterType || 'other',
						disasterDate: item.disasterDate || '未知日期',
						disasterDesc: item.disasterDesc || '无灾情描述',
						estimatedLoss: item.estimatedLoss || 0,
						requestAmount: item.requestAmount || 0,
						approvedAmount: item.approvedAmount || item.requestAmount || 0,
						status: item.status,
						address: item.address,
						cardNo: this.desensitizeIDCard(item.cardNo),
						createTime: item.createTime,
						approveTime: item.approveTime || item.updateTime,
						approveRemark: item.approveRemark || item.remark || '已通过审批'
					};
				}).filter(item => item !== null);
			}
		}
	}
</script>

<style>
	.yangguang-container {
		padding: 30rpx;
		background-color: #F5F7FA;
		min-height: 100vh;
	}
	
	.header {
		margin-bottom: 30rpx;
	}
	
	.title {
		font-size: 36rpx;
		font-weight: bold;
		display: block;
		margin-bottom: 10rpx;
		color: #333;
	}
	
	.subtitle {
		font-size: 24rpx;
		color: #666;
	}
	
	/* 搜索框 */
	.search-box {
		height: 80rpx;
		background-color: #FFFFFF;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		margin-bottom: 30rpx;
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
	
	/* 公示列表 */
	.public-list {
		margin-bottom: 30rpx;
	}
	
	.public-item {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.public-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 15rpx;
		border-bottom: 1px solid #F0F0F0;
		padding-bottom: 15rpx;
	}
	
	.applicant-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}
	
	.public-status {
		font-size: 24rpx;
		color: #67C23A;
		background-color: rgba(103, 194, 58, 0.1);
		padding: 4rpx 16rpx;
		border-radius: 20rpx;
	}
	
	.public-info {
		display: flex;
		flex-wrap: wrap;
		margin-bottom: 15rpx;
		font-size: 28rpx;
		color: #666;
	}
	
	.disaster-type, .disaster-date {
		margin-right: 20rpx;
	}
	
	.aid-amount {
		font-weight: bold;
		color: #E6A23C;
	}
	
	.public-footer {
		display: flex;
		justify-content: space-between;
		font-size: 24rpx;
		color: #999;
	}
	
	/* 空状态 */
	.empty-state {
		padding: 100rpx 0;
		text-align: center;
		background-color: #FFFFFF;
		border-radius: 16rpx;
	}
	
	.empty-icon {
		width: 100rpx;
		height: 100rpx;
		line-height: 100rpx;
		text-align: center;
		background-color: #F0F0F0;
		color: #999;
		font-size: 60rpx;
		border-radius: 50%;
		margin: 0 auto 20rpx;
	}
	
	.empty-text {
		font-size: 32rpx;
		color: #333;
		display: block;
		margin-bottom: 10rpx;
	}
	
	.empty-desc {
		font-size: 26rpx;
		color: #999;
		display: block;
		margin-bottom: 30rpx;
	}
	
	.empty-action {
		display: inline-block;
		padding: 10rpx 40rpx;
		background-color: #1890FF;
		color: #FFFFFF;
		font-size: 28rpx;
		border-radius: 30rpx;
	}
	
	/* 详情弹窗 */
	.modal-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		z-index: 998;
	}
	
	.detail-modal {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 85%;
		max-height: 80vh;
		background-color: #FFFFFF;
		border-radius: 16rpx;
		z-index: 999;
		display: flex;
		flex-direction: column;
		overflow: hidden;
	}
	
	.modal-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1px solid #F0F0F0;
	}
	
	.modal-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}
	
	.modal-close {
		font-size: 36rpx;
		color: #999;
	}
	
	.modal-body {
		padding: 30rpx;
		max-height: 60vh;
	}
	
	.applicant-info {
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #F0F0F0;
	}
	
	.detail-item {
		margin-bottom: 20rpx;
		display: flex;
		align-items: flex-start;
	}
	
	.detail-item-label {
		width: 200rpx;
		font-size: 28rpx;
		color: #999;
	}
	
	.detail-item-value {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}
	
	.highlight .detail-item-value {
		color: #E6A23C;
		font-weight: bold;
	}
</style>
