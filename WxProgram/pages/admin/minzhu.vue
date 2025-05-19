<template>
	<view class="minzhu-container">
		<!-- 顶部区域 -->
		<view class="header-area">
			<view class="header">
				<view class="title-section">
					<text class="page-title">民主评议</text>
					<text class="subtitle">灾情救助申请公开评审</text>
				</view>
				<view class="user-info" v-if="isLoggedIn">
					<text class="user-name">{{ userName }}</text>
					<text class="user-role">{{ 
						userRole === 'admin' ? '管理员' : 
						userRole === 'reviewer' ? '评议员' : 
						'普通用户' 
					}}</text>
				</view>
			</view>
		</view>
		
		<!-- 未登录或非评议员提示 -->
		<view class="auth-notice" v-if="!isLoggedIn || !isEvaluator">
			<view class="notice-icon">!</view>
			<text class="notice-title">{{ !isLoggedIn ? '请先登录' : '权限不足' }}</text>
			<text class="notice-desc">{{ !isLoggedIn ? '您需要登录后才能参与民主评议' : '只有评议员才能参与灾情救助申请的民主评议' }}</text>
			<view class="notice-action" @tap="handleAuthAction">
				<text>{{ !isLoggedIn ? '立即登录' : '返回首页' }}</text>
			</view>
		</view>
		
		<!-- 评议内容区域 - 仅已登录评议员可见 -->
		<block v-if="isLoggedIn && isEvaluator">
			<!-- 筛选区域 -->
			<view class="filter-section">
				<view class="search-box">
					<input type="text" v-model="searchKeyword" placeholder="搜索申请人姓名或申请编号" @input="handleSearch" />
					<text class="search-icon">搜</text>
				</view>
				
				<view class="filter-tabs">
					<view class="filter-tab" 
						v-for="(tab, index) in filterTabs" 
						:key="index"
						:class="{ active: currentFilter === tab.value }"
						@tap="setFilter(tab.value)">
						<text>{{ tab.label }}</text>
					</view>
				</view>
			</view>
			
			<!-- 申请列表 -->
			<view class="application-list" v-if="filteredApplications.length > 0">
				<view class="application-item" v-for="(item, index) in filteredApplications" :key="item.id" @tap="selectApplication(item)">
					<view class="application-status" :class="'status-' + getEvaluationStatus(item)">
						<text>{{ getEvaluationStatusText(item) }}</text>
					</view>
					
					<view class="application-content">
						<view class="application-header">
							<text class="applicant-name">{{ item.name }}</text>
							<text class="application-id">编号: {{ item.reportId }}</text>
						</view>
						
						<view class="application-info">
							<text class="disaster-type">{{ formatDisasterType(item.disasterType) }}</text>
							<text class="disaster-date">{{ item.disasterDate }}</text>
							<text class="estimated-loss">损失: {{ item.estimatedLoss }}元</text>
						</view>
						
						<view class="application-footer">
							<text class="submit-time">提交时间: {{ formatDate(item.createTime) }}</text>
							<view class="score-summary" v-if="item.averageScore">
								<text class="score">{{ item.averageScore }}分</text>
								<text class="count">({{ item.evaluationCount || 0 }}人评)</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-else>
				<view class="empty-icon">!</view>
				<text class="empty-text">暂无{{ getFilterName(currentFilter) }}申请</text>
				<view class="empty-action" @tap="loadApplicationList">
					<text>刷新列表</text>
				</view>
			</view>
		</block>
		
		<!-- 评议弹窗 -->
		<view class="modal-overlay" v-if="showEvaluationModal" @tap="cancelEvaluation"></view>
		<view class="evaluation-modal" v-if="showEvaluationModal">
			<view class="modal-header">
				<text class="modal-title">民主评议</text>
				<text class="modal-close" @tap="cancelEvaluation">×</text>
			</view>
			
			<scroll-view class="modal-body" scroll-y="true">
				<!-- 申请人信息 -->
				<!-- 申请基本信息卡片 -->
				<view class="info-card">
					<view class="card-header">
						<text class="card-title">申请人信息</text>
					</view>
					
					<view class="applicant-summary">
						<!-- <view class="applicant-avatar">
							<text>{{ applicationData.name ? applicationData.name.substring(0, 1) : '?' }}</text>
						</view> -->
						<view class="applicant-info">
								<text class="label">姓名：</text>
							<text class="applicant-name">{{ currentApplication.name || '暂无' }}</text>
						</view>
						<view class="applicant-info">
								<text class="label">联系方式：</text>
							<text class="applicant-details">{{ currentApplication.phone || '暂无电话' }}</text>
						</view>
					</view>
					
					<view class="info-grid">
						<view class="info-item">
							<text class="label">身份证号：</text>
							<text class="value">{{ desensitizeIDCard(currentApplication.cardNo) || '暂无' }}</text>
						</view>
						<view class="info-item">
							<text class="label">家庭人口：</text>
							<text class="value">{{ currentApplication.familySize || '0' }} 人</text>
						</view>
						<view class="info-item full-width">
							<text class="label">家庭住址</text>
							<text class="value">{{ currentApplication.address || '暂无' }}</text>
						</view>
					</view>
				</view>
				
				<!-- 灾情简述 -->
				<view class="disaster-summary">
					<text class="summary-label">灾情概述</text>
					<text class="summary-date">发生日期：{{ currentApplication.disasterDate }}</text>
				</view>
				<view class="disaster-detail">
					<text class="disaster-label">{{currentApplication.disasterDesc}}</text>
					<view class="disaster-desc">
						<text class="disaster-title">经济损失：</text>
						<text class="disaster-value">{{currentApplication.estimatedLoss}}元</text>
					</view>
				</view>
				<view class="disaster-detail">
					<view class="disaster-desc">
						<text class="disaster-title">救助需求：</text>
						<text class="disaster-value">申请救助：{{currentApplication.requestAmount || '-'}}元</text>
					</view>
				</view>

				
				<!-- 评议意见 -->
				<view class="opinion-section" v-if="currentApplication.isComment !== 0">
					<text class="section-title">评议意见</text>
					<textarea class="opinion-input" v-model="evaluation.opinion" placeholder="请输入您的评议意见..."></textarea>
				</view>
				
			</scroll-view>
			
			<view class="modal-footer">
				<view class="modal-btn cancel-btn" @tap="cancelEvaluation">
					<text>取消</text>
				</view>
				<view class="modal-btn fault-btn" @tap="faultEvaluation" v-if="currentApplication.isComment !== 0">
					<text>驳回</text>
				</view>
				<view class="modal-btn confirm-btn" @tap="submitEvaluation" v-if="currentApplication.isComment !== 0">
					<text>提交评议</text>
				</view>
			</view>
		</view>
		
		<!-- 注意：在这里可以检查并直接调试用户角色 -->
		<!-- <view class="debug-info" v-if="process.env.NODE_ENV === 'development'" @tap="forceEvaluatorRole">
			<text>调试信息: 角色{{ userRole }}({{ isEvaluator ? '有评议权限' : '无评议权限' }})</text>
		</view> -->
	</view>
</template>

<script>
	import { baseUrl } from "@/utils/apiconfig.js";
	export default {
		data() {
			return {
				// 用户登录状态
				isLoggedIn: false,
				isEvaluator: false,
				userId: '',
				userName: '',
				evaluatorId: '',
				userRole: 'user', // 添加用户角色字段
				hasEvaluatedValue: false,
				// 过滤选项
				filterTabs: [
					{ label: '全部申请', value: 'all' },
					{ label: '待评议', value: 'pending' },
					{ label: '已评议', value: 'evaluated' }
				],
				currentFilter: 'pending', // 默认显示待评议
				
				// 搜索关键词
				searchKeyword: '',
				
				// 申请列表
				applications: [],
				
				// 分页参数
				pageNum: 1,
				pageSize: 10,
				total: 0,
				
				// 弹窗控制
				showEvaluationModal: false,
				
				// 当前选中的申请
				currentApplication: {},
				
				// 评议数据
				evaluation: {
					applicationId: null,
					truthScore: 0,
					severityScore: 0,
					necessityScore: 0,
					totalScore: 0,
					opinion: '',
					evaluatorId: '',
					evaluatorName: ''
				},
				
				// 历史评议
				evaluationHistory: [],
				
				// 添加悬停评分数据
				hoverScore: {
					truth: 0,
					severity: 0,
					necessity: 0
				},
				
				// 添加评分提示词
				scoreTips: {
					truth: ['', '不真实', '部分真实', '基本真实', '较为真实', '完全真实'],
					severity: ['', '影响轻微', '一般影响', '中度影响', '严重影响', '极其严重'],
					necessity: ['', '可不救助', '建议救助', '需要救助', '急需救助', '必须救助']
				},
				
				// 总分描述
				totalScoreDesc: [
					'请完成评分',
					'不建议救助',
					'可考虑救助',
					'建议优先救助',
					'强烈建议救助',
					'必须立即救助'
				]
			}
		},
		computed: {
			// 过滤后的申请列表 - 添加错误处理
			filteredApplications() {
				if (!this.applications || !this.applications.length) return [];
				
				try {
					let filtered = [...this.applications];
					
					// 根据当前筛选条件过滤
					if (this.currentFilter === 'pending') {
						filtered = filtered.filter(item => !this.hasEvaluated(item));
					} else if (this.currentFilter === 'evaluated') {
						filtered = filtered.filter(item => this.hasEvaluated(item));
					}
					
					// 根据搜索关键词过滤
					if (this.searchKeyword) {
						const keyword = this.searchKeyword.toLowerCase();
						filtered = filtered.filter(item => {
							return (item.name && item.name.toLowerCase().includes(keyword)) || 
								   (item.reportId && item.reportId.toLowerCase().includes(keyword));
						});
					}
					
					return filtered;
				} catch (error) {
					console.error('过滤申请列表时出错:', error);
					return [];
				}
			}
		},
		onShow() {
			// 检查用户登录状态
			this.checkLoginStatus();
			
			// 如果已登录并且是评议员，则加载数据
			if (this.isLoggedIn && this.isEvaluator) {
				this.loadApplicationList();
			}
		},
		// 添加下拉刷新事件
		onPullDownRefresh() {
			if (this.isLoggedIn && this.isEvaluator) {
				this.loadApplicationList().then(() => {
					uni.stopPullDownRefresh();
				}).catch(err => {
					console.error('刷新数据出错:', err);
					uni.stopPullDownRefresh();
				});
			} else {
				uni.stopPullDownRefresh();
			}
		},
		onReady() {
			// 强制刷新组件
			this.$forceUpdate();
		},
		methods: {
			// 强制设置为评议员角色（仅开发环境使用）
			forceEvaluatorRole() {
				this.userRole = 'reviewer';
				this.isEvaluator = true;
				
				// 同时更新本地存储
				let userInfo = uni.getStorageSync('user_info');
				if (Array.isArray(userInfo) && userInfo.length > 0) {
					userInfo = userInfo[0];
				} else if (!userInfo) {
					userInfo = {};
				}
				
				userInfo.userrole = 'reviewer';
				uni.setStorageSync('user_info', userInfo);
				
				uni.showToast({
					title: '已切换为评议员角色',
					icon: 'none'
				});
				
				// 重新加载数据
				this.loadApplicationList();
			},
			
			// 检查用户登录状态和评议员身份 - 完全重写
			checkLoginStatus() {
				try {
					const token = uni.getStorageSync('token');
					this.isLoggedIn = !!token;
					
					if (this.isLoggedIn) {
						// 获取用户信息
						let userInfo = uni.getStorageSync('user_info');
						
						// 处理不同格式的用户信息
						if (Array.isArray(userInfo) && userInfo.length > 0) {
							userInfo = userInfo[0];
						} else if (!userInfo) {
							userInfo = {};
						}
						
						console.log('原始用户信息:', userInfo);
						
						// 设置用户基本信息
						this.userId = userInfo.id || '';
						this.userName = userInfo.realName || '用户';
						this.evaluatorId = userInfo.id || '';
						
						// 直接获取角色信息 - 优先查找userrole字段
						const role = userInfo.userrole || userInfo.role || '';
						console.log('检测到的角色:', role);
						
						// 判断角色并设置评议权限
						if (role.toLowerCase() === 'reviewer' || role.toLowerCase() === 'admin') {
							this.userRole = role.toLowerCase();
							this.isEvaluator = true;
							console.log('用户拥有评议权限');
						} else {
							this.userRole = 'user';
							this.isEvaluator = false;
							console.log('用户没有评议权限');
						}
						
						// 如果用户有评议权限，立即加载数据
						if (this.isEvaluator) {
							this.loadApplicationList();
						}
					} else {
						this.isEvaluator = false;
						this.userId = '';
						this.userName = '';
						this.evaluatorId = '';
						this.userRole = 'user';
					}
				} catch (error) {
					console.error('检查登录状态出错:', error);
					this.isLoggedIn = false;
					this.isEvaluator = false;
					this.userRole = 'user';
				}
			},
			
			// 简化checkEvaluatorRole方法，只在必要时调用
			checkEvaluatorRole() {
				// 如果已经确定是评议员，就不再检查
				if (this.isEvaluator) {
					return;
				}
				
				// 如果不是评议员，可以尝试从API获取角色
				try {
					if (!baseUrl || !this.userId) {
						return;
					}
					
					uni.request({
						url: `${baseUrl}shebao/evaluation/check-role`,
						method: 'GET',
						header: {
							'Authorization': uni.getStorageSync('token') || ''
						},
						success: (res) => {
							console.log('角色检查响应:', res.data);
							
							if (res.statusCode === 200 && res.data.code === 200) {
								// 获取后端返回的角色
								const backendRole = res.data.data?.role || '';
								
								if (backendRole.toLowerCase() === 'reviewer' || backendRole.toLowerCase() === 'admin') {
									this.userRole = backendRole.toLowerCase();
									this.isEvaluator = true;
									
									// 更新本地存储
									let userInfo = uni.getStorageSync('user_info');
									if (Array.isArray(userInfo) && userInfo.length > 0) {
										userInfo = userInfo[0];
									} else if (!userInfo) {
										userInfo = {};
									}
									
									userInfo.userrole = backendRole;
									uni.setStorageSync('user_info', userInfo);
									
									// 加载申请列表
									this.loadApplicationList();
								}
							}
						}
					});
				} catch (error) {
					console.error('检查评议员角色出错:', error);
				}
			},
			
			// 处理认证相关操作
			handleAuthAction() {
				if (!this.isLoggedIn) {
					// 跳转到登录页面
					uni.navigateTo({
						url: '/pages/login/login'
					});
				} else {
					// 返回首页
					uni.switchTab({
						url: '/pages/index/index'
					});
				}
			},
			
			// 加载用户信息到评议表单
			loadUserInfoToEvaluation() {
				try {
					this.evaluation.evaluatorId = this.evaluatorId || this.userId;
					this.evaluation.evaluatorName = this.userName;
				} catch (error) {
					console.error('加载用户信息到评议表单出错:', error);
				}
			},
			
			// 加载申请列表 - 添加错误处理和改进
			loadApplicationList() {
				if (!this.isLoggedIn || !this.isEvaluator) {
					return Promise.resolve();
				}
				
				uni.showLoading({
					title: '加载中...'
				});
				
				return new Promise((resolve, reject) => {
					try {
						// 从本地存储获取API基地址
						// 增加baseUrl定义（从本地存储获取）
					if (!baseUrl) {
					  console.error('baseUrl未配置');
					  uni.showToast({ title: '服务器配置错误', icon: 'none' });
					  return;
					}
						if (!baseUrl) {
							uni.hideLoading();
							// uni.showToast({
							// 	title: 'API地址未配置',
							// 	icon: 'none'
							// });
							// 使用模拟数据
							this.loadMockData();
							resolve();
							return;
						}
						
						// 构建请求参数
						const requestData = {
							pageNum: this.pageNum,
							pageSize: this.pageSize,
							status: 3,  // 只加载已通过审批的申请
							userId: this.evaluatorId || this.userId // 添加评议员ID
						};
						
						console.log('请求参数:', requestData);
						
						// 发送请求获取申请列表
						uni.request({
							url: `${baseUrl}shebao/commentSet`,
							method: 'POST',
							data: requestData,
							header: {
								'content-type': 'application/json',
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
								
								console.log('获取评议申请列表响应:', res.data);
								
								// 检查响应数据
								if (res.data && res.data.code === 200) {
									// 处理返回的数据
									let applications = [];

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
									
									if (applications.length > 0) {
										this.applications = this.formatApplicationData(applications);
										this.total = Array.isArray(res.data.data) ? applications.length : 
											(res.data.data.total || applications.length);
										
										// 加载完申请列表后，检查每个申请的评议状态
										// this.checkEvaluationStatus();
									} else {
										// 如果返回空数据，显示提示
										uni.showToast({
											title: '暂无评议申请数据',
											icon: 'none'
										});
										this.applications = [];
										this.total = 0;
									}
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
					} catch (error) {
						uni.hideLoading();
						console.error('加载申请列表出错:', error);
						this.loadMockData();
						resolve();
					}
				});
			},
			
			// 设置过滤条件
			setFilter(filter) {
				if (this.currentFilter === filter) return;
				
				this.currentFilter = filter;
				this.pageNum = 1; // 重置页码
			},
			
			// 搜索处理
			handleSearch() {
				this.pageNum = 1; // 重置页码
			},
			
			// 选择申请进行评议 - 添加错误处理和权限检查
			selectApplication(application) {
				try {
					if (!this.isLoggedIn || !this.isEvaluator) {
						uni.showToast({
							title: '您没有评议权限',
							icon: 'none'
						});
						return;
					}
					
					if (!application || !application.id) {
						uni.showToast({
							title: '申请数据无效',
							icon: 'none'
						});
						return;
					}
					
					this.currentApplication = JSON.parse(JSON.stringify(application));
					
					// // 重置评议数据
					// this.resetEvaluation();
					
					// // 加载历史评议
					// this.loadEvaluationHistory(application.id);
					
					// 显示评议弹窗
					this.showEvaluationModal = true;
				} catch (error) {
					console.error('选择申请出错:', error);
					uni.showToast({
						title: '操作失败',
						icon: 'none'
					});
				}
			},
			
			// 重置评议数据 - 加入评议员ID
			resetEvaluation() {
				this.evaluation = {
					applicationId: this.currentApplication.id,
					truthScore: 0,
					severityScore: 0,
					necessityScore: 0,
					totalScore: 0,
					opinion: '',
					evaluatorId: this.evaluatorId || this.userId,
					evaluatorName: this.userName
				};
			},
			
			// 加载评议历史 - 改进错误处理
			loadEvaluationHistory(applicationId, checkOnly = false) {
				if (!applicationId) {
					this.evaluationHistory = [];
					return;
				}
				
				// 增加baseUrl定义（从本地存储获取）

					if (!baseUrl) {
					  console.error('baseUrl未配置');
					  uni.showToast({ title: '服务器配置错误', icon: 'none' });
					  return;
					}
					if (!baseUrl) {
					// 使用模拟数据
					this.evaluationHistory = this.generateMockEvaluationHistory();
					return;
				}
				
				uni.request({
					url: `${baseUrl}shebao/evaluation/history/${applicationId}`,
					method: 'GET',
					header: {
						'Authorization': uni.getStorageSync('token') || ''
					},
					success: (res) => {
						try {
							if (res.statusCode === 200 && res.data.code === 200 && res.data.data) {
								if (Array.isArray(res.data.data)) {
									// 如果只是检查状态，则不需要存储历史记录
									if (!checkOnly) {
										this.evaluationHistory = res.data.data;
									}
									
									// 检查当前用户是否已评议
									const userEvaluation = res.data.data.find(
										item => item.evaluatorId == (this.evaluatorId || this.userId)
									);
									if (userEvaluation) {
										// 更新当前申请的评议状态
										this.updateApplicationEvaluationStatus(applicationId, true);
									}
								} else {
									console.error('评议历史数据格式不正确:', res.data);
									this.evaluationHistory = [];
								}
							} else {
								console.error('获取评议历史失败:', res.data);
								this.evaluationHistory = this.generateMockEvaluationHistory();
							}
						} catch (error) {
							console.error('处理评议历史数据出错:', error);
							this.evaluationHistory = [];
						}
					},
					fail: (err) => {
						console.error('请求评议历史失败:', err);
						this.evaluationHistory = this.generateMockEvaluationHistory();
					}
				});
			},
			
			// 更新申请的评议状态
			updateApplicationEvaluationStatus(applicationId, hasEvaluated) {
				const index = this.applications.findIndex(item => item.id === applicationId);
				console.log(index, "index")
				if (index !== -1) {
					this.applications[index].hasEvaluated = hasEvaluated;
				}
			},
			
			// 设置真实性评分
			setTruthScore(score) {
				this.evaluation.truthScore = score;
			},
			
			// 设置严重性评分
			setSeverityScore(score) {
				this.evaluation.severityScore = score;
			},
			
			// 设置必要性评分  
			setNecessityScore(score) {
				this.evaluation.necessityScore = score;
			},
			
			// 计算总分 - 添加错误处理
			calculateTotalScore() {
				try {
					const { truthScore, severityScore, necessityScore } = this.evaluation;
					if (typeof truthScore !== 'number' || typeof severityScore !== 'number' || typeof necessityScore !== 'number') {
						return '0.0';
					}
					const totalScore = (truthScore + severityScore + necessityScore) / 3;
					return totalScore.toFixed(1);
				} catch (error) {
					console.error('计算总分出错:', error);
					return '0.0';
				}
			},
			
			// 判断评议是否有效
			isEvaluationValid() {
				try {
					if (!this.isLoggedIn || !this.isEvaluator) return false;
					
					const { truthScore, severityScore, necessityScore } = this.evaluation;
					return truthScore > 0 && severityScore > 0 && necessityScore > 0;
				} catch (error) {
					console.error('检查评议有效性出错:', error);
					return false;
				}
			},
			
			// 取消评议
			cancelEvaluation() {
				this.showEvaluationModal = false;
			},
			
			//驳回
			faultEvaluation() {
				if (!this.currentApplication && this.currentApplication.id) {
					uni.showToast({
						title: '申请ID无效',
						icon: 'none'
					});
					return;
				}
				
				uni.showLoading({
					title: '提交中...'
				});
				
				if (!baseUrl) {
					uni.hideLoading();
					// uni.showToast({
					// 	title: 'API地址未配置',
					// 	icon: 'none'
					// });
					return;
				}
				let userInfo = uni.getStorageSync('user_info');
				if (Array.isArray(userInfo) && userInfo.length > 0) {
					userInfo = userInfo[0];
				} else if (!userInfo) {
					userInfo = {};
				}
				// 根据具体需求准备提交数据
				const approvalData = {
					id:this.currentApplication.id,
					userId: userInfo.id,
					status: 2, // 3是审核通过，2是驳回
					auditRemark:this.evaluation.opinion,
					flagComment: 1
				};
				
				console.log('提交审批数据:', approvalData);
				
				
				// 发送审批请求
				uni.request({
					url: `${baseUrl}list/update`,
					method: 'POST',
					data: approvalData,
					header: {
						'content-type': 'application/json',
						'Authorization': uni.getStorageSync('token') || ''
					},
					success: (res) => {
						uni.hideLoading();
						
						if (res.statusCode === 200 && res.data.code === 200) {
							uni.showToast({
								title: '审批操作成功',
								icon: 'success'
							});
							
							// 关闭确认窗口
							this.showConfirm = false;
							
							this.loadApplicationList()
							
							// 延迟返回
							setTimeout(() => {
								uni.navigateBack();
							}, 1500);
						} else {
							uni.showToast({
								title: res.data.msg || '审批操作失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						uni.hideLoading();
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
						console.error('审批请求失败:', err);
					}
				});
			},
			
			// 提交评议 - 添加错误处理和权限检查
			submitEvaluation() {
				try {
					if (!this.isLoggedIn || !this.isEvaluator) {
						uni.showToast({
							title: '您没有评议权限',
							icon: 'none'
						});
						return;
					}
					
					// if (!this.isEvaluationValid()) {
					// 	uni.showToast({
					// 		title: '请完成所有评分项',
					// 		icon: 'none'
					// 	});
					// 	return;
					// }
					
					uni.showLoading({
						title: '提交中...'
					});
					
					// 增加baseUrl定义（从本地存储获取）
					if (!baseUrl) {
					  console.error('baseUrl未配置');
					  uni.showToast({ title: '服务器配置错误', icon: 'none' });
					  return;
					}
					if (!baseUrl) {
						uni.hideLoading();
						// uni.showToast({
						// 	title: 'API地址未配置',
						// 	icon: 'none'
						// });
						return;
					}
					
					// 计算总分并更新
					this.evaluation.totalScore = parseFloat(this.calculateTotalScore());
					
					// 确保必要字段存在
					const submitData = {
						...this.evaluation,
						applicationId: this.evaluation.applicationId || this.currentApplication.id,
						evaluatorId: this.evaluation.evaluatorId || this.evaluatorId,
						evaluatorName: this.evaluation.evaluatorName || this.userName
					};
					
					console.log('提交评议数据:', submitData);
					
					// 发送评议请求
					uni.request({
						url: `${baseUrl}shebao/evaluation/add`,
						method: 'POST',
						data: submitData,
						header: {
							'content-type': 'application/json',
							'Authorization': uni.getStorageSync('token') || ''
						},
						success: (res) => {
							uni.hideLoading();
							
							if (res.statusCode === 200 && res.data.code === 200) {
								uni.showToast({
									title: '评议提交成功',
									icon: 'success'
								});
								
								// 立即将当前申请标记为已评议
								this.updateApplicationEvaluationStatus(this.currentApplication.id, true);
								
								// 更新本地数据
								this.updateLocalEvaluation();
								
								// 关闭弹窗
								this.showEvaluationModal = false;
								
								// 刷新列表
								this.loadApplicationList();
							} else {
								uni.showToast({
									title: res.data.msg || '评议提交失败',
									icon: 'none'
								});
							}
						},
						fail: (err) => {
							uni.hideLoading();
							uni.showToast({
								title: '网络请求失败',
								icon: 'none'
							});
							console.error('评议提交失败:', err);
						}
					});
				} catch (error) {
					uni.hideLoading();
					console.error('提交评议出错:', error);
					uni.showToast({
						title: '操作失败，请重试',
						icon: 'none'
					});
				}
			},
			
			// 更新本地评议数据 - 添加错误处理
			updateLocalEvaluation() {
				try {
					// 添加到历史评议
					const newEvaluation = {
						...this.evaluation,
						evaluationTime: new Date().getTime()
					};
					
					this.evaluationHistory.unshift(newEvaluation);
					
					// 更新申请列表中的评分
					const index = this.applications.findIndex(item => item.id === this.currentApplication.id);
					if (index !== -1) {
						// 计算新平均分
						const app = this.applications[index];
						const count = (app.evaluationCount || 0) + 1;
						const totalScore = (app.averageScore || 0) * (count - 1) + this.evaluation.totalScore;
						const averageScore = (totalScore / count).toFixed(1);
						
						// 更新数据
						this.applications[index] = {
							...app,
							evaluationCount: count,
							averageScore: averageScore,
							hasEvaluated: true
						};
					}
				} catch (error) {
					console.error('更新本地评议数据出错:', error);
				}
			},
			
			// 判断是否已评议 - 添加错误处理
			hasEvaluated(application) {
				try {
					return application && application.hasEvaluated === true;
				} catch (error) {
					console.error('检查是否已评议出错:', error);
					return false;
				}
			},
			
			// 获取评议状态
			getEvaluationStatus(application) {
				if (application.isComment === 0) {
					return 'evaluated';
				} else {
					return 'pending';
				}
			},
			
			// 获取评议状态文本
			getEvaluationStatusText(application) {
				if (application.isComment === 0) {
					return '已评议';
				} else if(application.isComment ===  1) {
					return '待评议';
				} else {
					return "未知状态"
				}

			},
			
			// 获取过滤名称
			getFilterName(filter) {
				const filterMap = {
					'all': '相关',
					'pending': '待评议',
					'evaluated': '已评议'
				};
				return filterMap[filter] || '相关';
			},
			
			// 格式化灾害类型
			formatDisasterType(type) {
				try {
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
				} catch (error) {
					console.error('格式化灾害类型出错:', error);
					return '未知灾害';
				}
			},
			
			// 格式化申请数据 - 添加错误处理
			formatApplicationData(applications) {
				console.log(applications)
				try {
					if (!Array.isArray(applications)) {
						console.error('应用数据不是数组:', applications);
						return [];
					}
					
					return applications.map(item => {
						try {
							if (!item) return null;
							
							return {
								id: item.id,
								reportId: item.reportId || `申请${item.id || '未知'}`,
								name: item.name || '未知申请人',
								disasterType: item.disasterType || 'other',
								disasterDate: item.disasterDate || '未知日期',
								disasterDesc: item.disasterDesc || '无灾情描述',
								estimatedLoss: item.estimatedLoss || 0,
								status: item.status,
								createTime: item.createTime,
								evaluationCount: item.evaluationCount || 0,
								averageScore: item.averageScore || 0,
								hasEvaluated: item.hasEvaluated || false,
								phone: item.phone,
								cardNo: this.desensitizeIDCard(item.cardNo),
								familySize: item.familySize,
								address: item.address,
								requestAmount: item.requestAmount,
								isComment:item.isComment
								
							};
						} catch (error) {
							console.error('格式化单条申请数据出错:', error);
							return null;
						}
					}).filter(item => item !== null);
				} catch (error) {
					console.error('格式化申请数据出错:', error);
					return [];
				}
			},
			
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
			
			// 格式化日期 - 添加错误处理
			formatDate(timestamp) {
				try {
					if (!timestamp) return '未知';
					
					const date = new Date(timestamp);
					if (isNaN(date.getTime())) return '未知日期';
					
					return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
				} catch (error) {
					console.error('格式化日期出错:', error);
					return '未知日期';
				}
			},
			
			// 加载模拟数据
			loadMockData() {
				try {
					// 创建10条模拟申请数据
					const mockData = [];
					
					for (let i = 1; i <= 10; i++) {
						const createTime = Date.now() - (i * 24 * 60 * 60 * 1000); // 每条记录间隔一天
						const hasEvaluated = i % 3 === 0;
						
						mockData.push({
							id: i,
							reportId: 'DR' + (10000 + i),
							name: ['张三', '李四', '王五', '赵六', '钱七'][i % 5],
							disasterType: ['earthquake', 'flood', 'typhoon', 'fire', 'other'][i % 5],
							disasterDate: this.formatDate(createTime - (5 * 24 * 60 * 60 * 1000)).split(' ')[0],
							disasterDesc: '受灾情况描述，这里是详细描述内容，描述受灾的具体情况和受损程度...',
							estimatedLoss: (i * 10000) + 5000,
							status: 3,
							createTime: createTime,
							evaluationCount: hasEvaluated ? Math.floor(Math.random() * 5) + 1 : 0,
							averageScore: hasEvaluated ? (Math.random() * 2 + 3).toFixed(1) : 0,
							hasEvaluated: hasEvaluated
						});
					}
					
					this.applications = mockData;
					this.total = mockData.length;
				} catch (error) {
					console.error('加载模拟数据出错:', error);
					this.applications = [];
					this.total = 0;
				}
			},
			
			// 生成模拟评议历史
			generateMockEvaluationHistory() {
				try {
					const history = [];
					const reviewers = ['王评委', '李评委', '张评委', '赵评委'];
					
					for (let i = 0; i < Math.floor(Math.random() * 4) + 1; i++) {
						const time = Date.now() - (i * 12 * 60 * 60 * 1000); // 每条记录间隔12小时
						
						history.push({
							evaluatorId: i + 1,
							evaluatorName: reviewers[i % reviewers.length],
							applicationId: this.currentApplication.id || 0,
							truthScore: Math.floor(Math.random() * 2) + 3,
							severityScore: Math.floor(Math.random() * 2) + 3,
							necessityScore: Math.floor(Math.random() * 2) + 3,
							totalScore: (Math.random() * 2 + 3).toFixed(1),
							opinion: ['情况属实，确需救助', '受灾情况严重，应当优先考虑', '基本情况符合救助条件', '建议给予适当救助'][i % 4],
							evaluationTime: time
						});
					}
					
					return history;
				} catch (error) {
					console.error('生成模拟评议历史出错:', error);
					return [];
				}
			},
			
			// 设置悬停评分
			setHoverScore(type, score) {
				this.hoverScore[type] = score;
			},
			
			// 清除悬停评分
			clearHoverScore(type) {
				this.hoverScore[type] = 0;
			},
			
			// 获取评分提示
			getScoreTips(type, score) {
				if (!score) return '请评分';
				return this.scoreTips[type][score] || '';
			},
			
			// 获取总分描述
			getTotalScoreDesc() {
				const totalScore = parseFloat(this.calculateTotalScore());
				if (isNaN(totalScore) || totalScore <= 0) return this.totalScoreDesc[0];
				if (totalScore < 2) return this.totalScoreDesc[1];
				if (totalScore < 3) return this.totalScoreDesc[2];
				if (totalScore < 4) return this.totalScoreDesc[3];
				if (totalScore < 4.5) return this.totalScoreDesc[4];
				return this.totalScoreDesc[5];
			},
			
			// 添加检查评议状态的方法
			checkEvaluationStatus() {
				// 如果没有申请或未登录，则不检查
				if (!this.applications.length || !this.isLoggedIn || !this.isEvaluator) {
					return;
				}
				
				const evaluatorId = this.evaluatorId || this.userId;
				
				if (!baseUrl || !evaluatorId) {
					return;
				}
				
				// 获取用户已评议的申请列表
				uni.request({
					url: `${baseUrl}shebao/evaluation/user-evaluated/${evaluatorId}`,
					method: 'GET',
					header: {
						'Authorization': uni.getStorageSync('token') || ''
					},
					success: (res) => {
						if (res.statusCode === 200 && res.data.code === 200 && Array.isArray(res.data.data)) {
							const evaluatedIds = res.data.data.map(item => item.applicationId || item.id);
							
							// 更新应用列表中的评议状态
							this.applications.forEach((app, index) => {
								if (evaluatedIds.includes(app.id)) {
									this.applications[index].hasEvaluated = true;
								}
							});
						}
					}
				});
				
				// 如果后端没有提供此API，也可以单独查询每个申请的评议历史
				/*
				this.applications.forEach(app => {
					this.loadEvaluationHistory(app.id, true);
				});
				*/
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #F5F7FA;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
	}

	.minzhu-container {
		padding-bottom: 30rpx;
	}

	/* 顶部区域样式 */
	.header-area {
		background: linear-gradient(135deg, #1890FF, #36CBCB);
		padding: 40rpx 30rpx 20rpx;
		color: #FFFFFF;
		box-shadow: 0 4rpx 20rpx rgba(24, 144, 255, 0.2);
	}

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
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

	/* 筛选区域样式 */
	.filter-section {
		background-color: #FFFFFF;
		padding: 20rpx 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	/* 搜索框 */
	.search-box {
		height: 80rpx;
		background-color: #F5F7FA;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		margin-bottom: 20rpx;
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

	/* 过滤选项卡 */
	.filter-tabs {
		display: flex;
		margin: 0 -10rpx;
	}

	.filter-tab {
		padding: 12rpx 24rpx;
		margin: 0 10rpx;
		font-size: 26rpx;
		border-radius: 6rpx;
		background-color: #F5F7FA;
		color: #666666;
		transition: all 0.3s;
		text-align: center;
		flex: 1;
	}

	.filter-tab.active {
		background-color: #E6F7FF;
		color: #1890FF;
		font-weight: bold;
	}

	/* 申请列表样式 */
	.application-list {
		padding: 30rpx;
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

	.status-pending {
		background-color: #E6A23C;
	}

	.status-evaluated {
		background-color: #67C23A;
	}

	.application-content {
		padding: 20rpx;
	}

	.application-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.applicant-name {
		font-size: 28rpx;
		font-weight: bold;
	}

	.application-id {
		font-size: 24rpx;
		color: #999999;
	}

	.application-info {
		margin-bottom: 10rpx;
	}

	.disaster-type {
		font-size: 24rpx;
		color: #666666;
	}

	.disaster-date {
		font-size: 24rpx;
		color: #999999;
	}

	.estimated-loss {
		font-size: 24rpx;
		color: #999999;
	}

	.application-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.submit-time {
		font-size: 24rpx;
		color: #999999;
	}

	.score-summary {
		font-size: 24rpx;
		color: #666666;
	}

	.score {
		font-weight: bold;
	}

	.count {
		margin-left: 10rpx;
	}

	/* 评议弹窗样式 */
	.modal-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.5);
		z-index: 9997;
	}

	.evaluation-modal {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 80%;
		max-width: 600rpx;
		max-height: 85vh;
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 40rpx;
		box-shadow: 0 2rpx 20rpx rgba(0, 0, 0, 0.1);
		z-index: 9998;
		display: flex;
		flex-direction: column;
	}

	.modal-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 10rpx;
		border-bottom: 1px solid #EBEEF5;
	}

	.modal-title {
		font-size: 36rpx;
		font-weight: bold;
	}

	.modal-close {
		font-size: 28rpx;
		color: #999999;
		padding: 10rpx;
	}

	.modal-body {
		max-height: 60vh;
		flex: 1;
		overflow-y: auto;
	}
	
	.modal-footer {
		display: flex;
		justify-content: space-between;
		margin-top: 20rpx;
		padding-top: 20rpx;
		border-top: 1px solid #EBEEF5;
	}

	.modal-btn {
		flex: 1;
		height: 80rpx;
		border-radius: 40rpx;
		font-size: 28rpx;
		font-weight: bold;
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 0 10rpx;
	}

	.cancel-btn {
		background-color: #F5F7FA;
		color: #606266;
	}

	.confirm-btn {
		background-color: #1890FF;
		color: #FFFFFF;
	}
	.fault-btn {
		background-color: #ff5500;
		color: #FFFFFF;
	}

	.disabled {
		background-color: #F5F7FA;
		color: #909399;
		opacity: 0.7;
	}
	
	/* 添加缺失的样式 */
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
	
	/* 修复状态样式 */
	.status-pending {
		background-color: #E6A23C;
	}

	.status-evaluated {
		background-color: #67C23A;
	}
	
	/* 修复弹窗样式 */
	.modal-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.5);
		z-index: 9997;
	}

	.evaluation-modal {
		position: fixed;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 80%;
		max-width: 600rpx;
		max-height: 85vh;
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 40rpx;
		box-shadow: 0 2rpx 20rpx rgba(0, 0, 0, 0.1);
		z-index: 9998;
		display: flex;
		flex-direction: column;
	}

	.modal-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 10rpx;
		border-bottom: 1px solid #EBEEF5;
	}

	.modal-title {
		font-size: 36rpx;
		font-weight: bold;
	}

	.modal-close {
		font-size: 28rpx;
		color: #999999;
		padding: 10rpx;
	}

	.modal-body {
		max-height: 60vh;
		flex: 1;
		overflow-y: auto;
	}
	
	.modal-footer {
		display: flex;
		justify-content: space-between;
		margin-top: 20rpx;
		padding-top: 20rpx;
		border-top: 1px solid #EBEEF5;
	}

	.modal-btn {
		flex: 1;
		height: 80rpx;
		border-radius: 40rpx;
		font-size: 28rpx;
		font-weight: bold;
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 0 10rpx;
	}

	.cancel-btn {
		background-color: #F5F7FA;
		color: #606266;
	}

	.confirm-btn {
		background-color: #1890FF;
		color: #FFFFFF;
	}

	.disabled {
		background-color: #F5F7FA;
		color: #909399;
		opacity: 0.7;
	}
	
	/* 优化评分区域样式 */
	.score-section {
		background-color: #F8FAFF;
		padding: 20rpx;
		border-radius: 12rpx;
		margin-bottom: 30rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 10rpx;
		display: block;
	}

	.section-desc {
		font-size: 24rpx;
		color: #909399;
		margin-bottom: 20rpx;
		display: block;
	}

	.score-items {
		margin-bottom: 20rpx;
	}

	.score-item {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		padding: 15rpx;
		background: white;
		border-radius: 8rpx;
		box-shadow: 0 2rpx 5rpx rgba(0, 0, 0, 0.05);
	}

	.score-label {
		width: 150rpx;
		font-size: 28rpx;
		color: #333333;
		font-weight: bold;
	}

	.score-stars-container {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.score-stars {
		display: flex;
		align-items: center;
	}

	.star {
		font-size: 40rpx;
		color: #DCDFE6;
		margin-right: 10rpx;
		transition: transform 0.2s ease;
	}

	.star.active {
		color: #FFBA00;
		transform: scale(1.1);
	}

	.star.hover {
		color: #FFD980;
	}

	.score-tips {
		font-size: 22rpx;
		color: #909399;
		margin-top: 8rpx;
		min-height: 22rpx;
	}

	.score-value {
		font-size: 36rpx;
		color: #1890FF;
		font-weight: bold;
		margin-right: 20rpx;
	}

	.total-score {
		background-color: #E6F7FF;
		padding: 15rpx 20rpx;
		border-radius: 8rpx;
		margin-top: 20rpx;
		display: flex;
		align-items: center;
	}

	.total-label {
		font-size: 28rpx;
		color: #333333;
		font-weight: bold;
		margin-right: 20rpx;
	}

	.total-value {
		font-size: 36rpx;
		color: #1890FF;
		font-weight: bold;
	}

	.total-desc {
		flex: 1;
		font-size: 26rpx;
		color: #909399;
	}

	.opinion-section {
		margin-bottom: 30rpx;
	}

	.opinion-input {
		width: 100%;
		height: 180rpx;
		background-color: #FFFFFF;
		border: 1px solid #EBEEF5;
		border-radius: 8rpx;
	}

	.history-section {
		margin-bottom: 30rpx;
	}

	.history-item {
		background-color: #FFFFFF;
		border-radius: 8rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.history-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.evaluator {
		font-size: 26rpx;
		font-weight: bold;
		color: #333333;
	}

	.history-time {
		font-size: 24rpx;
		color: #909399;
	}

	.history-score {
		font-size: 26rpx;
		color: #1890FF;
		font-weight: bold;
	}

	.history-opinion {
		font-size: 26rpx;
		color: #606266;
		line-height: 1.5;
	}
	.applicant-summary {
		.applicant-title {
			font-size: 30rpx;
			font-weight: 600;
			border-bottom: 1px solid #EBEEF5;
			padding-bottom: 10rpx;
		}
	}
	
	.info-card {
		background-color: #FFFFFF;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}
	
	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 16rpx;
		border-bottom: 1rpx solid #F0F0F0;
	}
	
	.card-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333333;
	}
	/* 基本信息网格 */
	.info-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}
	
	.info-item {
		display: flex;
		flex-direction: column;
	}
	
	.label {
		font-size: 24rpx;
		color: #909399;
		margin-bottom: 8rpx;
	}
	
	.value {
		font-size: 28rpx;
		color: #333333;
		word-break: break-all;
	}
	.disaster-summary {
		display: flex;
		justify-content: space-between;
		margin-bottom: 20rpx;
		padding-bottom: 10rpx;
		border-bottom: 1rpx solid #F0F0F0;
		.summary-date {
			color: #909399;
			font-size: 24rpx;
		}
	}
	.disaster-detail {
		padding: 10rpx 0;
		.disaster-label {
			font-size: 24rpx;
			color: #909399;
		}
		.disaster-desc {
			display: flex;
			justify-content: space-between;
			padding-bottom: 10rpx;
			margin-bottom: 20rpx;
			border-bottom: 1rpx solid #F0F0F0;
			.disaster-title {
				font-size: 28rpx;
				color: #333333;
				font-weight: bold;
			}
			.disaster-value {
				font-size: 28rpx;
				color: #da0000;
				font-weight: bold;
			}
		}
	}
</style>
